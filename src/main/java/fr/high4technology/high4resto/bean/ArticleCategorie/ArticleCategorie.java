package fr.high4technology.high4resto.bean.ArticleCategorie;

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
public class ArticleCategorie {
	@Id
	private String id;
	@Getter
	@Builder.Default
	private String name = "";
	@Getter
	@Builder.Default
	private String description = "";
	@Getter
	private int order;
	@Getter
	private Image iconImage;
	@Getter
	private Image image;
	@Getter
	@Builder.Default
	private boolean visible = true;
}
