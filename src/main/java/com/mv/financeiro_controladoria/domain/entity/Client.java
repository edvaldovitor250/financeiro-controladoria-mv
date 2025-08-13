package com.mv.financeiro_controladoria.domain.entity;

import com.mv.financeiro_controladoria.domain.entity.enums.PersonType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(
        name = "CLIENTS",
        indexes = {
                @Index(name = "IDX_CLIENTS_NAME", columnList = "NAME")
        }
)
@Getter
@Setter
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(name = "NAME", length = 120, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "PERSON_TYPE", nullable = false, length = 2)
    private PersonType personType;

    @Embedded
    private Address address;

    @Size(max = 30)
    @Column(name = "PHONE", length = 30)
    private String phone;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Account> accounts = new HashSet<>();

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDate createdAt;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDate.now();
    }
}
