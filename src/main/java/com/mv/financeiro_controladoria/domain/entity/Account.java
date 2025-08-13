package com.mv.financeiro_controladoria.domain.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(
        name = "ACCOUNTS",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_ACCOUNTS_CLIENT_ACCNUMBER", columnNames = {"CLIENT_ID", "ACC_NUMBER"})
        },
        indexes = {
                @Index(name = "IDX_ACCOUNTS_CLIENT", columnList = "CLIENT_ID")
        }
)
@Getter
@Setter
@EqualsAndHashCode(of = {"client", "number"})
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 40)
    @Column(name = "BANK", length = 40, nullable = false)
    private String bank;

    @NotBlank
    @Size(max = 20)
    @Column(name = "ACC_NUMBER", length = 20, nullable = false)
    private String number;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ACCOUNTS_CLIENT"))
    private Client client;

    @Column(name = "ACTIVE", nullable = false )
    private Boolean active = true;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version;
}
