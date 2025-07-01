package controllers;


import entities.Massaggio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import repositories.MassaggioRepository;

import java.util.List;

@RestController
@RequestMapping("/api/massaggi")
@RequiredArgsConstructor
public class MassaggioController {

    private final MassaggioRepository massaggioRepo;

    @GetMapping
    public List<Massaggio> getAllMassaggi() {
        return massaggioRepo.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> creaMassaggio(@RequestBody Massaggio massaggio) {
        massaggioRepo.save(massaggio);
        return ResponseEntity.ok("Massaggio creato con successo");
    }
}
