package com.mv.financeiro_controladoria.domain.model;

import com.mv.financeiro_controladoria.domain.model.enums.PersonType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@Table(name = "individual_clients")
public class IndividualClient extends Client {

    @Size(max = 14)
    @Column(unique = true)
    private String cpf;

    public IndividualClient() {
        setPersonType(PersonType.PF);
    }

}
