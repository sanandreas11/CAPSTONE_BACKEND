package repositories;

import entities.Massaggio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MassaggioRepository extends JpaRepository<Massaggio, Long> {
}

