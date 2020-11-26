package fr.high4technology.high4resto.bean.ImageCategorie;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.Image.Image;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ImageCategorie {
    @Id
    private String id;
    @Getter
    @Builder.Default
    private String name = "";
    @Getter
    @Builder.Default
    private String description = "";
    @Getter
    private boolean visible;
    @Getter
    private Image topImage;
}
