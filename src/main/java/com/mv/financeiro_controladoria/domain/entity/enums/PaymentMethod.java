package com.mv.financeiro_controladoria.domain.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {
    CREDITO,
    DEBITO,
    PIX,
    BOLETO,
    TED,
    DOC,
    TRANSFERENCIA;

    @JsonValue
    public String json() { return name(); }

    @JsonCreator
    public static PaymentMethod from(String v) {
        for (PaymentMethod pm : values()) {
            if (pm.name().equalsIgnoreCase(v)) return pm;
        }
        throw new IllegalArgumentException("Forma de pagamento inválida: " + v);
    }
}
