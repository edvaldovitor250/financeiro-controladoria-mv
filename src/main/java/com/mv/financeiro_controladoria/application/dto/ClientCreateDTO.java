package com.mv.financeiro_controladoria.application.dto;


import java.util.List;

public class ClientCreateDTO {
    public String name;
    public String phone;
    public String personType;
    public AddressDTO address;

    public IndividualDataDTO individual;
    public CorporateDataDTO corporate;

    public List<AccountDTO> accounts;

    public MovementCreateDTO initialMovement;
}