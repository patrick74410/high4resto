package fr.high4technology.high4resto.bean.Panier;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.ItemCarte.ItemCarte;
import fr.high4technology.high4resto.bean.Struct.KeyMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Panier {
    @Id
    String id;
    @Getter 
    String username;
    @Getter
    @Builder.Default
    private List<ItemCarte> items=new ArrayList<ItemCarte>();
    @Getter
    private Date date;
    @Getter
    private String status;
    @Getter
    @Builder.Default
    private List<KeyMap> metaData=new ArrayList<KeyMap>();
}
