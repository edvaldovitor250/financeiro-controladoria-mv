package com.mv.financeiro_controladoria.application.usecase;

import com.mv.financeiro_controladoria.application.dto.account.AccountDTO;
import com.mv.financeiro_controladoria.domain.entity.Account;
import com.mv.financeiro_controladoria.infra.persistence.repository.AccountRepository;
import com.mv.financeiro_controladoria.infra.persistence.repository.MovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;

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
