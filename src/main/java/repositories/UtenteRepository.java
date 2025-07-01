package repositories;

import entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<entities.Utente, Long> {
    Optional<Utente> findByEmail(String email);
}
