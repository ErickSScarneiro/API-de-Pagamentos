package payment.ecommerce.paymentEco.entities.DTOpagamento;

import payment.ecommerce.paymentEco.entities.Pagamento;
import payment.ecommerce.paymentEco.enums.MetodoPagamento;
import payment.ecommerce.paymentEco.enums.Moeda;
import payment.ecommerce.paymentEco.enums.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PagamentoResponse(
        UUID id,
        Integer clienteId,
        Integer compraId,
        MetodoPagamento metodo,
        BigDecimal valor,
        Moeda moeda,
        StatusPagamento status,
        LocalDateTime criadoEm
) {
    public static PagamentoResponse de(Pagamento p) {
        return new PagamentoResponse(
                p.getId(),
                p.getClienteId(),
                p.getCompraId(),
                p.getMetodoPagamento(),
                p.getValorPago(),
                p.getMoeda(),
                p.getStatusPagamento(),
                p.getPagamentoCriado()
        );
    }
}