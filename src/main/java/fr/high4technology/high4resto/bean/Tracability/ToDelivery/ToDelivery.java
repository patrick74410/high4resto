package fr.high4technology.high4resto.bean.Tracability.ToDelivery;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.Tracability.Prepare.Prepare;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ToDelivery {
    @Id
	private String id;
    @Getter
    private Prepare prepare;
    @Getter
    private String inside;
    @Getter
    private String deleveryPerson;
}
