package com.mv.financeiro_controladoria.application.dto.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class AllClientsBalanceReportDTO {
    public LocalDate date;
    public List<Item> items;

    public AllClientsBalanceReportDTO(LocalDate date, List<Item> items) {
        this.date = date; this.items = items;
    }
    public static class Item {
        public Long clientId;
        public String clientName;
        public LocalDate clientSince;
        public BigDecimal balanceAtDate;

        public Item(Long id, String name, LocalDate since, BigDecimal bal) {
            this.clientId = id; this.clientName = name; this.clientSince = since; this.balanceAtDate = bal;
        }
    }
}
