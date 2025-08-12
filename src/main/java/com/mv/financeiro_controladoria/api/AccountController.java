package com.mv.financeiro_controladoria.api;

import com.mv.financeiro_controladoria.application.dto.AccountDTO;
import com.mv.financeiro_controladoria.application.service.AccountService;
import com.mv.financeiro_controladoria.domain.model.Account;
import com.mv.financeiro_controladoria.domain.model.Client;
import com.mv.financeiro_controladoria.domain.repository.AccountRepository;
import com.mv.financeiro_controladoria.domain.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    @PostMapping("/clients/{clientId}/accounts")
    public ResponseEntity<Account> create(@PathVariable Long clientId, @RequestBody AccountDTO dto) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        Account acc = new Account();
        acc.setClient(client);
        acc.setBank(dto.bank);
        acc.setNumber(dto.number);
        acc.setActive(true);
        Account saved = accountRepository.save(acc);
        return ResponseEntity.created(URI.create("/api/accounts/" + saved.getId())).body(saved);
    }

    @PutMapping("/accounts/{id}")
    public ResponseEntity<Account> update(@PathVariable Long id, @RequestBody AccountDTO dto) {
        return ResponseEntity.ok(accountService.update(id, dto));
    }

    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<Void> deleteLogical(@PathVariable Long id) {
        accountService.deleteLogical(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/clients/{clientId}/accounts")
    public ResponseEntity<List<Account>> listByClient(@PathVariable Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        return ResponseEntity.ok(accountRepository.findByClient(client));
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<Account> get(@PathVariable Long id) {
        Account acc = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
        return ResponseEntity.ok(acc);
    }
}
