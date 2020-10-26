package fr.high4technology.high4resto.bean.Image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.time.Duration;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {
    @Autowired
    private ImageRepository images;
	private final ReactiveGridFsTemplate gridFsTemplate;

    @PostMapping(path = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Void>> upload(@RequestPart("file") Mono<FilePart> fileParts,@RequestParam("description") String description,@RequestParam("fileName") String fileName,@RequestParam("group") String group) {
        return fileParts
            .flatMap(part -> this.gridFsTemplate.store(part.content(), part.filename()))
            .flatMap(id -> this.images.save(Image.builder().fileName(fileName).description(description).group(group).gridId(id.toHexString()).build()))
 			.map( r -> ResponseEntity.ok().<Void>build())
			.defaultIfEmpty(ResponseEntity.ok().build());
	}    

    @GetMapping("/download/{id}")
    public Flux<Void> read(@PathVariable String id, ServerWebExchange exchange) {
       return this.gridFsTemplate.findOne(query(where("_id").is(id)))
            .flatMap(gridFsTemplate::getResource)
            .flatMapMany(r -> {
				exchange.getResponse().getHeaders().setContentType(MediaType.IMAGE_PNG);
				exchange.getResponse().getHeaders().setCacheControl(CacheControl.maxAge(Duration.ofSeconds(3600)).cachePrivate());		
				return exchange.getResponse().writeWith(r.getDownloadStream());
			});
    }

    @GetMapping("/find/")
	public Flux<Image> getAllAll()
	{
		return images.findAll();
	}

	@GetMapping("/find/{idItem}")
	public Mono<Image> getById(@PathVariable String idItem){
		return images.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem)
	{
		return images.deleteById(idItem)
				.map( r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.ok().build());
	}

	@DeleteMapping("/deleteGrid/{idGrid}")
	public Mono<ResponseEntity<Void>> deleteGrid(@PathVariable String idGrid)
	{
		return gridFsTemplate.delete(query(where("_id").is(idGrid)))
				.map( r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.ok().build());
	}

	@PutMapping("/update/")
	Mono<Image> update(@RequestBody Image image)
	{
		return images.findById(image.getId())
		.map(foundItem -> {
			foundItem.setDescription(image.getDescription());
			foundItem.setGroup(image.getGroup());
			return foundItem;
		 })
		.flatMap(images::save);
	}	

}