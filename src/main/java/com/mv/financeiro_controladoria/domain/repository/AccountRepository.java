package com.mv.financeiro_controladoria.domain.repository;

import com.mv.financeiro_controladoria.domain.model.Account;
import com.mv.financeiro_controladoria.domain.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByClient(Client client);


}