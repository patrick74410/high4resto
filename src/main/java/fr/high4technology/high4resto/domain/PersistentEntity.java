package fr.high4technology.high4resto.domain;

import java.time.LocalDateTime;
import fr.high4technology.high4resto.bean.user.Username;

public interface PersistentEntity {

    String getId();

    void setId(String id);

    Username getCreatedBy();

    void setCreatedBy(Username username);

    Username getLastModifiedBy();

    void setLastModifiedBy(Username username);

    LocalDateTime getCreatedDate();

    void setCreatedDate(LocalDateTime createdDate);

    LocalDateTime getLastModifiedDate();

    void setLastModifiedDate(LocalDateTime lastModifiedDate);

}
