package payment.ecommerce.paymentEco.exceptions.errors;

public class ClienteNaoLocalizadoException extends RuntimeException {
    public ClienteNaoLocalizadoException(Integer clienteID) {
        super("Cliente referente no pagamento não localizado, cliente ID: " + clienteID);
    }
}
