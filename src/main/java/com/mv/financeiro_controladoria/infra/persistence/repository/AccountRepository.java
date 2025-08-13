package com.mv.financeiro_controladoria.infra.persistence.repository;

import com.mv.financeiro_controladoria.domain.entity.Account;
import com.mv.financeiro_controladoria.domain.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByClient(Client client);


}