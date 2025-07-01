package controllers;

import entities.*;
import repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/prenotazioni")
@RequiredArgsConstructor
public class PrenotazioneController {

    private final PrenotazioneRepository prenotazioneRepo;
    private final MassaggioRepository massaggioRepo;
    private final UtenteRepository utenteRepo;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> creaPrenotazione(@RequestBody PrenotazioneDTO dto, Principal principal) {
        Utente utente = utenteRepo.findByEmail(principal.getName()).orElseThrow();
        Massaggio massaggio = massaggioRepo.findById(dto.getMassaggioId()).orElseThrow();

        Prenotazione p = Prenotazione.builder()
                .dataOra(dto.getDataOra())
                .massaggio(massaggio)
                .utente(utente)
                .build();

        prenotazioneRepo.save(p);
        return ResponseEntity.ok("Prenotazione salvata");
    }

    @GetMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public List<Prenotazione> getPrenotazioni(Principal principal) {
        Utente utente = utenteRepo.findByEmail(principal.getName()).orElseThrow();
        return prenotazioneRepo.findByUtente(utente);
    }
}
