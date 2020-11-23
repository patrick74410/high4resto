package fr.high4technology.high4resto.bean.Stock;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.Struct.Disponibility;
import fr.high4technology.high4resto.bean.ItemCarte.ItemCarte;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Stock {
    @Id
	private String id;
    @Getter
    private ItemCarte item;
    @Getter
    @Builder.Default
    private Disponibility disponibility=new Disponibility();
    @Getter
    private String inside;
}
