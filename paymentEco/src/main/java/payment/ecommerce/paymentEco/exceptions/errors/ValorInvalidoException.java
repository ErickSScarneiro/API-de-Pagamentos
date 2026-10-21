package payment.ecommerce.paymentEco.exceptions.errors;

import java.math.BigDecimal;

public class ValorInvalidoException extends RuntimeException {
    public ValorInvalidoException(BigDecimal valorPagamento, BigDecimal valorCompra) {
        super(
                "Valor de pagamento diferente do valor original da compra. Valor compra |"
                        + valorCompra + "| Valor de pagamento |" + valorPagamento + "|");
    }
}
