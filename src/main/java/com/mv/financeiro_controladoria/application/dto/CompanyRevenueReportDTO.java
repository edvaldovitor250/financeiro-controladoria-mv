package com.mv.financeiro_controladoria.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CompanyRevenueReportDTO {
    public LocalDate start;
    public LocalDate end;
    public List<Item> clients;
    public BigDecimal total;

    public CompanyRevenueReportDTO(LocalDate s, LocalDate e, List<Item> items, BigDecimal t) {
        this.start = s; this.end = e; this.clients = items; this.total = t;
    }

    public static class Item {
        public Long clientId;
        public String clientName;
        public long movementCount;
        public BigDecimal amount;
        public Item(Long id, String name, long count, BigDecimal amount) {
            this.clientId = id; this.clientName = name; this.movementCount = count; this.amount = amount;
        }
    }
}
