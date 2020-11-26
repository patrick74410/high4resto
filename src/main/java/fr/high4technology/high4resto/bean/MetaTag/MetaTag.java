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
    @Builder.Default
    private String description = "";
    @Getter
    @Builder.Default
    private String keywords = "";
    @Getter
    @Builder.Default
    private String author = "";
    @Getter
    @Builder.Default
    private String facebookTitle = "";
    @Getter
    @Builder.Default
    private String facebookDescription = "";
    @Getter
    @Builder.Default
    private String facebookImage = "";
    @Getter
    @Builder.Default
    private String twitterTitle = "";
    @Getter
    @Builder.Default
    private String twitterDescription = "";
    @Getter
    @Builder.Default
    private String twitterImage = "";
    @Getter
    @Builder.Default
    private String twitterAuthor = "";
    @Getter
    @Builder.Default
    private List<KeyMap> other = new ArrayList<KeyMap>();
}
