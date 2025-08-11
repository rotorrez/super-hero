package com.santander.san.audobs.sanaudobsbamoecoexislib.dto.andgo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AvalDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Recommended as number to avoid thousands separators. */
    private BigDecimal amount;
    private String channel;
    private String city;
    private String reason;
    private String release1; // confirmar nombre exacto
}
