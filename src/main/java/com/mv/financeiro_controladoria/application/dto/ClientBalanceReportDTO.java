package com.mv.financeiro_controladoria.application.dto;

import com.mv.financeiro_controladoria.domain.model.Client;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ClientBalanceReportDTO {
    public Long clientId;
    public String clientName;
    public LocalDate clientSince;
    public AddressDTO address;
    public long creditCount;
    public long debitCount;
    public long totalCount;
    public BigDecimal feePaid;
    public BigDecimal initialBalance;
    public BigDecimal currentBalance;
    public LocalDate start;
    public LocalDate end;

    public static ClientBalanceReportDTO of(Client c, BigDecimal credit, BigDecimal debit,
                                            long total, BigDecimal fee, BigDecimal initial, BigDecimal current,
                                            LocalDate start, LocalDate end) {
        ClientBalanceReportDTO d = new ClientBalanceReportDTO();
        d.clientId = c.getId();
        d.clientName = c.getName();
        d.clientSince = c.getCreatedAt();
        d.address = AddressDTO.from(c.getAddress());
        d.creditCount = 0;
        d.debitCount  = 0;
        d.totalCount  = total;
        d.feePaid = fee;
        d.initialBalance = initial;
        d.currentBalance = current;
        d.start = start; d.end = end;
        return d;
    }
}
