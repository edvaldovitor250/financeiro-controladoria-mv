package com.mv.financeiro_controladoria.infra.persistence.repository;

import com.mv.financeiro_controladoria.domain.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("select count(ic) from IndividualClient ic where ic.cpf = :cpf")
    long countByCpf(@Param("cpf") String cpf);

    @Query("select count(pj) from CorporateClient pj where pj.cnpj = :cnpj")
    long countByCnpj(@Param("cnpj") String cnpj);

}

