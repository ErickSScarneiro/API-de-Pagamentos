package payment.ecommerce.paymentEco.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import payment.ecommerce.paymentEco.enums.MetodoPagamento;
import payment.ecommerce.paymentEco.enums.Moeda;
import payment.ecommerce.paymentEco.enums.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pagamentos", indexes = {
        @Index(name = "idx_pagamento_cliente", columnList = "cliente_id"),
        @Index(name = "idx_pagamento_compra", columnList = "compra_id")
})
@Getter @Setter
@NoArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_pagamento")
    private UUID id;

    @Column(name = "cliente_id", nullable = false)
    private Integer clienteId;

    @Column(name = "compra_id", nullable = false)
    private Integer compraId;

    @CreationTimestamp
    @Column(name = "data_pagamento_criado", updatable = false, nullable = false)
    private LocalDateTime pagamentoCriado;

    @UpdateTimestamp
    @Column(name = "data_pagamento_atualizado", nullable = false)
    private LocalDateTime pagamentoAtualizado;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pagamento", nullable = false, length = 30)
    private MetodoPagamento metodoPagamento;

    @Column(name = "valor_pago", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Moeda moeda;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento", nullable = false, length = 20)
    private StatusPagamento statusPagamento;

    @Column(name = "chave_idempotencia", nullable = false, unique = true, updatable = false)
    private String chaveIdempotencia;

    public void aprovar() {
        exigirPendente();
        this.statusPagamento = StatusPagamento.APROVADO;
    }

    public void negar() {
        exigirPendente();
        this.statusPagamento = StatusPagamento.NEGADO;
    }

    private void exigirPendente() {
        if (this.statusPagamento != StatusPagamento.PENDENTE) {
            throw new IllegalStateException(
                    "Só é possível alterar pagamentos pendentes. Status atual: " + statusPagamento);
        }
    }

}