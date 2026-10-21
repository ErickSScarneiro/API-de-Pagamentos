package payment.ecommerce.paymentEco.exceptions.errors;

public class PagamentoJaExisteParaCompra extends RuntimeException {
    public PagamentoJaExisteParaCompra(Integer id) {
        super("Pagamento já existe para a compra ID " + id);
    }
}
