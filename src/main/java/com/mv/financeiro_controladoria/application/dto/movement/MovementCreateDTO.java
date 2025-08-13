package com.mv.financeiro_controladoria.application.dto.movement;

import com.mv.financeiro_controladoria.domain.entity.enums.MovementType;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
public class MovementCreateDTO {
    @NotNull
    private MovementType type;

    @NotNull
    @Digits(integer = 17, fraction = 2)
    @Positive
    private BigDecimal amount;

    @Size(max = 180)
    public String description;

    public LocalDate date;

    public Long accountId;
}