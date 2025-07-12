package controllers;

import entities.Massaggio;
import entities.Prenotazione;
import entities.Utente;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import repositories.PrenotazioneRepository;
import repositories.UtenteRepository;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/statistiche")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final PrenotazioneRepository prenotazioneRepo;
    private final UtenteRepository utenteRepo;

    @GetMapping("/conta-utenti")
    public long contaUtenti() {
        return utenteRepo.count();
    }

    @GetMapping("/conta-prenotazioni")
    public long contaPrenotazioni() {
        return prenotazioneRepo.count();
    }

    @GetMapping("/guadagno-totale")
    public double guadagnoTotale() {
        return prenotazioneRepo.findAll().stream()
                .filter(Prenotazione::isPagato)
                .mapToDouble(p -> p.getMassaggio().getPrezzo())
                .sum();
    }

    @GetMapping("/prenotazioni-per-massaggiatore")
    public Map<String, Long> prenotazioniPerMassaggiatore() {
        List<Prenotazione> prenotazioni = prenotazioneRepo.findAll();

        return prenotazioni.stream()
                .filter(p -> p.getMassaggio() != null && p.getMassaggio().getMassaggiatore() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getMassaggio().getMassaggiatore().getEmail(),
                        Collectors.counting()
                ));
    }
}
