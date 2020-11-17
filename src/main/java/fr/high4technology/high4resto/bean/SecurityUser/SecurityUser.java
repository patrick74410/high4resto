package fr.high4technology.high4resto.bean.SecurityUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class SecurityUser {
    @Id
    private String id;

    @Getter
    private String email;

    @Getter
    private String generateKey;

    @Getter
    private String identity;

}
