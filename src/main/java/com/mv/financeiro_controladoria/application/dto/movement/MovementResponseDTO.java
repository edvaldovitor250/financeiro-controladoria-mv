package com.mv.financeiro_controladoria.application.dto.movement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mv.financeiro_controladoria.domain.entity.Movement;
import com.mv.financeiro_controladoria.domain.entity.enums.MovementType;
import com.mv.financeiro_controladoria.domain.entity.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class MovementResponseDTO {

    private Long id;
    private MovementType type;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private Long clientId;
    private Long accountId;
    private String accountNumber;
    private String bank;

    public static MovementResponseDTO from(Movement m) {
        MovementResponseDTO d = new MovementResponseDTO();
        d.setId(m.getId());
        d.setType(m.getType());
        d.setPaymentMethod(m.getPaymentMethod());
        d.setAmount(m.getAmount());
        d.setDescription(m.getDescription());
        d.setDate(m.getDate());
        d.setClientId(m.getClient() != null ? m.getClient().getId() : null);

        if (m.getAccount() != null) {
            d.setAccountId(m.getAccount().getId());
            d.setAccountNumber(m.getAccount().getNumber());
            d.setBank(m.getAccount().getBank());
        }
        return d;
    }
}
