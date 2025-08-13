package com.mv.financeiro_controladoria.application.usecase;// package com.mv.financeiro_controladoria.application.usecase;

import com.mv.financeiro_controladoria.application.dto.account.AccountCreateDTO;
import com.mv.financeiro_controladoria.application.dto.account.AccountResponseDTO;
import com.mv.financeiro_controladoria.application.dto.account.AccountUpdateDTO;
import com.mv.financeiro_controladoria.application.mapper.AccountMapper;
import com.mv.financeiro_controladoria.domain.entity.Account;
import com.mv.financeiro_controladoria.domain.entity.Client;
import com.mv.financeiro_controladoria.infra.persistence.repository.AccountRepository;
import com.mv.financeiro_controladoria.infra.persistence.repository.ClientRepository;
import com.mv.financeiro_controladoria.infra.persistence.repository.MovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final MovementRepository movementRepository;

    @Transactional
    public AccountResponseDTO create(Long clientId, AccountCreateDTO dto) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        Account acc = AccountMapper.toEntity(dto);
        acc.setClient(client);

        acc = accountRepository.save(acc);
        return AccountMapper.toResponse(acc);
    }

    @Transactional
    public AccountResponseDTO update(Long id, AccountUpdateDTO dto) {
        Account acc = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

        if (Boolean.FALSE.equals(acc.getActive())) {
            throw new IllegalStateException("Conta inativa não pode ser alterada.");
        }

        if (movementRepository.countByAccount_Id(id) > 0) {
            throw new IllegalStateException("Conta com movimentações não pode ser alterada.");
        }

        AccountMapper.apply(dto, acc);

        return AccountMapper.toResponse(acc);
    }

    @Transactional
    public void deleteLogical(Long id) {
        Account acc = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

        if (Boolean.FALSE.equals(acc.getActive())) {
            return;
        }
        acc.setActive(false);
    }

    @Transactional(readOnly = true)
    public List<AccountResponseDTO> listByClient(Long clientId) {
        clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        return accountRepository.findByClientId(clientId)
                .stream()
                .map(AccountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccountResponseDTO get(Long id) {
        Account acc = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));
        return AccountMapper.toResponse(acc);
    }
}
