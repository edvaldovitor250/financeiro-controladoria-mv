package com.mv.financeiro_controladoria.application.mapper;// package com.mv.financeiro_controladoria.application.mapper;

import com.mv.financeiro_controladoria.application.dto.account.AccountCreateDTO;
import com.mv.financeiro_controladoria.application.dto.account.AccountResponseDTO;
import com.mv.financeiro_controladoria.application.dto.account.AccountUpdateDTO;
import com.mv.financeiro_controladoria.domain.entity.Account;
import lombok.var;

public final class AccountMapper {

    private AccountMapper() {}

    public static void apply(AccountUpdateDTO dto, Account acc) {
        acc.setBank(dto.getBank());
        acc.setNumber(dto.getNumber());
    }

    public static Account toEntity(AccountCreateDTO dto) {
        Account acc = new Account();
        acc.setBank(dto.getBank());
        acc.setNumber(dto.getNumber());
        acc.setActive(true);
        return acc;
    }

    public static AccountResponseDTO toResponse(Account acc) {
        AccountResponseDTO dto = new AccountResponseDTO();
        dto.setId(acc.getId());
        dto.setBank(acc.getBank());
        dto.setNumber(acc.getNumber());
        dto.setActive(acc.getActive());
        dto.setClientId(acc.getClient() != null ? acc.getClient().getId() : null);
        try {
            var versionField = acc.getClass().getDeclaredField("version");
            versionField.setAccessible(true);
            Object v = versionField.get(acc);
            dto.setVersion(v instanceof Number ? ((Number) v).longValue() : null);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            dto.setVersion(null);
        }
        return dto;
    }
}
