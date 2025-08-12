package com.mv.financeiro_controladoria.domain.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 40)
    private String bank;

    @Size(max = 20)
    private String number;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Client client;


}
