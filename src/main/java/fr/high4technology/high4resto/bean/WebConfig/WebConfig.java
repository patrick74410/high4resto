package fr.high4technology.high4resto.bean.WebConfig;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.Image.Image;
import fr.high4technology.high4resto.bean.ImageCategorie.ImageCategorie;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class WebConfig {
    @Id
    private String id;
    @Getter
    @Builder.Default
    private String title = "";
    @Getter
    private Image logo;
    @Getter
    private ImageCategorie caroussel;
    @Getter
    @Builder.Default
    private String googleMapApi = "";
    @Getter
    @Builder.Default
    private String auth0Key = "";
    @Getter
    @Builder.Default
    private String auth0Domain = "";
    @Getter
    private boolean qty;
}
