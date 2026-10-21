package payment.ecommerce.paymentEco.exceptions.errors;

public class ChaveIdempotenciaConflitanteException extends RuntimeException {
    public ChaveIdempotenciaConflitanteException(String chave) {
        super("Chave Idempotencia Já adicionada em outro pagamento, chave " + chave);
    }
}
