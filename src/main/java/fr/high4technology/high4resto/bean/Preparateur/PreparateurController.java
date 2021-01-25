package fr.high4technology.high4resto.bean.Preparateur;

import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.high4technology.high4resto.Util.Util;
import fr.high4technology.high4resto.WebSocket.ServerCanalHandler;
import fr.high4technology.high4resto.bean.Audio.Audio;
import fr.high4technology.high4resto.bean.Audio.AudioRepository;
import fr.high4technology.high4resto.bean.Struct.Message;
import fr.high4technology.high4resto.bean.Tracability.Order.Order;
import fr.high4technology.high4resto.bean.Tracability.Order.OrderRepository;
import fr.high4technology.high4resto.bean.Tracability.Prepare.Prepare;
import fr.high4technology.high4resto.bean.Tracability.Prepare.PrepareRepository;
import fr.high4technology.high4resto.bean.Tracability.toPrepare.ToPrepare;
import fr.high4technology.high4resto.bean.Tracability.toPrepare.ToPrepareRepository;
import fr.high4technology.high4resto.Util.Variable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/"+Variable.apiPath+"/preparateur")
@RequiredArgsConstructor
@Slf4j
public class PreparateurController {

    VoiceSelectionParams voice = VoiceSelectionParams.newBuilder().setLanguageCode("fr-FR")
    .setSsmlGender(SsmlVoiceGender.MALE).build();

    @Autowired
    private OrderRepository orders;
    @Autowired
    private ServerCanalHandler serverCanal;
    @Autowired
    private ToPrepareRepository toPrepares;
    @Autowired
    private PrepareRepository prepares;
    @Autowired
    private AudioRepository audios;

    private final ReactiveGridFsTemplate gridFsTemplate;

    @PutMapping("/speak/")
    public Mono<Audio> speak(@RequestBody Audio audio)
    {
        return this.audios.findById(Util.hash(audio.getText()))
        .switchIfEmpty(Mono.just("").flatMap(i -> {
            log.warn("Audio n'existe pas je le génère");
            String fileName = Util.randomIdentifier() + ".mp3";
            try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
                SynthesisInput input = SynthesisInput.newBuilder().setText(audio.getText()).build();
                AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();
                SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice,
                        audioConfig);
                ByteString audioContents = response.getAudioContent();
                DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
                DefaultDataBuffer finalFile;
                finalFile = factory.wrap(audioContents.toByteArray());
                return this.gridFsTemplate.store(Flux.just(finalFile), fileName);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
            return Mono.empty();
        }).flatMap(id->{
            audio.setGridId(id.toHexString());
            audio.setId(Util.hash(audio.getText()));
            return this.audios.save(audio);
        }))
        .switchIfEmpty(Mono.just(Audio.builder().gridId("error").build()));
    }

    @PutMapping("/moveToPrepare/")
    Mono<ToPrepare> moveToTake(@RequestBody ToPrepare toPrepare)
    {
        toPrepare.setInside(Util.getTimeNow());
        toPrepare.setId(toPrepare.getOrder().getId());
        return this.orders.deleteById(toPrepare.getOrder().getId()).then(toPrepares.save(toPrepare));
    }

    @PutMapping("/moveToPrepared/")
    Mono<Prepare> moveToPrepared(@RequestBody Prepare prepare)
    {
        prepare.setInside(Util.getTimeNow());
        prepare.setId(prepare.getToPrepare().getId());
        return this.toPrepares.deleteById(prepare.getToPrepare().getId()).then(this.prepares.save(prepare))
        .flatMap(prep->{
            return Mono.just(prep);
        });
    }

    @PutMapping("/callServer/")
    Mono<Message> callServer(@RequestBody Message message)
    {
        this.serverCanal.sendMessage(message.getType()+":"+message.getFunction());
        return Mono.just(message);
    }

    @GetMapping("/findToPrepare/{role}")
    public Flux<ToPrepare> getToPrepare(@PathVariable String role){
        return this.toPrepares.findAll()
        .filter(toPrepare->toPrepare.getOrder().getPreOrder().getStock().getItem().getRoles().contains(role)).sort((a,b)->{
            return a.getOrder().getPreOrder().getDestination().compareTo(b.getOrder().getPreOrder().getDestination());
        }).sort((a,b)->{
            int aa=a.getOrder().getPreOrder().getStock().getItem().getCategorie().getOrder();
            int bb=b.getOrder().getPreOrder().getStock().getItem().getCategorie().getOrder();
            if(aa>bb)
            return 1;
            else if(aa<bb)
            return -1;
            else return 0;
        }).sort((a,b)->{
            return a.getOrder().getPreOrder().getStock().getItem().getName().compareTo(b.getOrder().getPreOrder().getStock().getItem().getName());
        });
    }

    @GetMapping("/findSignalOrder/{role}")
    public Flux<Order> getSignalOrder(@PathVariable String role){
        return this.orders.findAll().filter(order->!order.isToTake())
        .filter(order->order.getPreOrder().getStock().getItem().getRoles().contains(role)).sort((a,b)->{
            return a.getPreOrder().getDestination().compareTo(b.getPreOrder().getDestination());
        }).sort((a,b)->{
            int aa=a.getPreOrder().getStock().getItem().getCategorie().getOrder();
            int bb=b.getPreOrder().getStock().getItem().getCategorie().getOrder();
            if(aa>bb)
            return 1;
            else if(aa<bb)
            return -1;
            else return 0;
        }).sort((a,b)->{
            return a.getPreOrder().getStock().getItem().getName().compareTo(b.getPreOrder().getStock().getItem().getName());
        });
    }

    @GetMapping("/findToTakeOrder/{role}")
    public Flux<Order> getToTake(@PathVariable String role){
        return this.orders.findAll().filter(order->order.isToTake())
        .filter(order->order.getPreOrder().getStock().getItem().getRoles().contains(role))
        .sort((a,b)->{
            return a.getPreOrder().getDestination().compareTo(b.getPreOrder().getDestination());
        }).sort((a,b)->{
            int aa=a.getPreOrder().getStock().getItem().getCategorie().getOrder();
            int bb=b.getPreOrder().getStock().getItem().getCategorie().getOrder();
            if(aa>bb)
            return 1;
            else if(aa<bb)
            return -1;
            else return 0;
        }).sort((a,b)->{
            return a.getPreOrder().getStock().getItem().getName().compareTo(b.getPreOrder().getStock().getItem().getName());
        });
    }
}
