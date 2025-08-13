package com.mv.financeiro_controladoria.domain.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PersonType {
    PF("Pessoa Física"),
    PJ("Pessoa Jurídica");

    private final String description;

    PersonType(String description) {
        this.description = description;
    }

    @JsonValue
    public String getCode() {
        return name();
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static PersonType fromValue(String value) {
        for (PersonType type : values()) {
            if (type.name().equalsIgnoreCase(value)
                    || type.description.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de pessoa inválido: " + value);
    }
}
