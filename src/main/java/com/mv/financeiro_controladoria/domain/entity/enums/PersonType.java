package com.mv.financeiro_controladoria.domain.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PersonType {
    PF("Pessoa Física"),
    PJ("Pessoa Jurídica");

    private final String description;

    @JsonValue
    public String getCode() {
        return name();
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
