package fr.high4technology.high4resto.bean.ItemPlaning;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ItemPlaning {
    @Id
    private String id;
    @Getter
    @Builder.Default
    private double[] a = new double[24*60];
}
