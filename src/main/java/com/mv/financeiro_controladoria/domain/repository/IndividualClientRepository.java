package com.mv.financeiro_controladoria.domain.repository;

import com.mv.financeiro_controladoria.domain.model.IndividualClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndividualClientRepository extends JpaRepository<IndividualClient, Long> { }