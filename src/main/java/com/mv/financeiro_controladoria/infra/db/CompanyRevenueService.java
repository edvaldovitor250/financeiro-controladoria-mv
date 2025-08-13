package com.mv.financeiro_controladoria.infra.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class CompanyRevenueService {

    private final JdbcTemplate jdbc;
    private SimpleJdbcCall fnCompanyRevenue;
    private SimpleJdbcCall fnClientNetBalance;

    public CompanyRevenueService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @PostConstruct
    public void init() {
        this.fnCompanyRevenue = new SimpleJdbcCall(jdbc)
                .withCatalogName("XPTO_PKG")
                .withFunctionName("FN_COMPANY_REVENUE");

        this.fnClientNetBalance = new SimpleJdbcCall(jdbc)
                .withCatalogName("XPTO_PKG")
                .withFunctionName("FN_CLIENT_NET_BALANCE");
    }

    public BigDecimal revenue(LocalDate start, LocalDate end) {
        Map<String,Object> in = new HashMap<>();
        in.put("P_START_DATE", java.sql.Date.valueOf(start));
        in.put("P_END_DATE", java.sql.Date.valueOf(end));
        Number n = fnCompanyRevenue.executeFunction(Number.class, in);
        return new BigDecimal(n.toString());
    }

    public BigDecimal getClientNetBalance(Long clientId) {
        Map<String,Object> in = new HashMap<>();
        in.put("P_CLIENT_ID", clientId);
        Number n = fnClientNetBalance.executeFunction(Number.class, in);
        return new BigDecimal(n.toString());
    }
}
