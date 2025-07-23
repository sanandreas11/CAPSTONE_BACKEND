package services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendRegistrationEmail(String to, String nomeCompleto) {
        String subject = "Benvenuto nel sistema di prenotazione massaggi!";
        String body = """
                Ciao %s,
                
                La tua registrazione al sistema di prenotazioni è andata a buon fine.
                
                Puoi ora accedere alla piattaforma e iniziare a prenotare i tuoi massaggi preferiti.
                
                Grazie per esserti registrato!
                
                — Il team di CAPSTONE_BACKEND
                """.formatted(nomeCompleto);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String nomeCompleto, String token) {
        String resetLink = "http://localhost:5173/reset-password?token=" + token;
        String subject = "Recupero password - CAPSTONE";
        String body = """
            Ciao %s,

            Abbiamo ricevuto una richiesta di recupero password.
            Clicca sul link seguente per crearne una nuova:

            %s

            Se non hai richiesto nulla, ignora questo messaggio.

            — Team CAPSTONE_BACKEND
            """.formatted(nomeCompleto, resetLink);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
