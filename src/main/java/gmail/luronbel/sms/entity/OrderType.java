package gmail.luronbel.sms.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Order type.
 */
@RequiredArgsConstructor
public enum OrderType {
    S("sell"), B("buy");

    @Getter
    private final String operation;
}