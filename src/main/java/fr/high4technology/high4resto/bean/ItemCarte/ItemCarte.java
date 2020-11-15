package fr.high4technology.high4resto.bean.ItemCarte;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.Allergene.Allergene;
import fr.high4technology.high4resto.bean.Image.Image;
import fr.high4technology.high4resto.bean.ItemCategorie.ItemCategorie;
import fr.high4technology.high4resto.bean.OptionItem.OptionsItem;
import fr.high4technology.high4resto.bean.Promotion.Promotion;
import fr.high4technology.high4resto.bean.Tva.Tva;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ItemCarte {
    @Id
    private String id;
    @Getter
    private String name;
    @Getter
    private String description;
    @Getter
    private double price;
    @Getter
    private int order;
    @Getter
    @Builder.Default
    private Image sourceImage=new Image();
    @Getter
    @Builder.Default
    private Tva tva =new Tva();
    @Getter
    @Builder.Default
    private ItemCategorie categorie =new ItemCategorie();
    @Getter
    @Builder.Default
    private List<Allergene> allergenes = new ArrayList<Allergene>();
    @Getter
    @Builder.Default
    private List<OptionsItem> options = new ArrayList<OptionsItem>();
    @Getter
    @Builder.Default
    private boolean visible=true;
    @Getter
    @Builder.Default
    private List<Promotion> promotions = new ArrayList<Promotion>();
    @Getter
    private int stock;
    
}
