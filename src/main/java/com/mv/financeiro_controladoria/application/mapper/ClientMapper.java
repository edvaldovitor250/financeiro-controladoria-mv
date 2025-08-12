package com.mv.financeiro_controladoria.application.mapper;

import com.mv.financeiro_controladoria.application.dto.AccountDTO;
import com.mv.financeiro_controladoria.application.dto.AddressDTO;
import com.mv.financeiro_controladoria.application.dto.ClientCreateDTO;
import com.mv.financeiro_controladoria.domain.model.*;

import java.util.stream.Collectors;

public final class ClientMapper {

    private ClientMapper() { }

    public static Client toEntity(ClientCreateDTO dto) {
        Address address = toAddress(dto.address);

        if ("PF".equalsIgnoreCase(dto.personType)) {
            IndividualClient pf = new IndividualClient();
            pf.setName(dto.name);
            pf.setPhone(dto.phone);
            pf.setAddress(address);
            if (dto.individual != null) {
                pf.setCpf(dto.individual.cpf);
            }
            if (dto.accounts != null) {
                pf.setAccounts(dto.accounts.stream().map(a -> toAccount(a, pf)).collect(Collectors.toSet()));
            }
            return pf;
        } else {
            CorporateClient pj = new CorporateClient();
            pj.setName(dto.name);
            pj.setPhone(dto.phone);
            pj.setAddress(address);
            if (dto.corporate != null) {
                pj.setCnpj(dto.corporate.cnpj);
                pj.setCompanyName(dto.corporate.companyName);
            }
            if (dto.accounts != null) {
                pj.setAccounts(dto.accounts.stream().map(a -> toAccount(a, pj)).collect(Collectors.toSet()));
            }
            return pj;
        }
    }

    private static Address toAddress(AddressDTO a) {
        if (a == null) return null;
        Address address = new Address();
        address.setStreet(a.street);
        address.setCity(a.city);
        address.setState(a.state);
        address.setZipCode(a.zipCode);
        address.setComplement(a.complement);
        return address;
    }

    private static Account toAccount(AccountDTO dto, Client owner) {
        Account acc = new Account();
        acc.setBank(dto.bank);
        acc.setNumber(dto.number);
        acc.setClient(owner);
        return acc;
    }
}
