package payment.ecommerce.paymentEco.exceptions.errors;

public class ChaveIdempotenciaObrigatoriaException extends RuntimeException {
    public ChaveIdempotenciaObrigatoriaException() {
        super("Chave Idempotencia obrigatoria!");
    }
}
