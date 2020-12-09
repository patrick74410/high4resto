package fr.high4technology.high4resto.bean.commande;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.Tracability.PreOrder.PreOrder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Commande {
    @Id
    private String id;
    @Getter
    private long number;
    @Getter
    private String destination;
    @Getter
    private String mandatory;
    @Getter
    private String client;
    @Getter
    private String deleveryMode;
    @Getter
    private String status;
    @Getter
    private String inside;
    @Getter
    @Builder.Default
    private List<PreOrder> items = new ArrayList<PreOrder>();
    @Getter
    private Boolean finish;
    @Getter
    private Double totalPrice;
    @Getter
    private String message;

}
