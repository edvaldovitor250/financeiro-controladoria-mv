package com.mv.financeiro_controladoria.application.service;

import com.mv.financeiro_controladoria.infra.db.CompanyRevenueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyRevenueServiceTest {

    @Mock private JdbcTemplate jdbc;
    @Mock private SimpleJdbcCall fnCompanyRevenue;
    @Mock private SimpleJdbcCall fnClientNetBalance;

    private CompanyRevenueService service;

    @BeforeEach
    void setUp() throws Exception {
        service = new CompanyRevenueService(jdbc);
        setPrivate(service, "fnCompanyRevenue", fnCompanyRevenue);
        setPrivate(service, "fnClientNetBalance", fnClientNetBalance);
    }

    private static void setPrivate(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    void revenue_deve_retornar_valor_e_enviar_parametros_corretos() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end   = LocalDate.of(2025, 1, 31);

        when(fnCompanyRevenue.executeFunction(eq(Number.class), anyMap()))
                .thenReturn(42);

        BigDecimal result = service.revenue(start, end);
        assertEquals(new BigDecimal("42"), result);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(fnCompanyRevenue).executeFunction(eq(Number.class), captor.capture());
        Map<String, Object> in = captor.getValue();

        assertEquals(Date.valueOf(start), in.get("P_START_DATE"));
        assertEquals(Date.valueOf(end),   in.get("P_END_DATE"));
        verifyNoMoreInteractions(fnCompanyRevenue);
    }

    @Test
    void getClientNetBalance_deve_retornar_valor_e_enviar_parametro_corretamente() {
        when(fnClientNetBalance.executeFunction(eq(Number.class), anyMap()))
                .thenReturn(1250.75);

        BigDecimal val = service.getClientNetBalance(99L);
        assertEquals(new BigDecimal("1250.75"), val);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(fnClientNetBalance).executeFunction(eq(Number.class), captor.capture());
        assertEquals(99L, captor.getValue().get("P_CLIENT_ID"));
        verifyNoMoreInteractions(fnClientNetBalance);
    }

    @Test
    void revenue_quando_oracle_retorna_null_lanca_NPE_atual() {
        when(fnCompanyRevenue.executeFunction(eq(Number.class), anyMap()))
                .thenReturn(null);

        assertThrows(NullPointerException.class,
                () -> service.revenue(LocalDate.now(), LocalDate.now()));
    }
}
