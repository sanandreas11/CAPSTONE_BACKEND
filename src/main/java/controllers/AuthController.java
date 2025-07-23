package controllers;

import auth.ResetPasswordRequest;
import entities.Utente;
import auth.LoginRequest;
import auth.RegisterRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import repositories.UtenteRepository;
import security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import services.EmailService;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UtenteRepository utenteRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Utente utente = utenteRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if (!passwordEncoder.matches(request.getPassword(), utente.getPassword())) {
            return ResponseEntity.badRequest().body("Password errata");
        }

        String token = jwtUtil.generateToken(utente);
        return ResponseEntity.ok(Map.of("token", token, "role", utente.getRuolo().name()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (utenteRepo.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email già in uso");
        }

        Utente utente = new Utente();
        utente.setNome(request.getNome());
        utente.setCognome(request.getCognome());
        utente.setEmail(request.getEmail());

        utente.setPassword(passwordEncoder.encode(request.getPassword()));

        utente.setRuolo(request.getRuolo());
        utenteRepo.save(utente);
        return ResponseEntity.ok("Registrazione completata");
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token mancante");
        }

        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
            }
        }

        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token non valido");
        }

        String email = jwtUtil.extractUsername(refreshToken);
        Utente utente = utenteRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        String newAccessToken = jwtUtil.generateToken(utente);

        return ResponseEntity.ok(Map.of(
                "token", newAccessToken,
                "role", utente.getRuolo().name()
        ));
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        Utente utente = utenteRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email non registrata"));

        String token = jwtUtil.generateToken(utente);
        emailService.sendPasswordResetEmail(
                utente.getEmail(),
                utente.getNome() + " " + utente.getCognome(),
                token
        );

        return ResponseEntity.ok("Email per il reset della password inviata");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        String token = request.getToken();
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token non valido o scaduto");
        }

        String email = jwtUtil.extractUsername(token);
        Utente utente = utenteRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        utente.setPassword(passwordEncoder.encode(request.getNewPassword()));
        utenteRepo.save(utente);

        return ResponseEntity.ok("Password aggiornata");
    }

    @PostMapping("/password-reset/perform")
    public ResponseEntity<?> performReset(@RequestBody ResetPasswordRequest request) {
        String token = request.getToken();
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token non valido o scaduto");
        }

        String email = jwtUtil.extractUsername(token);
        Utente utente = utenteRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        utente.setPassword(passwordEncoder.encode(request.getNewPassword()));
        utenteRepo.save(utente);

        return ResponseEntity.ok("Password aggiornata");
    }
}