package com.mv.financeiro_controladoria.application.usecase;

import com.mv.financeiro_controladoria.application.dto.movement.MovementCreateDTO;
import com.mv.financeiro_controladoria.application.dto.movement.MovementResponseDTO;
import com.mv.financeiro_controladoria.domain.entity.Account;
import com.mv.financeiro_controladoria.domain.entity.Client;
import com.mv.financeiro_controladoria.domain.entity.Movement;
import com.mv.financeiro_controladoria.domain.entity.enums.MovementType;
import com.mv.financeiro_controladoria.infra.persistence.repository.AccountRepository;
import com.mv.financeiro_controladoria.infra.persistence.repository.ClientRepository;
import com.mv.financeiro_controladoria.infra.persistence.repository.MovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MovementService {

    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    public MovementResponseDTO createForClient(Long clientId, MovementCreateDTO dto) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        Movement m = new Movement();
        m.setClient(client);
        m.setType(dto.getType());
        m.setAmount(dto.getAmount().setScale(2));
        m.setDescription(dto.getDescription());
        m.setDate(dto.getDate() != null ? dto.getDate() : LocalDate.now());

        if (dto.getAccountId() != null) {
            Account acc = accountRepository.findById(dto.getAccountId())
                    .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

            if (!acc.getClient().getId().equals(client.getId())) {
                throw new IllegalStateException("Conta não pertence ao cliente informado.");
            }
            if (Boolean.FALSE.equals(acc.getActive())) {
                throw new IllegalStateException("Conta está inativa.");
            }
            m.setAccount(acc);
        }

        Movement saved = movementRepository.save(m);
        return MovementResponseDTO.from(saved);
    }

    // compatibilidade com versão antiga
    public MovementResponseDTO createForClient(Client client, MovementCreateDTO dto) {
        return createForClient(client.getId(), dto);
    }

    // ---------- MÉTODOS FALTANTES ----------

    @Transactional(readOnly = true)
    public List<MovementResponseDTO> listByClient(Long clientId, LocalDate start, LocalDate end, MovementType type) {
        if (!clientRepository.existsById(clientId)) {
            throw new EntityNotFoundException("Cliente não encontrado");
        }

        List<Movement> list;
        if (start != null && end != null && type != null) {
            list = movementRepository.findByClientIdAndTypeAndDateBetween(clientId, type, start, end);
        } else if (start != null && end != null) {
            list = movementRepository.findByClientIdAndDateBetween(clientId, start, end);
        } else if (type != null) {
            list = movementRepository.findByClientIdAndType(clientId, type);
        } else {
            list = movementRepository.findByClientId(clientId);
        }

        return list.stream()
                .map(MovementResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MovementResponseDTO getById(Long id) {
        Movement m = movementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movimentação não encontrada"));
        return MovementResponseDTO.from(m);
    }
}
