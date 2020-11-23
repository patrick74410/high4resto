package fr.high4technology.high4resto.bean.Tracability.toPrepare;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.Tracability.Order.Order;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ToPrepare {
    @Id
	private String id;
    @Getter
    private Order order;
    @Getter
    private String inside;
    @Getter
    private String executorName;
}
