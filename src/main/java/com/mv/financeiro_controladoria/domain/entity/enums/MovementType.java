package com.mv.financeiro_controladoria.domain.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MovementType {
    RECEITA("Receita"),
    DESPESA("Despesa");

    private final String description;

    MovementType(String description) {
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
    public static MovementType fromValue(String value) {
        for (MovementType type : values()) {
            if (type.name().equalsIgnoreCase(value)
                    || type.description.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de movimentação inválido: " + value);
    }
}
