package fr.high4technology.high4resto.bean.table;

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

public class Table {
	@Id
	private String id;
    @Getter
    private String name;
    @Getter
    private int place;
}
