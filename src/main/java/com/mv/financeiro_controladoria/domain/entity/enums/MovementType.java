package com.mv.financeiro_controladoria.domain.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MovementType {
    RECEITA("Receita"),
    DESPESA("Despesa");

    private final String description;


    @JsonValue
    public String getCode() {
        return name();
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
