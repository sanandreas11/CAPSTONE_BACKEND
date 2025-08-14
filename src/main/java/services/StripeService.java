package services;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StripeService {

    @Value("${stripe.secretKey}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public String createCheckoutSession(Long prenotazioneId, int amount) throws Exception {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:5173/prenotazioni") // URL dopo il pagamento
                .setCancelUrl("http://localhost:5173/carrello") // URL se l'utente annulla
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount((long) amount)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Pagamento Prenotazione ID: " + prenotazioneId)
                                                                .build())
                                                .build())
                                .build())
                .putMetadata("prenotazioneId", prenotazioneId.toString())
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }
}