package repositories;

import entities.Prenotazione;
import entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {
    List<entities.Prenotazione> findByUtente(Utente utente);

List<Prenotazione> findByMassaggio_Massaggiatore(Utente massaggiatore);

    List<Prenotazione> findByPagatoFalse();

    List<Prenotazione> findByUtenteAndAnnullataFalse(Utente utente);

    List<Prenotazione> findByMassaggio_MassaggiatoreAndAnnullataFalse(Utente massaggiatore);

    List<Prenotazione> findByMassaggio_Massaggiatore_IdAndDataOraBetween(
            Long massaggiatoreId,
            LocalDateTime start,
            LocalDateTime end
    );
}

