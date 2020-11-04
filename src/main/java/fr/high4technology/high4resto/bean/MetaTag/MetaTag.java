package fr.high4technology.high4resto.bean.MetaTag;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import fr.high4technology.high4resto.bean.Struct.KeyMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class MetaTag {
    @Id
    private String id;
    @Getter
    private String description;
    @Getter
    private String keywords;
    @Getter
    private String author;
    @Getter
    private String facebookTitle;
    @Getter
    private String facebookDescription;
    @Getter
    private String facebookImage;
    @Getter
    private String twitterTitle;
    @Getter
    private String twitterDescription;
    @Getter
    private String twitterImage;
    @Getter
    private String twitterAuthor;
    @Getter
    @Builder.Default
    private List<KeyMap> other=new ArrayList<KeyMap>();
}
 