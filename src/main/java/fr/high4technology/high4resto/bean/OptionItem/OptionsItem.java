package fr.high4technology.high4resto.bean.OptionItem;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class OptionsItem {
    @Id
    private String id;
    @Getter
    private String label;
    @Getter
    @Builder.Default
    private List<OptionItem> options = new ArrayList<OptionItem>();
    @Getter
    private boolean unique;
}
