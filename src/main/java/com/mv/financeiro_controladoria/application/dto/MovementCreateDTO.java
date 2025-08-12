package com.mv.financeiro_controladoria.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MovementCreateDTO {
    public String type;
    public BigDecimal amount;
    public String description;
    public LocalDate date;
    public Long accountId;
}