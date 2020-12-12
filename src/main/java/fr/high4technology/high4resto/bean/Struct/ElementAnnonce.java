package fr.high4technology.high4resto.bean.Struct;

import java.util.ArrayList;
import java.util.List;
import fr.high4technology.high4resto.bean.Tracability.Order.Order;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ElementAnnonce
{
    private String itemName;
    private String itemId;
    private Long number;
    private List<Order> orders = new ArrayList<Order>();
}