package fr.high4technology.high4resto.bean.Tracability.Delevery;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.Tracability.ToDelivery.ToDelivery;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Delevery {
    @Id
	private String id;
    @Getter
    private ToDelivery toDelivery;
    @Getter
    private String inside;
  
}
