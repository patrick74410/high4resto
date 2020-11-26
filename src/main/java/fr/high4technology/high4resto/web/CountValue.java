package fr.high4technology.high4resto.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@AllArgsConstructor(onConstructor = @__({ @JsonCreator }))
public class CountValue implements Serializable {

    private static final long serialVersionUID = -6111842686170395417L;
    private final long count;

}
