package controllers;

import entities.Utente;
import enumerations.Ruolo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import repositories.UtenteRepository;

import java.util.List;

@RestController
@RequestMapping("/api/utenti")
@RequiredArgsConstructor
public class UtenteController {

    private final UtenteRepository utenteRepository;

    @GetMapping("/massaggiatori")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public List<Utente> getMassaggiatori() {
        return utenteRepository.findByRuolo(Ruolo.MASSAGGIATORE);
    }
}