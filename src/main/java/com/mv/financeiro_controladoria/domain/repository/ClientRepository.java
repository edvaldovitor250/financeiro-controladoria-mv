package com.mv.financeiro_controladoria.domain.repository;

import com.mv.financeiro_controladoria.domain.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> { }