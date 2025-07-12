package controllers;

import java.time.LocalDateTime;
import java.util.UUID;
import auth.AuthResponse;
import auth.CustomUserDetailsService;
import auth.LoginRequest;
import auth.RegisterRequest;
import entities.PasswordResetToken;
import entities.Utente;
import enumerations.Ruolo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import repositories.PasswordResetTokenRepository;
import repositories.UtenteRepository;
import security.JwtUtil;
import services.EmailService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UtenteRepository utenteRepository;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    // ✅ REGISTRAZIONE
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        // Imposta CLIENTE come default
        Ruolo ruolo = request.getRuolo() != null ? request.getRuolo() : Ruolo.CLIENTE;

        Utente utente = Utente.builder()
                .nome(request.getNome())
                .cognome(request.getCognome())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .ruolo(ruolo)
                .build();

        utenteRepository.save(utente);
        String token = jwtUtil.generateToken(utente.getEmail());

        // ✅ Invio email di benvenuto
        String nomeCompleto = utente.getNome() + " " + utente.getCognome();
        emailService.sendRegistrationEmail(utente.getEmail(), nomeCompleto);

        return ResponseEntity.ok(new AuthResponse(token));
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        passwordResetTokenRepository.deleteByUtente(utente); // rimuove token precedenti

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .scadenza(LocalDateTime.now().plusMinutes(30))
                .utente(utente)
                .build();

        passwordResetTokenRepository.save(resetToken);
        emailService.sendPasswordResetEmail(utente.getEmail(), utente.getNome() + " " + utente.getCognome(), token);

        return ResponseEntity.ok("Email per il reset inviata con successo.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String nuovaPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token non valido"));

        if (resetToken.getScadenza().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Il token è scaduto.");
        }

        Utente utente = resetToken.getUtente();
        utente.setPassword(passwordEncoder.encode(nuovaPassword));
        utenteRepository.save(utente);
        passwordResetTokenRepository.delete(resetToken);

        return ResponseEntity.ok("Password aggiornata con successo.");
    }
}