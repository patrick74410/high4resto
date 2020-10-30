package fr.high4technology.high4resto.bean.Horaire;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.Struct.BetweenTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Horaire {
    @Id
    private String id;
    @Getter
    @Builder.Default
    private List<BetweenTime> lundi=new ArrayList<BetweenTime>();
    @Getter
    @Builder.Default
    private List<BetweenTime> mardi=new ArrayList<BetweenTime>();
    @Getter
    @Builder.Default
    private List<BetweenTime> mercredi=new ArrayList<BetweenTime>();
    @Getter
    @Builder.Default
    private List<BetweenTime> jeudi=new ArrayList<BetweenTime>();
    @Getter
    @Builder.Default
    private List<BetweenTime> vendredi=new ArrayList<BetweenTime>();
    @Getter
    @Builder.Default
    private List<BetweenTime> samedi=new ArrayList<BetweenTime>();
    @Getter
    @Builder.Default
    private List<BetweenTime> dimanche=new ArrayList<BetweenTime>();
    @Getter
    private List<BetweenTime> ferie=new ArrayList<BetweenTime>();
}
