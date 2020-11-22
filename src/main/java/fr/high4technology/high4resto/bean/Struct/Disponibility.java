package fr.high4technology.high4resto.bean.Struct;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Disponibility {
    private List<BetweenTime> disponible=new ArrayList<BetweenTime>();
    private String DateDebut;
    private String DateFin;
    private boolean[] jourValide={false,false,false,false,false,false,false};
    private boolean ferie;
   
}
