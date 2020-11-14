package fr.high4technology.high4resto.bean.Client;

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
public class Client {
    @Id
    private String id;
    @Getter
    String username;
    @Getter
    String name;
    @Getter
    String lastName;
    @Getter
    String email;
    @Getter
    String apiSecurity;   
}
