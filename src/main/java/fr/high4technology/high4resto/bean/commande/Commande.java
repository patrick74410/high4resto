package fr.high4technology.high4resto.bean.commande;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

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
    @Getter
    private long distanceTime;
    @Getter
    private long distance;
    @Getter
    private String timeToTake;

    private PreOrder retrievePreorderWithBiggestPart(ArrayList<PreOrder> itemsCopy) {
        PreOrder tp = itemsCopy.get(0);
        for (PreOrder preOrder : itemsCopy) {
            if (preOrder.getStock().getItem().getPart() > tp.getStock().getItem().getPart())
                tp = preOrder;
        }
        itemsCopy.remove(tp);
        return tp;
    }

    private LinkedList<Double> generatePreorderLinkedTime(PreOrder preOrder) {
        LinkedList<Double> micro = new LinkedList<Double>();
        for (int i = 0; i != preOrder.getStock().getItem().getTime(); i++) {
            micro.add(preOrder.getStock().getItem().getPart());
        }
        return micro;

    }

    private LinkedList<Double> recursiveAdd(Double number,LinkedList<Double> micro)
    {
        int index = 0;
        for (Double value : micro) {
            if (value < 1.0)
                break;
            index += 1;
        }
        if(index==micro.size())
        {
            micro.add(number);
            return micro;
        }
        else
        {
            if(number+micro.get(index)>1)
            {
                micro.set(index, 1.0);
                return recursiveAdd(number+micro.get(index)-1,micro);
            }
            else
            {
                micro.set(index, number+micro.get(index));
                return micro;
            }
        }
    }

    public LinkedList<Double> generateMicroPlanning() {
        ArrayList<PreOrder> itemsCopy = new ArrayList<PreOrder>();
        itemsCopy.addAll(items);

        LinkedList<Double> microPlanning = new LinkedList<Double>();

        if (itemsCopy.size() > 0) {
            microPlanning.addAll(generatePreorderLinkedTime(this.retrievePreorderWithBiggestPart(itemsCopy)));
            while (itemsCopy.size() > 0) {
                LinkedList<Double> micro = generatePreorderLinkedTime(this.retrievePreorderWithBiggestPart(itemsCopy));

                for (Double toAdd : micro) {
                    this.recursiveAdd(toAdd, microPlanning);
                }

            }
            StringBuilder t=new StringBuilder();
            microPlanning.forEach(value->{
                    t.append(value+" ");
            });

        }
        return microPlanning;
    }
}
