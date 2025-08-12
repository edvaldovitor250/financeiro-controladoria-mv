package com.mv.financeiro_controladoria.domain.model;

import com.mv.financeiro_controladoria.domain.model.enums.MovementType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "movements")
@Getter
@Setter
public class Movement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MovementType type;

    @NotNull
    private BigDecimal amount;

    @Column(length = 180)
    private String description;

    @NotNull
    private LocalDate date;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

}