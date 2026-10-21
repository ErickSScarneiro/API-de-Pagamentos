package payment.ecommerce.paymentEco.repositorys;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.jpa.repository.JpaRepository;
import payment.ecommerce.paymentEco.entities.Pagamento;
import payment.ecommerce.paymentEco.enums.StatusPagamento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PagamentoRepository extends JpaRepository<Pagamento, UUID> {
    List<Pagamento> findByClienteId(Integer clienteId);

    Optional<Pagamento> findByChaveIdempotencia(String chave);

    boolean existsByCompraIdAndStatusPagamentoIn(@NotNull(message = "O id da compra é obrigatório") @Positive(message = "O id da compra deve ser positivo") Integer integer, List<StatusPagamento> pendente);
}
