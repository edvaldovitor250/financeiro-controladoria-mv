package com.mv.financeiro_controladoria.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Address {

    @NotBlank
    @Size(max = 120)
    @Column(name = "STREET", length = 120, nullable = false)
    private String street;

    @NotBlank
    @Size(max = 60)
    @Column(name = "CITY", length = 60, nullable = false)
    private String city;

    @NotBlank
    @Size(max = 60)
    @Column(name = "STATE", length = 60, nullable = false)
    private String state;

    @NotBlank
    @Size(max = 12)
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "CEP deve estar no formato 99999-999")
    @Column(name = "ZIP_CODE", length = 12, nullable = false)
    private String zipCode;

    @Size(max = 120)
    @Column(name = "COMPLEMENT", length = 120)
    private String complement;
}
