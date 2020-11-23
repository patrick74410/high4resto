package fr.high4technology.high4resto.bean.HomePage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.high4technology.high4resto.bean.Article.ArticleRepository;
import fr.high4technology.high4resto.bean.ArticleCategorie.ArticleCategorieRepository;
import fr.high4technology.high4resto.bean.Horaire.HoraireRepository;
import fr.high4technology.high4resto.bean.Identite.IdentiteRepository;
import fr.high4technology.high4resto.bean.Image.ImageRepository;
import fr.high4technology.high4resto.bean.MetaTag.MetaTagRepository;
import fr.high4technology.high4resto.bean.WebConfig.WebConfigRespository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/homePage")
@RequiredArgsConstructor
public class HomePageController {
    @Autowired
    private WebConfigRespository configs;
    @Autowired
    private MetaTagRepository metas;
    @Autowired
    private IdentiteRepository identites;
    @Autowired
    private ArticleCategorieRepository articleCategories;
    @Autowired
    private ArticleRepository articles;
    @Autowired
    private ImageRepository images;
    @Autowired
    private HoraireRepository horaires;

    @GetMapping("/get/")
    public Mono<HomePage> get()
    {
        final HomePage result=HomePage.builder().build();   
        return configs.findAll().flatMap(config->{
            config.getCaroussel().setDescription("");
            config.getLogo().setCategorie(null);
            config.getLogo().setDescription("");
            result.setWebConfig(config);
            return Flux.empty();
        }).thenMany(metas.findAll())
        .flatMap(meta->{
            result.setMetaTag(meta);
            return Flux.empty();
        }).thenMany(identites.findAll())
        .flatMap(idntite->{
            idntite.getLogo().setDescription("");
            idntite.getLogo().setCategorie(null);
            result.setIdentite(idntite);
            return Flux.empty();
        }).thenMany(articleCategories.findAll().filter(articleCategorie->articleCategorie.isVisible()))
        .flatMap(articleCategorie->{
            articleCategorie.setDescription("");
            articleCategorie.setImage(null);
            result.getArticleCategorie().add(articleCategorie);
            return Flux.empty();
        }).thenMany(images.findAll().filter(image->image.getCategorie()!= null).filter(image->image.getCategorie().getName().equals(result.getWebConfig().getCaroussel().getName())))
        .flatMap(image->{
            result.getCaroussel().add(image);
            return Flux.empty(); 
        }).thenMany(articles.findAll().filter(article->article.isOnTop()))
        .flatMap(article->{
            article.setContent("");
            result.getOnTop().add(article);
            return Flux.empty();
        }).thenMany(horaires.findAll())
        .flatMap(horaire->{
            result.setHoraire(horaire);
            return Flux.empty();            
        })
        .then(Mono.just(result));
    }
}
