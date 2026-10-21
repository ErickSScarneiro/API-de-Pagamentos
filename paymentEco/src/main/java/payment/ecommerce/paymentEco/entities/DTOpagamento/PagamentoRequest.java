package payment.ecommerce.paymentEco.entities.DTOpagamento;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import payment.ecommerce.paymentEco.enums.MetodoPagamento;
import payment.ecommerce.paymentEco.enums.Moeda;

import java.math.BigDecimal;

public record PagamentoRequest(

        @NotNull(message = "O id do cliente é obrigatório")
        @Positive(message = "O id do cliente deve ser positivo")
        Integer clienteId,

        @NotNull(message = "O id da compra é obrigatório")
        @Positive(message = "O id da compra deve ser positivo")
        Integer compraId,

        @NotNull(message = "O valor é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "O valor deve ter no máximo 10 dígitos inteiros e 2 casas decimais")
        @Positive(message = "O valor da compra precisa ser positivo!")
        BigDecimal valor,

        @NotNull(message = "O método de pagamento é obrigatório")
        MetodoPagamento metodo,

        @NotNull(message = "A moeda é obrigatória")
        Moeda moeda
) {
}