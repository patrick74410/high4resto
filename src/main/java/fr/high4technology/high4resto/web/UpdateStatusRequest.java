package fr.high4technology.high4resto.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusRequest implements Serializable {

    private static final long serialVersionUID = -6194755734106931149L;
    @NotBlank
    private String status;

}
