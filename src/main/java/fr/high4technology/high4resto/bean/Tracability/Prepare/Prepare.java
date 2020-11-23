package fr.high4technology.high4resto.bean.Tracability.Prepare;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.Tracability.toPrepare.ToPrepare;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Prepare {
    @Id
	private String id;
    @Getter
    private ToPrepare toPrepare;
    @Getter
    private String inside;
}
