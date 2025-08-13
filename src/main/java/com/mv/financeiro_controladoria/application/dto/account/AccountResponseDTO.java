package com.mv.financeiro_controladoria.application.dto.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountResponseDTO {
    private Long id;
    private String bank;
    private String number;
    private Boolean active;
    private Long clientId;
    private Long version;
}