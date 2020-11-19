package fr.high4technology.high4resto.bean.Image;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
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

import fr.high4technology.high4resto.bean.Article.ArticleRepository;
import fr.high4technology.high4resto.bean.ArticleCategorie.ArticleCategorieRepository;
import fr.high4technology.high4resto.bean.Identite.IdentiteRepository;
import fr.high4technology.high4resto.bean.ImageCategorie.ImageCategorie;
import fr.high4technology.high4resto.bean.ItemCarte.ItemCarteRepository;
import fr.high4technology.high4resto.bean.ItemCategorie.ItemCategorieRepository;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
public class ImageController {
	@Autowired
	private ImageRepository images;
	@Autowired
	private ItemCarteRepository items;
	@Autowired
	private ItemCategorieRepository itemsCategorie;
	@Autowired
	private ArticleCategorieRepository articleCategories;
	@Autowired
	private ArticleRepository articles;
	@Autowired
	private IdentiteRepository identites;

	private final ReactiveGridFsTemplate gridFsTemplate;

	final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";

	final java.util.Random rand = new java.util.Random();
	
	final Set<String> identifiers = new HashSet<String>();
	
	public String randomIdentifier() {
		StringBuilder builder = new StringBuilder();
		while(builder.toString().length() == 0) {
			int length = rand.nextInt(5)+5;
			for(int i = 0; i < length; i++) {
				builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
			}
			if(identifiers.contains(builder.toString())) {
				builder = new StringBuilder();
			}
		}
		return builder.toString();
	}


