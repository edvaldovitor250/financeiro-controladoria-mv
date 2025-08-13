package com.mv.financeiro_controladoria.infra.persistence.repository;

import com.mv.financeiro_controladoria.domain.entity.Movement;
import com.mv.financeiro_controladoria.domain.entity.enums.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface MovementRepository extends JpaRepository<Movement, Long> {

    long countByClientIdAndType(Long clientId, MovementType type);

    long countByClientIdAndTypeAndDateBetween(Long clientId, MovementType type,
                                              LocalDate start, LocalDate end);

    long countByAccount_Id(Long accountId);

    List<Movement> findByClientId(Long clientId);

    List<Movement> findByClientIdAndType(Long clientId, MovementType type);

    List<Movement> findByClientIdAndDateBetween(Long clientId, LocalDate start, LocalDate end);

    List<Movement> findByClientIdAndTypeAndDateBetween(Long clientId, MovementType type, LocalDate start, LocalDate end);

    Movement getById(Long id);


    @Query("select coalesce(sum(m.amount), 0) " +
            "from Movement m " +
            "where m.client.id = :clientId " +
            "and   m.type = com.mv.financeiro_controladoria.domain.entity.enums.MovementType.RECEITA " +
            "and   m.date between :start and :end")
    BigDecimal sumCreditByClientAndPeriod(@Param("clientId") Long clientId,
                                          @Param("start") LocalDate start,
                                          @Param("end") LocalDate end);

    @Query("select coalesce(sum(m.amount), 0) " +
            "from Movement m " +
            "where m.client.id = :clientId " +
            "and   m.type = com.mv.financeiro_controladoria.domain.entity.enums.MovementType.DESPESA " +
            "and   m.date between :start and :end")
    BigDecimal sumDebitByClientAndPeriod(@Param("clientId") Long clientId,
                                         @Param("start") LocalDate start,
                                         @Param("end") LocalDate end);

    @Query("select count(m) " +
            "from Movement m " +
            "where m.client.id = :clientId " +
            "and   m.date between :start and :end")
    long countByClientAndPeriod(@Param("clientId") Long clientId,
                                @Param("start") LocalDate start,
                                @Param("end") LocalDate end);

    @Query("select coalesce(sum(m.amount), 0) " +
            "from Movement m " +
            "where m.client.id = :clientId " +
            "and   m.type = com.mv.financeiro_controladoria.domain.entity.enums.MovementType.RECEITA " +
            "and   m.date <= :date")
    BigDecimal sumCreditByClientUntil(@Param("clientId") Long clientId, @Param("date") LocalDate date);

    @Query("select coalesce(sum(m.amount), 0) " +
            "from Movement m " +
            "where m.client.id = :clientId " +
            "and   m.type = com.mv.financeiro_controladoria.domain.entity.enums.MovementType.DESPESA " +
            "and   m.date <= :date")
    BigDecimal sumDebitByClientUntil(@Param("clientId") Long clientId, @Param("date") LocalDate date);
}
