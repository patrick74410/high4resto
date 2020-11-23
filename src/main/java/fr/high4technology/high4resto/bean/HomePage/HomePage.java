package fr.high4technology.high4resto.bean.HomePage;

import java.util.ArrayList;
import java.util.List;

import fr.high4technology.high4resto.bean.Article.Article;
import fr.high4technology.high4resto.bean.ArticleCategorie.ArticleCategorie;
import fr.high4technology.high4resto.bean.Horaire.Horaire;
import fr.high4technology.high4resto.bean.Identite.Identite;
import fr.high4technology.high4resto.bean.Image.Image;
import fr.high4technology.high4resto.bean.MetaTag.MetaTag;
import fr.high4technology.high4resto.bean.WebConfig.WebConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class HomePage {
    private WebConfig webConfig;
    private MetaTag metaTag;
    private Identite identite;
    @Builder.Default
    private List<ArticleCategorie> articleCategorie=new ArrayList<ArticleCategorie>();
    @Builder.Default
    private List<Article> onTop=new ArrayList<Article>();
    @Builder.Default
    private List<Image> caroussel=new ArrayList<Image>();
    private Horaire horaire;
    
}
