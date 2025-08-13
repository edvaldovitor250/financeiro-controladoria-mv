package com.mv.financeiro_controladoria.domain.entity;

import com.mv.financeiro_controladoria.domain.entity.enums.PersonType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "clients")
@Getter
@Setter
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "person_type", nullable = false, length = 3)
    private PersonType personType;

    @Embedded
    private Address address;

    @Column(length = 30)
    private String phone;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Account> accounts = new HashSet<>();

    @Column(name="created_at", nullable=false)
    private LocalDate createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDate.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public PersonType getPersonType() { return personType; }
    public void setPersonType(PersonType personType) { this.personType = personType; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Set<Account> getAccounts() { return accounts; }
    public void setAccounts(Set<Account> accounts) { this.accounts = accounts; }
}
