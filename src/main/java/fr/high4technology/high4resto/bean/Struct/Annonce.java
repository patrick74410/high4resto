package fr.high4technology.high4resto.bean.Struct;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Annonce {
    private String table;
    private List<ElementAnnonce> elements= new ArrayList<ElementAnnonce>();
}


