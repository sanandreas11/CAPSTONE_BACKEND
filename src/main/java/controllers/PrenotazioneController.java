package controllers;

import dtos.PrenotazioneDTO;
import entities.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import services.StripeService;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prenotazioni")
@RequiredArgsConstructor
public class PrenotazioneController {

    private final PrenotazioneRepository prenotazioneRepo;
    private final MassaggioRepository massaggioRepo;
    private final UtenteRepository utenteRepo;
    private final StripeService stripeService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> creaPrenotazione(@RequestBody @Valid PrenotazioneDTO dto, Principal principal) {
        Utente utente = utenteRepo.findByEmail(principal.getName()).orElseThrow();
        Massaggio massaggio = massaggioRepo.findById(dto.getMassaggioId()).orElseThrow();

        boolean occupato = !prenotazioneRepo
                .findByMassaggio_Massaggiatore_IdAndDataOraAndAnnullataFalse(
                        massaggio.getMassaggiatore().getId(),
                        dto.getDataOra()
                ).isEmpty();

        if (occupato) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Il massaggiatore è già occupato in quella fascia oraria.");
        }

        Prenotazione p = Prenotazione.builder()
                .dataOra(dto.getDataOra())
                .massaggio(massaggio)
                .utente(utente)
                .build();

        prenotazioneRepo.save(p);
        return ResponseEntity.ok("Prenotazione salvata con successo");
    }

    @GetMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public List<Prenotazione> getPrenotazioni(Principal principal) {
        Utente utente = utenteRepo.findByEmail(principal.getName()).orElseThrow();
        return prenotazioneRepo.findByUtente(utente);
    }

    @GetMapping("/assegnate")
    @PreAuthorize("hasRole('MASSAGGIATORE')")
    public ResponseEntity<List<Prenotazione>> getPrenotazioniAssegnate(Principal principal) {
        Utente massaggiatore = utenteRepo.findByEmail(principal.getName()).orElseThrow();
        List<Prenotazione> prenotazioni = prenotazioneRepo.findByMassaggio_Massaggiatore(massaggiatore);
        return ResponseEntity.ok(prenotazioni);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/{id}/paga")
    public ResponseEntity<String> pagaPrenotazione(@PathVariable Long id, Principal principal) {
        Prenotazione prenotazione = prenotazioneRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));

        if (!prenotazione.getUtente().getEmail().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non puoi pagare questa prenotazione");
        }

        if (prenotazione.isPagato()) {
            return ResponseEntity.badRequest().body("Prenotazione già pagata");
        }

        prenotazione.setPagato(true);
        prenotazione.setDataPagamento(LocalDateTime.now());
        prenotazioneRepo.save(prenotazione);

        return ResponseEntity.ok("Pagamento registrato");
}
    @GetMapping("/non-pagate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASSAGGIATORE')")
    public List<Prenotazione> getPrenotazioniNonPagate() {
        return prenotazioneRepo.findByPagatoFalse();
    }
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/{id}/checkout")
    public ResponseEntity<String> checkout(@PathVariable Long id, Principal principal) throws Exception {
        Prenotazione prenotazione = prenotazioneRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));

        if (!prenotazione.getUtente().getEmail().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non autorizzato");
        }

        int prezzoMassaggioInCents = (int) Math.round(prenotazione.getMassaggio().getPrezzo() * 100);
        String sessionUrl = stripeService.createCheckoutSession(id, prezzoMassaggioInCents);
        return ResponseEntity.ok(sessionUrl);
    }
    @PreAuthorize("hasRole('CLIENTE')")
    @PutMapping("/{id}/annulla")
    public ResponseEntity<String> annullaPrenotazione(@PathVariable Long id, Principal principal) {
        Prenotazione prenotazione = prenotazioneRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));

        if (!prenotazione.getUtente().getEmail().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non puoi annullare questa prenotazione");
        }

        if (prenotazione.isAnnullata()) {
            return ResponseEntity.badRequest().body("Prenotazione già annullata");
        }

        LocalDateTime oraPrenotazione = prenotazione.getDataOra();
        LocalDateTime limiteAnnullamento = oraPrenotazione.minusHours(12);

        if (LocalDateTime.now().isAfter(limiteAnnullamento)) {
            return ResponseEntity.badRequest().body("Non è più possibile annullare questa prenotazione");
        }

        prenotazione.setAnnullata(true);
        prenotazioneRepo.save(prenotazione);

        return ResponseEntity.ok("Prenotazione annullata con successo");
    }
    @GetMapping("/api/prenotazioni/occupate")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'MASSAGGIATORE')")
    public List<String> getOrariOccupati(
            @RequestParam Long massaggiatoreId,
            @RequestParam String data // formato: YYYY-MM-DD
    ) {
        LocalDate giorno = LocalDate.parse(data);
        List<Prenotazione> prenotazioni = prenotazioneRepo
                .findByMassaggio_Massaggiatore_IdAndDataOraBetween(
                        massaggiatoreId,
                        giorno.atStartOfDay(),
                        giorno.plusDays(1).atStartOfDay()
                );

        return prenotazioni.stream()
                .map(p -> p.getDataOra().toLocalTime().toString().substring(0, 5))
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/annulla-by-admin")
    public ResponseEntity<String> annullaPrenotazioneByAdmin(@PathVariable Long id) {
        Prenotazione prenotazione = prenotazioneRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));

        if (prenotazione.isAnnullata()) {
            return ResponseEntity.badRequest().body("Prenotazione già annullata");
        }

        prenotazione.setAnnullata(true);
        prenotazioneRepo.save(prenotazione);

        return ResponseEntity.ok("Prenotazione annullata con successo dall'admin");
    }
}
