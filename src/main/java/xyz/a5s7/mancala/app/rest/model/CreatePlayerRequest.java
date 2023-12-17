package xyz.a5s7.mancala.app.rest.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public final class CreatePlayerRequest {
    @NotNull
    @Size(min = 1, max = 100)
    String playerName;
}
