package com.prismo.accountapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDTO {

    @NotBlank(message = "Document number is required")
    @Size(min = 11, max = 11, message = "Document number must be exactly 11 characters")
    private String document_number;

}
