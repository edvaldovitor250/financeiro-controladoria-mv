package com.mv.financeiro_controladoria.application.dto;


public class ClientUpdateDTO {
    public String name;
    public String phone;
    public AddressDTO address;

    public String personType;

    public Individual individual;
    public Corporate corporate;

    public static class Individual {
        public String cpf;
    }
    public static class Corporate {
        public String cnpj;
        public String companyName;
    }
}
