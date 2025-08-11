package com.santander.san.audobs.sanaudobsbamoecoexislib.dto.andgo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NextTaskInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String caseIdentifier;
    private String taskCode;
    private String taskDescription;
    private String url;
}
