package fr.high4technology.high4resto.bean.ImageCategorie;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ImageCategorie {
    @Id
	private String id;
    @Getter 
    private String name;
    @Getter
    private String description;
    @Getter
    private boolean visible;
}
