package com.santander.san.audobs.sanaudobsbamoecoexislib.dto.andgo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpecificInformationDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Use String si a veces llega numérico y a veces alfanumérico. */
    private String idMongo;

    /** Alternativa: payload con avales. */
    private List<AvalDTO> avales;
}
