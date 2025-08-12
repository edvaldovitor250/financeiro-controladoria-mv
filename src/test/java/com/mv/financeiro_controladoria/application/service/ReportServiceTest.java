//package com.mv.financeiro_controladoria.application.service;
//
//import com.mv.financeiro_controladoria.application.dto.ClientBalanceReportDTO;
//import com.mv.financeiro_controladoria.application.dto.CompanyRevenueReportDTO;
//import com.mv.financeiro_controladoria.domain.model.Client;
//import com.mv.financeiro_controladoria.domain.repository.ClientRepository;
//import com.mv.financeiro_controladoria.domain.repository.MovementRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ReportServiceTest {
//
//    @Mock private ClientRepository clientRepository;
//    @Mock private MovementRepository movementRepository;
//
//    private ReportService service;
//
//    @BeforeEach
//    void setUp() {
//        service = new ReportService(clientRepository, movementRepository);
//    }
//
//    @Test
//    void clientBalance_ok() {
//        Long clientId = 1L;
//        Client c = new Client();
//        c.setId(clientId);
//        c.setName("Cliente A");
//
//        LocalDate start = LocalDate.of(2025, 1, 1);
//        LocalDate end   = LocalDate.of(2025, 1, 31);
//
//        when(clientRepository.findById(clientId)).thenReturn(Optional.of(c));
//        when(movementRepository.sumCreditByClientAndPeriod(clientId, start, end)).thenReturn(new BigDecimal("300.00"));
//        when(movementRepository.sumDebitByClientAndPeriod(clientId, start, end)).thenReturn(new BigDecimal("120.00"));
//        when(movementRepository.countByClientAndPeriod(clientId, start, end)).thenReturn(12L);
//
//        ClientBalanceReportDTO dto = service.clientBalance(clientId, start, end);
//
//        assertEquals(clientId, dto.clientId);
//        assertEquals("Cliente A", dto.clientName);
//        assertEquals(new BigDecimal("180.00"), dto.currentBalance);
//        assertEquals(12L, dto.totalCount);
//        assertEquals(new BigDecimal("9.00"), dto.feePaid);
//        assertEquals(start, dto.start);
//        assertEquals(end, dto.end);
//    }
//
//    @Test
//    void clientBalance_clienteNaoEncontrado() {
//        when(clientRepository.findById(99L)).thenReturn(Optional.empty());
//        assertThrows(IllegalArgumentException.class,
//                () -> service.clientBalance(99L, LocalDate.now().minusDays(10), LocalDate.now()));
//    }
//
//    @Test
//    void companyRevenue_ok() {
//        Client c1 = new Client(); c1.setId(1L); c1.setName("A");
//        Client c2 = new Client(); c2.setId(2L); c2.setName("B");
//        Client c3 = new Client(); c3.setId(3L); c3.setName("C");
//
//        when(clientRepository.findAll()).thenReturn(Arrays.asList(c1, c2, c3));
//
//        LocalDate start = LocalDate.of(2025, 2, 1);
//        LocalDate end   = LocalDate.of(2025, 2, 28);
//
//        when(movementRepository.countByClientAndPeriod(1L, start, end)).thenReturn(5L);
//        when(movementRepository.countByClientAndPeriod(2L, start, end)).thenReturn(15L);
//        when(movementRepository.countByClientAndPeriod(3L, start, end)).thenReturn(25L);
//
//        CompanyRevenueReportDTO dto = service.companyRevenue(start, end);
//
//        assertEquals(start, dto.start);
//        assertEquals(end, dto.end);
//        assertEquals(3, dto.items.size());
//
//        CompanyRevenueReportDTO.Item i1 = dto.items.stream().filter(i -> i.clientId.equals(1L)).findFirst().orElseThrow();
//        CompanyRevenueReportDTO.Item i2 = dto.items.stream().filter(i -> i.clientId.equals(2L)).findFirst().orElseThrow();
//        CompanyRevenueReportDTO.Item i3 = dto.items.stream().filter(i -> i.clientId.equals(3L)).findFirst().orElseThrow();
//
//        assertEquals(5L, i1.totalMovements);
//        assertEquals(new BigDecimal("5.00"), i1.fee);
//
//        assertEquals(15L, i2.totalMovements);
//        assertEquals(new BigDecimal("11.25"), i2.fee);
//
//        assertEquals(25L, i3.totalMovements);
//        assertEquals(new BigDecimal("12.50"), i3.fee);
//
//        assertEquals(new BigDecimal("28.75"), dto.totalRevenue);
//    }
//}
