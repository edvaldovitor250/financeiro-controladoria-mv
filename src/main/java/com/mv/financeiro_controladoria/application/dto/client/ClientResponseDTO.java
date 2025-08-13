package com.mv.financeiro_controladoria.application.dto.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientResponseDTO {
    public Long id;
    public String name;
    public String phone;
    public String personType;
}