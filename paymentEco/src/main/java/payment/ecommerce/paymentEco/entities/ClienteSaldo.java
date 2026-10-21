package payment.ecommerce.paymentEco.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

@Entity
@Immutable
@Table(name = "cliente", schema = "ecomerce")
@Getter
@NoArgsConstructor
public class ClienteSaldo {

    @Id
    @Column(name = "id_cliente")
    private Integer id;

    @Column(name = "saldo_cliente")
    private BigDecimal saldo;
}