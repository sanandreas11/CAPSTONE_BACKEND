package dtos;

import lombok.Data;

@Data
public class MassaggioDTO {
    private String tipo;
    private double prezzo;
    private int durata;
    private Long massaggiatoreId;
}
