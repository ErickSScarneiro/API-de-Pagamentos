package payment.ecommerce.paymentEco.exceptions.errors;

import java.util.UUID;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(UUID id) {
        super("Saldo do cliente insuficiente para a transação.");
    }
}
