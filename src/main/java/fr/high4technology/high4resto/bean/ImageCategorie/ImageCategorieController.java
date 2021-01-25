package fr.high4technology.high4resto.bean.ImageCategorie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.high4technology.high4resto.bean.Image.ImageRepository;
import fr.high4technology.high4resto.Util.Variable;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/"+Variable.apiPath+"/imageCategorie")
@RequiredArgsConstructor
@Slf4j
public class ImageCategorieController {
    @Autowired
    private ImageCategoryRepository imageCategories;
    @Autowired

    private ImageRepository images;

    @GetMapping("/find/")
    public Flux<ImageCategorie> getAllAll() {
        return imageCategories.findAll();
    }

    @GetMapping("/findAlbum/")
    public Flux<ImageCategorie> getAlbum() {
        return imageCategories.findAll().filter(a->{return a.isVisible();});
    }

    @GetMapping("/find/{idItem}")
    public Mono<ImageCategorie> getById(@PathVariable String idItem) {
        return imageCategories.findById(idItem);
    }

    @DeleteMapping("/delete/{idItem}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem) {
        return imageCategories.deleteById(idItem).and(images.findAll().map(image -> {
            if (image.getCategorie().getId().equals(idItem))
                image.setCategorie(null);
            return image;
        }).flatMap(images::save)).map(r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.ok().<Void>build());
    }

    @PutMapping("/insert/")
    Mono<ImageCategorie> insert(@RequestBody ImageCategorie imageCategorie) {
        return imageCategories.save(imageCategorie);
    }

    @PutMapping("/update/")
    Mono<ImageCategorie> update(@RequestBody ImageCategorie imageCategorie) {
        return imageCategories.findById(imageCategorie.getId()).map(foundItem -> {
            foundItem.setName(imageCategorie.getName());
            foundItem.setDescription(imageCategorie.getDescription());
            foundItem.setVisible(imageCategorie.isVisible());
            foundItem.setTopImage(imageCategorie.getTopImage());
            return foundItem;
        }).flatMap(imgCategorie -> {
            images.findAll().subscribe(image -> {
                if (image.getCategorie().getId().equals(imgCategorie.getId())) {
                    image.setCategorie(imgCategorie);
                    var flux = images.save(image);
                    flux.doOnSubscribe(data -> log.info("data:" + data)).thenMany(flux).subscribe(
                            data -> log.info("data:" + data), err -> log.error("error:" + err),
                            () -> log.info("done initialization..."));
                }
            });
            return imageCategories.save(imgCategorie);
        });
    }
}
