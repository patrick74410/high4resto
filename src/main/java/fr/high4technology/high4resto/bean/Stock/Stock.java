package fr.high4technology.high4resto.bean.Stock;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private String inside;
    @Getter
    private String username;
}
