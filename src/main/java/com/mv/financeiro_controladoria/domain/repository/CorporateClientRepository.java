package com.mv.financeiro_controladoria.domain.repository;

import com.mv.financeiro_controladoria.domain.model.CorporateClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorporateClientRepository extends JpaRepository<CorporateClient, Long> { }