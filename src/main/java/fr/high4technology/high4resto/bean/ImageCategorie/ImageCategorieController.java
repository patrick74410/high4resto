package fr.high4technology.high4resto.bean.ImageCategorie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/imageCategorie")
@RequiredArgsConstructor
public class ImageCategorieController {
     @Autowired
     private ImageCategoryRepository imageCategories;
     @GetMapping("/find/")
     public Flux<ImageCategorie> getAllAll()
     {
         return imageCategories.findAll();
     }
 
     @GetMapping("/find/{idItem}")
     public Mono<ImageCategorie> getById(@PathVariable String idItem){
         return imageCategories.findById(idItem);
     }
 
     @DeleteMapping("/delete/{idItem}")
     public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem)
     {
     
         return imageCategories.deleteById(idItem)
                 .map( r -> ResponseEntity.ok().<Void>build())
                 .defaultIfEmpty(ResponseEntity.ok().<Void>build());
     }
 
     @PutMapping("/insert/")
     Mono<ImageCategorie> insert(@RequestBody ImageCategorie imageCategorie)
     {
         return imageCategories.save(imageCategorie);
     }
 
     @PutMapping("/update/")
     Mono<ImageCategorie> update(@RequestBody ImageCategorie imageCategorie)
     {
         return imageCategories.findById(imageCategorie.getId())
         .map(foundItem -> {
            foundItem.setName(imageCategorie.getName());
            foundItem.setDescription(imageCategorie.getDescription());
            foundItem.setVisible(imageCategorie.isVisible());
             return foundItem;
          })
         .flatMap(imageCategories::save);
     }     
}
