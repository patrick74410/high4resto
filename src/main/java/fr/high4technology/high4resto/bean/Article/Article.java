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
    private String title;
    @Getter
    private String resume;
    @Getter
    private String content;
    @Getter
    private String date;
    @Getter
    private String author;
}
