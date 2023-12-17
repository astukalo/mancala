package xyz.a5s7.mancala.app.rest.model;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public final class RegisterPlayerRequest {
    @NotNull
    String playerId;
}
