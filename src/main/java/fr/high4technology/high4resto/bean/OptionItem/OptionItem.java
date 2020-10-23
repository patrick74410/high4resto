package fr.high4technology.high4resto.bean.OptionItem;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class OptionItem {
    @Getter
    @Setter
	private String label;
    @Getter
    @Setter
    private double price;
    @Getter
    @Setter
    private boolean selected;
}
