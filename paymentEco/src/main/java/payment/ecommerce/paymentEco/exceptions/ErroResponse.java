package payment.ecommerce.paymentEco.exceptions;

import java.time.LocalDateTime;

public record ErroResponse(int status, String mensagem, LocalDateTime timestamp) {}