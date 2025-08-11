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
public class StartFastProcessRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private AndgoContactPointDTO contactPoint;
    private List<String> customersIdentification;
    private AndgoUserDTO user;

    private String companyCode;
    private String creationBranchCode;
    private String externalReference;

    private SpecificInformationDTO specificInformation;
}
