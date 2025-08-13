package com.mv.financeiro_controladoria.application.dto.account;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class AccountUpdateDTO {
    @NotBlank
    @Size(max = 40)
    public String bank;

    @NotBlank
    @Size(max = 20)
    public String number;

    public Long version;
}