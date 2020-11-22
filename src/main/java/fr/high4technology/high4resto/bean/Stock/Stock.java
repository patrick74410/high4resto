package fr.high4technology.high4resto.bean.Stock;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.Struct.Disponibility;
import fr.high4technology.high4resto.bean.ItemCarte.ItemCarte;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Stock {
    @Id
	private String id;
    @Getter
    private ItemCarte item;
    @Getter
    @Builder.Default
    private Disponibility disponibility=new Disponibility();
    @Getter
    @Builder.Default
    private String inside=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris")).getTime());
}
