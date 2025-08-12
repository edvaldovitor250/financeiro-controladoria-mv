package com.mv.financeiro_controladoria.application.service;

import com.mv.financeiro_controladoria.application.dto.AccountDTO;
import com.mv.financeiro_controladoria.domain.model.Account;
import com.mv.financeiro_controladoria.domain.repository.AccountRepository;
import com.mv.financeiro_controladoria.domain.repository.MovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;

    public AccountService(AccountRepository accountRepository,
                          MovementRepository movementRepository) {
        this.accountRepository = accountRepository;
        this.movementRepository = movementRepository;
    }

    @Transactional
    public Account update(Long id, AccountDTO dto) {
        Account acc = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

        if (movementRepository.countByAccount_Id(id) > 0) {
            throw new IllegalStateException("Conta com movimentações não pode ser alterada.");
        }

        if (Boolean.FALSE.equals(acc.getActive())) {
            throw new IllegalStateException("Conta inativa não pode ser alterada.");
        }

        acc.setBank(dto.bank);
        acc.setNumber(dto.number);
        return accountRepository.save(acc);
    }

    @Transactional
    public void deleteLogical(Long id) {
        Account acc = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
        acc.setActive(false);
        accountRepository.save(acc);
    }
}
