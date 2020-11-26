package fr.high4technology.high4resto.bean.Promotion;

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
public class Promotion {
    @Id
    private String id;
    @Getter
    @Builder.Default
    private String name = "";
    @Getter
    @Builder.Default
    private Double reduction = 0.0;
    @Getter
    @Builder.Default
    private String heureDebut = "";
    @Getter
    @Builder.Default
    private String heureFin = "";
    @Getter
    @Builder.Default
    private String dateDebut = "";
    @Getter
    @Builder.Default
    private String dateFin = "";
    @Getter
    private boolean[] jourValide;
    @Getter
    private boolean jourFerie;
    @Getter
    private boolean pourcentage;

}
