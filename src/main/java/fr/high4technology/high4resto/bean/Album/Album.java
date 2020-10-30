package fr.high4technology.high4resto.bean.Album;
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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Album {
    @Id
    private String id;
    @Getter
    private String name;
    @Builder.Default
    @Getter
    private List<Image> photos=new ArrayList<Image>();   
}
