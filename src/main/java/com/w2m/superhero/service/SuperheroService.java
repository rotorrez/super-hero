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
public class StartFastProcessResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String status;                 // e.g. "OK"
    private String message;                // e.g. "Satisfactory execution."
    private List<NextTaskInfo> tasks;      // puede venir vacío []
}
