package fr.high4technology.high4resto.bean.Tracability.Order;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.Tracability.PreOrder.PreOrder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Order {
    @Id
    private String id;
    @Getter
    private PreOrder preOrder;
    @Getter
    private String inside;
    @Getter
    private String mandatory;
    @Getter
    private String deliveryMode;
    @Getter
    private String orderNumber;
    @Getter
    private String meansOfPayment;
}
