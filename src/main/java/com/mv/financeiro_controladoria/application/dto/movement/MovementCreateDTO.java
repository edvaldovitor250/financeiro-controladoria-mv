package com.mv.financeiro_controladoria.application.dto.movement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mv.financeiro_controladoria.domain.entity.enums.MovementType;
import com.mv.financeiro_controladoria.domain.entity.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class MovementCreateDTO {

    @NotNull(message = "type é obrigatório (RECEITA ou DESPESA)")
    private MovementType type;

    @NotNull(message = "paymentMethod é obrigatório (ex.: CREDITO, DEBITO, PIX)")
    private PaymentMethod paymentMethod;

    @NotNull(message = "amount é obrigatório")
    @Digits(integer = 17, fraction = 2, message = "amount deve ter no máx 17 inteiros e 2 decimais")
    @Positive(message = "amount deve ser positivo")
    private BigDecimal amount;

    @Size(max = 180, message = "description pode ter no máximo 180 caracteres")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Positive(message = "accountId deve ser positivo")
    private Long accountId;
}
