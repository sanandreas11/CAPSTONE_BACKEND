package controllers;

import entities.Utente;
import enumerations.Ruolo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import repositories.UtenteRepository;

import java.util.List;

@RestController
@RequestMapping("/api/utenti")
@RequiredArgsConstructor
public class UtenteController {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/massaggiatori")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public List<Utente> getMassaggiatori() {
        return utenteRepository.findByRuolo(Ruolo.MASSAGGIATORE);
    }

    @PostMapping("/massaggiatori")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Utente> aggiungiMassaggiatore(@RequestBody Utente u) {
        u.setRuolo(Ruolo.MASSAGGIATORE);
        u.setPassword(passwordEncoder.encode(u.getPassword())); // encode sicuro
        return ResponseEntity.ok(utenteRepository.save(u));
    }

    @DeleteMapping("/massaggiatori/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> rimuoviMassaggiatore(@PathVariable Long id) {
        utenteRepository.deleteById(id);
        return ResponseEntity.ok("Massaggiatore rimosso");
    }
}