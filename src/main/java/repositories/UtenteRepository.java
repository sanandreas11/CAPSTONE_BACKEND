package repositories;

import entities.Utente;
import enumerations.Ruolo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtenteRepository extends JpaRepository<entities.Utente, Long> {
    Optional<Utente> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Utente> findByRuolo(Ruolo ruolo);
}
