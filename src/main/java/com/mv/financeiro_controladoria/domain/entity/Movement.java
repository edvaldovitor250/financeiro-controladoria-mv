package com.mv.financeiro_controladoria.domain.entity;

import com.mv.financeiro_controladoria.domain.entity.enums.MovementType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "MOVEMENTS",
        indexes = {
                @Index(name = "IDX_MOV_CLIENT", columnList = "CLIENT_ID"),
                @Index(name = "IDX_MOV_ACCOUNT", columnList = "ACCOUNT_ID"),
                @Index(name = "IDX_MOV_DATE", columnList = "MOV_DATE")
        }
)
@Getter
@Setter
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "MOV_TYPE", nullable = false, length = 10)
    private MovementType type;

    @NotNull
    @Column(name = "AMOUNT", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Size(max = 180)
    @Column(name = "DESCRIPTION", length = 180)
    private String description;

    @NotNull
    @Column(name = "MOV_DATE", nullable = false)
    private LocalDate date;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_MOV_CLIENT"))
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID", foreignKey = @ForeignKey(name = "FK_MOV_ACCOUNT"))
    private Account account;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version;
}
