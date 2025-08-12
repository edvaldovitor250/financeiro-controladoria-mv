package com.mv.financeiro_controladoria.application.service;

import com.mv.financeiro_controladoria.application.dto.MovementCreateDTO;
import com.mv.financeiro_controladoria.domain.model.Account;
import com.mv.financeiro_controladoria.domain.model.Client;
import com.mv.financeiro_controladoria.domain.model.Movement;
import com.mv.financeiro_controladoria.domain.model.enums.MovementType;
import com.mv.financeiro_controladoria.domain.repository.AccountRepository;
import com.mv.financeiro_controladoria.domain.repository.MovementRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class MovementService {

    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;


    @Transactional
    public Movement createForClient(Client client, MovementCreateDTO dto) {
        Movement m = new Movement();
        m.setClient(client);
        m.setType(MovementType.valueOf(dto.type.toUpperCase()));
        m.setAmount(dto.amount);
        m.setDescription(dto.description);
        m.setDate(dto.date != null ? dto.date : LocalDate.now());

        if (dto.accountId != null) {
            Account acc = accountRepository.findById(dto.accountId)
                    .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
            m.setAccount(acc);
        }

        return movementRepository.save(m);
    }
}
