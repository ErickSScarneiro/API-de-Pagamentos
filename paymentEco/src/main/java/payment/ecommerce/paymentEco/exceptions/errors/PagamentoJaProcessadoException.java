package payment.ecommerce.paymentEco.exceptions.errors;

import java.util.UUID;

public class PagamentoJaProcessadoException extends RuntimeException {
    public PagamentoJaProcessadoException(UUID id) {
        super("Pagamento já processado ID: " + id);
    }
}
