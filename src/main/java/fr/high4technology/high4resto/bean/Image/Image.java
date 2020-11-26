package fr.high4technology.high4resto.bean.Image;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.ImageCategorie.ImageCategorie;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Image {
    @Id
    private String id;
    @Getter
    @Builder.Default
    private String description = "";
    @Getter
    private String gridId;
    @Getter
    private String miniGridId;
    @Getter
    private String fileName;
    @Getter
    private ImageCategorie categorie;
    @Getter
    @Builder.Default
    private String alt = "";
    @Getter
    @Builder.Default
    private String link = "";
}
