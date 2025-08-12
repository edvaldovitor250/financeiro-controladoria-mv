//package com.mv.financeiro_controladoria.application.service;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class ClientApiTest {
//
//    @Autowired
//    MockMvc mvc;
//
//    @Test
//    void cria_cliente_com_mov_inicial() throws Exception {
//        String json = "{ \"name\":\"Cliente Y\",\"personType\":\"PF\", " +
//                "\"initialMovement\":{\"type\":\"RECEITA\",\"amount\":100.00} }";
//        mvc.perform(post("/api/clients")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").exists());
//    }
//}
