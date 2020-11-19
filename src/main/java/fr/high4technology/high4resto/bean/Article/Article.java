package fr.high4technology.high4resto.bean.Article;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.ArticleCategorie.ArticleCategorie;
import fr.high4technology.high4resto.bean.Image.Image;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Article {
    @Id
    private String id;
    @Getter
    private ArticleCategorie categorie;
    @Getter
    private Image image;
    @Getter
    private boolean onTop;
    @Getter
    private boolean visible;
    @Getter
    @Builder.Default
    private String title="";
    @Getter
    @Builder.Default
    private String resume="";
    @Getter
    @Builder.Default
    private String content="";
    @Getter
    private String date;
    @Getter
    @Builder.Default
    private String author="";
}
