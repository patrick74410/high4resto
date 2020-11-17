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


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Client {
    @Id
    private String id;
    @Getter
    private String name;
    @Getter
    private String lastName;
    @Getter
    private String email;
    @Getter
    private String adresseL1;
    @Getter
    private String adresseL2;
    @Getter
    private String zip;
    @Getter
    private String city;
    @Getter
    private boolean sendInfo;
    @Getter
    private Date firstConnexion;
    @Getter
    private Date lastConnexion;
    @Getter
    @Builder.Default
    private List<ItemCarte>currentPanier=new ArrayList<ItemCarte>();
}
