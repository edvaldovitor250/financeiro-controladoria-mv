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
        name = "CORPORATE_CLIENTS",
        indexes = {
                @Index(name = "IDX_CORP_CNPJ", columnList = "CNPJ", unique = true)
        }
)
@Getter
@Setter
public class CorporateClient extends Client {

    @NotBlank
    @Size(max = 18)
    @Column(name = "CNPJ", length = 18, unique = true, nullable = false)
    private String cnpj;

    @NotBlank
    @Size(max = 120)
    @Column(name = "COMPANY_NAME", length = 120, nullable = false)
    private String companyName;

    public CorporateClient() {
        setPersonType(PersonType.PJ);
    }
}
