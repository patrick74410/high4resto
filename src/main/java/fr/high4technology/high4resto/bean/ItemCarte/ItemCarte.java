package fr.high4technology.high4resto.bean.ItemCarte;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.Allergene.Allergene;
import fr.high4technology.high4resto.bean.Image.Image;
import fr.high4technology.high4resto.bean.ItemCategorie.ItemCategorie;
import fr.high4technology.high4resto.bean.OptionItem.OptionItem;
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
    @Builder.Default
    private String name = "";
    @Getter
    @Builder.Default
    private String description = "";
    @Getter
    @Builder.Default
    private double price = 0;
    @Getter
    private int order;
    @Getter
    @Builder.Default
    private Image sourceImage = new Image();
    @Getter
    @Builder.Default
    private Tva tva = new Tva();
    @Getter
    @Builder.Default
    private ItemCategorie categorie = new ItemCategorie();
    @Getter
    @Builder.Default
    private List<Allergene> allergenes = new ArrayList<Allergene>();
    @Getter
    @Builder.Default
    private List<OptionsItem> options = new ArrayList<OptionsItem>();
    @Getter
    @Builder.Default
    private boolean visible = true;
    @Getter
    @Builder.Default
    private List<Promotion> promotions = new ArrayList<Promotion>();
    @Getter
    @Builder.Default
    private int stock = 5;
    @Getter
    private String remarque;

    @Transient
    public Double finalPrice(String dateToDelivery) throws ParseException {
        Date realDateToDelivery = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dateToDelivery);
        Calendar c = Calendar.getInstance();
        c.setTime(realDateToDelivery);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0)
            dayOfWeek = 7;

        Double priceOfSelection = 0.0;
        Double priceOfItem = this.price;
        Double reduction = 0.0;

        for (OptionsItem option : this.options) {
            for (OptionItem choix : option.getOptions()) {
                if (choix.isSelected())
                    priceOfSelection += choix.getPrice();
            }
        }
        for (Promotion promotion : this.promotions) {
            if (promotion.getJourValide()[dayOfWeek] == true) {
                // Est-ce que je suis dans les bonnes dates ?
                Date realDateDebut=new SimpleDateFormat("yyyy-MM-dd").parse(promotion.getDateDebut());
                Date realDateFin=new SimpleDateFormat("yyyy-MM-dd").parse(promotion.getDateFin());
                if (!realDateToDelivery.before (realDateDebut) && !realDateToDelivery.after (realDateFin))
                {
                    // Qu'en est-t-il de l'heure ?
                    LocalTime start = LocalTime.parse( promotion.getHeureDebut()+":00" );
                    LocalTime stop = LocalTime.parse(promotion.getHeureFin()+":00");
                    LocalTime target = LocalTime.parse(dateToDelivery.substring(dateToDelivery.length()-8));

                    if( ( ! target.isBefore( start ) && target.isBefore( stop )))
                    {
                        if(promotion.isPourcentage())
                        {
                            reduction=priceOfItem*promotion.getReduction()/100;
                        }
                        else
                        {
                            reduction=promotion.getReduction();
                        }
                    }
                }
            }
        }
        return priceOfItem+priceOfSelection-reduction;
    }
}
