package fr.high4technology.high4resto.bean.ItemRoleLink;

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
public class ItemRoleLink {
	@Id
	private String id;
    @Getter
    @Builder.Default
    private List<String> roleName=new ArrayList<String>();
    @Getter
    private double part;
}
