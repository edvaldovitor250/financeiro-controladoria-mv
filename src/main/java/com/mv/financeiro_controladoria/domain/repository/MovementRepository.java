package com.mv.financeiro_controladoria.domain.repository;

import com.mv.financeiro_controladoria.domain.model.Movement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovementRepository extends JpaRepository<Movement, Long> { }