	@PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Mono<ResponseEntity<Void>> upload(@RequestPart("file") Mono<FilePart> fileParts,
			@RequestParam("link") String link, @RequestParam("alt") String alt,
			@RequestParam("description") String description, @RequestParam("fileName") String fileName,
			@RequestParam("categorie") String categorie) throws Exception {
		final ImageCategorie categorieO = (new ObjectMapper()).readValue(categorie, ImageCategorie.class);
		Image finalImage= Image.builder().link(link).alt(alt).fileName(fileName+".webp").description(description).categorie(categorieO).build();
		
		return fileParts.flatMap(part -> {
			File src = new File("/tmp/"+part.filename());
			File dest = new File("/tmp/"+part.filename() + ".webp");
			File thumDest= new File("/tmp/"+"thumb"+fileName+".webp");
			part.transferTo(src);

			String s;
			Process p;
			try {
				p = Runtime.getRuntime().exec("cwebp "+src.getName()+" -o "+dest.getName(),null,new File("/tmp/"));
				BufferedReader br = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
				while ((s = br.readLine()) != null)
					log.info("line: " + s);
				p.waitFor();
				log.info("exit: " + p.exitValue());
				p.destroy();
			} catch (Exception e) {e.printStackTrace();}

			try {
				p = Runtime.getRuntime().exec("cwebp -resize 300 0 "+src.getName()+" -o "+thumDest.getName(),null,new File("/tmp/"));
				BufferedReader br = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
				while ((s = br.readLine()) != null)
					log.info("line: " + s);
				p.waitFor();
				log.info("exit: " + p.exitValue());
				p.destroy();
			} catch (Exception e) {
				log.error(e.getMessage());
			}

			DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
			DefaultDataBuffer finalFile;

			try {
				finalFile = factory.wrap(FileUtils.readFileToByteArray(dest));
				FileUtils.deleteQuietly(src);
				FileUtils.deleteQuietly(dest);
				return this.gridFsTemplate.store(Flux.just(finalFile), part.filename());
			} catch (IOException e) {
				log.error(e.getMessage());
			}				
			return this.gridFsTemplate.store(part.content(), part.filename());})
			.flatMap(id ->{
				finalImage.setGridId(id.toHexString());
				File thumDest= new File("/tmp/"+"thumb"+fileName+".webp");
				try {
					DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
					DefaultDataBuffer finalFile;
					finalFile = factory.wrap(FileUtils.readFileToByteArray(thumDest));
					FileUtils.deleteQuietly(thumDest);
					return this.gridFsTemplate.store(Flux.just(finalFile),fileName);
				} catch (IOException e) {
					log.error(e.getMessage());
				}				
				return this.gridFsTemplate.store(Flux.just(),fileName+".webp");
			})
            .flatMap(id -> {
				finalImage.setMiniGridId(id.toHexString());

				return this.images.save(finalImage);
				}) 
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
	
    @GetMapping("/download/{id}/{name}")
    public Flux<Void> getImage(@PathVariable String id,@PathVariable String name, ServerWebExchange exchange) {
       return this.gridFsTemplate.findOne(query(where("_id").is(id)))
            .flatMap(gridFsTemplate::getResource)
            .flatMapMany(r -> {
				exchange.getResponse().getHeaders().setCacheControl(CacheControl.maxAge(Duration.ofSeconds(3600)).cachePrivate());		
				return exchange.getResponse().writeWith(r.getDownloadStream());
			});
	}
	
    @GetMapping("/find/")
	public Flux<Image> getAllAll()
	{
		return images.findAll();
	}

	@GetMapping("/filter/{categorieName}")
	public Flux<Image> filter(@PathVariable String categorieName)
	{
		return images.findAll().filter(image->image.getCategorie().getName().equals(categorieName));
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
			foundItem.setCategorie(image.getCategorie());
			foundItem.setAlt(image.getAlt());
			foundItem.setLink(image.getLink());
			return foundItem;
		 })
		 .flatMap(imageItem -> {
			
			this.items.findAll().subscribe(article -> {
				boolean change=false;
				try
				{
					if(article.getSourceImage().getId().equals(imageItem.getId()))
					{
						article.setSourceImage(imageItem);
						change=true;
					}	
				}
				catch(Exception e){}
				if(change)
				{
					var flux = items.save(article);
					flux.doOnSubscribe(data -> log.info("data:" + data)).thenMany(flux).subscribe(
							data -> log.info("data:" + data), err -> log.error("error:" + err),
							() -> log.info("done initialization..."));
				}
			});

			this.itemsCategorie.findAll().subscribe(itemCategorie->{
				boolean change=false;
				try
				{
					if(itemCategorie.getIconImage().getId().equals(imageItem.getId()))
					{
						itemCategorie.setIconImage(imageItem);
						change=true;
					}
				}
				catch(Exception e){}
				try
				{
					if(itemCategorie.getImage().getId().equals(imageItem.getId()))
					{
						itemCategorie.setImage(imageItem);
					}	
				}
				catch(Exception e){}
				if(change)
				{
					var flux = itemsCategorie.save(itemCategorie);
					flux.doOnSubscribe(data -> log.info("data:" + data)).thenMany(flux).subscribe(
							data -> log.info("data:" + data), err -> log.error("error:" + err),
							() -> log.info("done initialization..."));
				}
			});

			this.identites.findAll().subscribe(identite->{
				boolean change=false;
				try{
					if(identite.getLogo().getId().equals(imageItem.getId()))
					{
						identite.setLogo(imageItem);
						change=true;
					}						
				}
				catch(Exception e){}
				if(change)
				{
					var flux=this.identites.save(identite);
					flux.doOnSubscribe(data -> log.info("data:" + data)).thenMany(flux).subscribe(
							data -> log.info("data:" + data), err -> log.error("error:" + err),
							() -> log.info("done initialization..."));				
				}
			});

			this.articleCategories.findAll().subscribe(articleCategorie->
			{
				boolean change=false;
				try {
					if(articleCategorie.getIconImage().getId().equals(imageItem.getId()))
					{
						articleCategorie.setIconImage(imageItem);
						change=true;
					}
				} 
				catch (Exception e) {}
				try {
					if(articleCategorie.getImage().getId().equals(imageItem.getId()))
					{
						articleCategorie.setImage(imageItem);
						change=true;
					}	
				}
				catch (Exception e) {}
				if(change)
				{
					var flux =this.articleCategories.save(articleCategorie);
					flux.doOnSubscribe(data -> log.info("data:" + data)).thenMany(flux).subscribe(
							data -> log.info("data:" + data), err -> log.error("error:" + err),
							() -> log.info("done initialization..."));				
						
				}

			});

			this.articles.findAll().subscribe(article->{
				boolean change=false;
				try
				{
					if(article.getImage().getId().equals(imageItem.getId()))
					{
						article.setImage(imageItem);
						change=true;
					}
				}
				catch (Exception e) {}
				if(change)
				{
					var flux=this.articles.save(article);
					flux.doOnSubscribe(data -> log.info("data:" + data)).thenMany(flux).subscribe(
							data -> log.info("data:" + data), err -> log.error("error:" + err),
							() -> log.info("done initialization..."));										
				}
			});

			return this.images.save(imageItem);
		});	}	

}
