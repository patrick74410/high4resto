package fr.high4technology.high4resto.bean.Tracability.PreOrder;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.Stock.Stock;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class PreOrder {
    @Id
	private String id;
    @Getter
    private Stock stock;
    @Getter
    private String inside;
    @Getter
    private String idCustomer;
}
