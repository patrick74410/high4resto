package fr.high4technology.high4resto.bean.Struct;

import lombok.Setter;
import lombok.Builder;
import lombok.Getter;

@Getter
@Setter
@Builder
public class Gps {
    private double longitude;
    private double latitude;
}
