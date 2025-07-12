package controllers;

import dtos.MassaggioDTO;
import entities.Massaggio;
import entities.Utente;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import repositories.MassaggioRepository;
import repositories.UtenteRepository;

import java.util.List;

@RestController
@RequestMapping("/api/massaggi")
@RequiredArgsConstructor
public class MassaggioController {

    private final MassaggioRepository massaggioRepo;
    private final UtenteRepository utenteRepo;

    // ✅ Recupera tutti i massaggi (accessibile a tutti)
    @GetMapping
    public List<Massaggio> getAllMassaggi() {
        return massaggioRepo.findAll();
    }

    // ✅ Crea un massaggio e assegna un massaggiatore (solo ADMIN)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> creaMassaggio(@RequestBody MassaggioDTO dto) {
        Utente massaggiatore = utenteRepo.findById(dto.getMassaggiatoreId())
                .orElseThrow(() -> new RuntimeException("Massaggiatore non trovato"));

        Massaggio massaggio = Massaggio.builder()
                .tipo(dto.getTipo())
                .prezzo(dto.getPrezzo())
                .durata(dto.getDurata())
                .massaggiatore(massaggiatore)
                .build();

        massaggioRepo.save(massaggio);
        return ResponseEntity.ok("Massaggio creato e assegnato al massaggiatore.");
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> aggiornaMassaggio(@PathVariable Long id, @RequestBody MassaggioDTO dto) {
        Massaggio massaggio = massaggioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Massaggio non trovato"));

        Utente massaggiatore = utenteRepo.findById(dto.getMassaggiatoreId())
                .orElseThrow(() -> new RuntimeException("Massaggiatore non trovato"));

        massaggio.setTipo(dto.getTipo());
        massaggio.setPrezzo(dto.getPrezzo());
        massaggio.setDurata(dto.getDurata());
        massaggio.setMassaggiatore(massaggiatore);

        massaggioRepo.save(massaggio);
        return ResponseEntity.ok("Massaggio aggiornato con successo.");
    }

}
