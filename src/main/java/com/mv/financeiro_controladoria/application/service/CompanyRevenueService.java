package com.mv.financeiro_controladoria.application.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class CompanyRevenueService {

    private final JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall fnClientNetBalance;

    public CompanyRevenueService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        // FUNCTION schema.FN_CLIENT_NET_BALANCE(p_client_id IN NUMBER) RETURN NUMBER
        this.fnClientNetBalance = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("XPTO_PKG")      // se estiver dentro de um pacote; ajuste conforme seu objeto
                .withFunctionName("FN_CLIENT_NET_BALANCE");
    }

    public Number getClientNetBalance(Long clientId) {
        Map<String, Object> in = new HashMap<String, Object>();
        in.put("P_CLIENT_ID", clientId);
        // Quando a função não está em package, remova withCatalogName e chame: fnClientNetBalance.executeFunction(...)
        return fnClientNetBalance.executeFunction(Number.class, in);
    }
}
