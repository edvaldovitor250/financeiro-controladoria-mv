package com.mv.financeiro_controladoria.domain.repository;

import com.mv.financeiro_controladoria.domain.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> { }