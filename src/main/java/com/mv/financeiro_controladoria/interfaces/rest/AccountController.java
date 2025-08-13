package com.mv.financeiro_controladoria.interfaces.rest;

import com.mv.financeiro_controladoria.application.dto.account.AccountCreateDTO;
import com.mv.financeiro_controladoria.application.dto.account.AccountResponseDTO;
import com.mv.financeiro_controladoria.application.dto.account.AccountUpdateDTO;
import com.mv.financeiro_controladoria.application.usecase.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "CRUD de contas bancárias de clientes")
@Validated
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Criar conta para um cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Conta criada"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Validação ou regra de negócio")
    })
    @PostMapping("/clients/{clientId}/accounts")
    public ResponseEntity<AccountResponseDTO> create(@PathVariable Long clientId,
                                                     @Valid @RequestBody AccountCreateDTO dto,
                                                     UriComponentsBuilder uriBuilder) {
        AccountResponseDTO saved = accountService.create(clientId, dto);
        URI location = uriBuilder.path("/api/accounts/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @Operation(summary = "Atualizar conta (bloqueado se já houver movimentações ou se inativa)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta atualizada"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada"),
            @ApiResponse(responseCode = "409", description = "Conflito de versão (otimista)")
    })
    @PutMapping("/accounts/{id}")
    public ResponseEntity<AccountResponseDTO> update(@PathVariable Long id,
                                                     @Valid @RequestBody AccountUpdateDTO dto) {
        return ResponseEntity.ok(accountService.update(id, dto));
    }

    @Operation(summary = "Exclusão lógica da conta (marca como inativa)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Conta inativada"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<Void> deleteLogical(@PathVariable Long id) {
        accountService.deleteLogical(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar contas por cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de contas"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/clients/{clientId}/accounts")
    public ResponseEntity<List<AccountResponseDTO>> listByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(accountService.listByClient(clientId));
    }

    @Operation(summary = "Buscar conta por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta encontrada"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @GetMapping("/accounts/{id}")
    public ResponseEntity<AccountResponseDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.get(id));
    }
}
