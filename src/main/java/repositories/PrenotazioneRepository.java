package repositories;

import entities.Prenotazione;
import entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {
    List<entities.Prenotazione> findByUtente(Utente utente);
}
