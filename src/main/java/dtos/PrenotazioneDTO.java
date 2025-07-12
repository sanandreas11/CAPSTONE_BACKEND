package dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PrenotazioneDTO {

    @NotNull(message = "La data/ora è obbligatoria")
    @Future(message = "La data/ora deve essere nel futuro")
    private LocalDateTime dataOra;

    @NotNull(message = "ID massaggio obbligatorio")
    private Long massaggioId;
}
