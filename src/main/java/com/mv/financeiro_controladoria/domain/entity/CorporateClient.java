package com.mv.financeiro_controladoria.domain.entity;

import com.mv.financeiro_controladoria.domain.entity.enums.PersonType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "corporate_clients")
@Getter
@Setter
public class CorporateClient extends Client {

    @Size(max = 18)
    @Column(unique = true)
    private String cnpj;

    @Size(max = 120)
    private String companyName;

    public CorporateClient() {
        setPersonType(PersonType.PJ);
    }

}
