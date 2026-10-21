package payment.ecommerce.paymentEco.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import payment.ecommerce.paymentEco.entities.ClienteSaldo;

import java.math.BigDecimal;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<ClienteSaldo, Integer> {

    @Modifying(flushAutomatically = true)
    @Query(value = """
        UPDATE ecomerce.cliente
           SET saldo_cliente = saldo_cliente - :valor
         WHERE id_cliente = :id
           AND saldo_cliente >= :valor
        """, nativeQuery = true)
    int debitar(@Param("id") Integer id, @Param("valor") BigDecimal valor);

    @Query(value = "SELECT saldo_cliente FROM ecomerce.cliente WHERE id_cliente = :id",
            nativeQuery = true)
    Optional<BigDecimal> buscarSaldo(@Param("id") Integer id);
}