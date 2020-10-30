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
public class Promotion{
    @Id
    private String id;
    @Getter
    private String name;
    @Getter
    private Double reduction;
    @Getter
    private String heureDebut;
    @Getter
    private String heureFin;
    @Getter
    private String dateDebut;
    @Getter
    private String dateFin;
    @Getter
    private boolean[] jourValide;
    @Getter
    private boolean jourFerie;
    @Getter
    private boolean pourcentage;

}
