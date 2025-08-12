package com.mv.financeiro_controladoria.domain.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

@Embeddable
@Getter
@Setter

public class Address {

    @Size(max = 120)
    private String street;

    @Size(max = 60)
    private String city;

    @Size(max = 60)
    private String state;

    @Size(max = 12)
    private String zipCode;

    @Size(max = 120)
    private String complement;
}
