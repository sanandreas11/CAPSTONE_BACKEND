package entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prenotazione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataOra;

    @Column(nullable = false)
    private boolean pagato = false;

    private LocalDateTime dataPagamento;

    @ManyToOne
    private Utente utente;

    @ManyToOne
    private Massaggio massaggio;

    @Column(nullable = false)
    private boolean annullata = false;
}
