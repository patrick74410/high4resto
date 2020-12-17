package fr.high4technology.high4resto.bean.Client;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.ItemCarte.ItemCarte;
import fr.high4technology.high4resto.bean.commande.Commande;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Client {
    @Id
    private String id;
    @Getter
    @Builder.Default
    private String name = "";
    @Getter
    @Builder.Default
    private String lastName = "";
    @Getter
    @Builder.Default
    private String email = "";
    @Getter
    @Builder.Default
    private String adresseL1 = "";
    @Getter
    @Builder.Default
    private String adresseL2 = "";
    @Getter
    @Builder.Default
    private String zip = "";
    @Getter
    @Builder.Default
    private String city = "";
    @Getter
    @Builder.Default
    private boolean sendInfo = false;
    @Getter
    private Date firstConnexion;
    @Getter
    private Date lastConnexion;
    @Getter
    @Builder.Default
    private List<ItemCarte> currentPanier = new ArrayList<ItemCarte>();
    @Getter
    private Commande commande;
    @Getter
    @Builder.Default
    private double price= 0.0;
}
