package com.mv.financeiro_controladoria.application.dto.movement;

import lombok.Getter; import lombok.Setter;
import java.math.BigDecimal; import java.time.LocalDate;
import com.mv.financeiro_controladoria.domain.entity.Movement;

@Getter @Setter
public class MovementResponseDTO {
    private Long id;
    private String type;
    private BigDecimal amount;
    private String description;
    private LocalDate date;

    public static MovementResponseDTO from(Movement m) {
        MovementResponseDTO d = new MovementResponseDTO();
        d.setId(m.getId());
        d.setType(m.getType() != null ? m.getType().name() : null);
        d.setAmount(m.getAmount());
        d.setDescription(m.getDescription());
        d.setDate(m.getDate());
        return d;
    }
}
