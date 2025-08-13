package com.mv.financeiro_controladoria.application.dto.common;

import com.mv.financeiro_controladoria.domain.entity.Address;

public class AddressDTO {
    public String street;
    public String city;
    public String state;
    public String zipCode;
    public String complement;

    public static AddressDTO from(Address a) {
        if (a == null) return null;
        AddressDTO d = new AddressDTO();
        d.street = a.getStreet();
        d.city = a.getCity();
        d.state = a.getState();
        d.zipCode = a.getZipCode();
        d.complement = a.getComplement();
        return d;
    }
}
