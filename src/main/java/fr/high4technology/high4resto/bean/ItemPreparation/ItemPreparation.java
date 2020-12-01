package fr.high4technology.high4resto.bean.ItemPreparation;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ItemPreparation {
    @Id
    private String id;
    @Getter
    @Builder.Default
    private List<String> roleName = new ArrayList<String>();
    @Getter
    private double part;
    @Getter
    private String name;
    @Getter
    private int time;
}
