package fr.high4technology.high4resto.bean.ItemCategorie;

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
public class ItemCategorie {
	@Id
	private String id;
	@Getter
	private String name;    
	@Getter
	private String description;
	@Getter
	private int order;
	@Getter
	private Image iconImage;
	@Getter
	private Image image;
}
