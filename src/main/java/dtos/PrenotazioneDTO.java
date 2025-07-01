package dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PrenotazioneDTO {
    private LocalDateTime dataOra;
    private Long massaggioId;
}
