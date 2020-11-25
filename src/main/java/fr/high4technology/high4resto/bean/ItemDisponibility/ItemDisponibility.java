package fr.high4technology.high4resto.bean.ItemDisponibility;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.Horaire.Horaire;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ItemDisponibility {
    @Id
    private String id;
    @Getter
    private Horaire disponibility;
    @Getter
    private String dateDebut;
    @Getter 
    private String dateFin;
    @Getter
    private boolean always; 
}
