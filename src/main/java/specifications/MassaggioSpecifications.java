package specifications;

import entities.Massaggio;
import org.springframework.data.jpa.domain.Specification;

public class MassaggioSpecifications {

    public static Specification<Massaggio> hasPrezzoMinimo(Double min) {
        return (root, query, cb) -> min != null ? cb.greaterThanOrEqualTo(root.get("prezzo"), min) : null;
    }

    public static Specification<Massaggio> hasPrezzoMassimo(Double max) {
        return (root, query, cb) -> max != null ? cb.lessThanOrEqualTo(root.get("prezzo"), max) : null;
    }

    public static Specification<Massaggio> hasDurataMinima(Integer min) {
        return (root, query, cb) -> min != null ? cb.greaterThanOrEqualTo(root.get("durata"), min) : null;
    }

    public static Specification<Massaggio> hasDurataMassima(Integer max) {
        return (root, query, cb) -> max != null ? cb.lessThanOrEqualTo(root.get("durata"), max) : null;
    }
}
