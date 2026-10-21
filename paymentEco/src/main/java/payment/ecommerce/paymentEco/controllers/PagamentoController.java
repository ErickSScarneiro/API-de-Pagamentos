package payment.ecommerce.paymentEco.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payment.ecommerce.paymentEco.entities.DTOpagamento.PagamentoRequest;
import payment.ecommerce.paymentEco.entities.DTOpagamento.PagamentoResponse;
import payment.ecommerce.paymentEco.entities.Pagamento;
import payment.ecommerce.paymentEco.services.PagamentoService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pagamento")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @PostMapping
    public ResponseEntity<Pagamento> criar(
            @RequestHeader("Idempotency-Key") String chave,
            @Valid @RequestBody PagamentoRequest request) {

        Pagamento criado = pagamentoService.criar(chave, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PostMapping("/{id}/processar")
    public ResponseEntity<Pagamento> processar(@PathVariable UUID id) {
        return ResponseEntity.ok(pagamentoService.processar(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pagamento> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(pagamentoService.buscar(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PagamentoResponse>> listarPorCliente(@PathVariable Integer clienteId) {
        return ResponseEntity.ok(pagamentoService.listarPorCliente(clienteId));
    }

    @GetMapping("/{id}/validar")
    public ResponseEntity<Boolean> validar(@PathVariable UUID id) {
        return ResponseEntity.ok(pagamentoService.validarPagamento(id));
    }


}
