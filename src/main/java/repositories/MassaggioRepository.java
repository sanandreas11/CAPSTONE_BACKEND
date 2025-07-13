package repositories;

import entities.Massaggio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MassaggioRepository extends JpaRepository<Massaggio, Long>, JpaSpecificationExecutor<Massaggio> {
}

