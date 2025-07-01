package auth;


import lombok.Data;

@Data
public class RegisterRequest {
    private String nome;
    private String cognome;
    private String email;
    private String password;
}
