package com.mv.financeiro_controladoria.domain.entity;

import com.mv.financeiro_controladoria.domain.entity.enums.PersonType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(
        name = "INDIVIDUAL_CLIENTS",
        indexes = {
                @Index(name = "IDX_INDIVIDUAL_CPF", columnList = "CPF", unique = true)
        }
)
@Getter
@Setter
public class IndividualClient extends Client {

    @NotBlank
    @Size(max = 14)
    @Column(name = "CPF", length = 14, unique = true, nullable = false)
    private String cpf;

    public IndividualClient() {
        setPersonType(PersonType.PF);
    }
}
