package fr.high4technology.high4resto.bean.Identite;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.Image.Image;
import fr.high4technology.high4resto.bean.Struct.Gps;
import fr.high4technology.high4resto.bean.Struct.KeyMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Identite {
    @Id
    private String id;
    @Getter
    private String nomEtablissement;
    @Getter
    private String zip;
    @Getter
    private String city;
    @Getter
    private String number;
    @Getter
    private String adresse;
    @Getter
    private String complement;
    @Getter
    @Builder.Default
    private List<KeyMap> contact=new ArrayList<KeyMap>();
    @Getter
    private String siret;     
    @Getter
    @Builder.Default
    private Gps coordonnee=Gps.builder().latitude(0).longitude(0).build();
    @Getter
    private Image logo;
    @Getter
    private String description;
}
