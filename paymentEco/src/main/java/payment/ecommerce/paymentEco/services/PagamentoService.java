package payment.ecommerce.paymentEco.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import payment.ecommerce.paymentEco.entities.DTOpagamento.PagamentoRequest;
import payment.ecommerce.paymentEco.entities.DTOpagamento.PagamentoResponse;
import payment.ecommerce.paymentEco.entities.Pagamento;
import payment.ecommerce.paymentEco.enums.StatusPagamento;
import payment.ecommerce.paymentEco.exceptions.errors.*;
import payment.ecommerce.paymentEco.exceptions.errors.PagamentoJaExisteParaCompra;
import payment.ecommerce.paymentEco.repositorys.ClienteRepository;
import payment.ecommerce.paymentEco.repositorys.PagamentoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public Pagamento criar(String chave, PagamentoRequest pagamentoRequest) {

        if (chave == null || chave.isBlank()) {
            throw new ChaveIdempotenciaObrigatoriaException();
        }

        Optional<Pagamento> existente = pagamentoRepository.findByChaveIdempotencia(chave);

        if (existente.isPresent()) {
            Pagamento p = existente.get();

            if (!mesmosDados(p, pagamentoRequest)) {
                log.warn("Chave {} reutilizada com dados diferentes", chave);
                throw new ChaveIdempotenciaConflitanteException(chave);
            }

            log.info("Requisição repetida com a chave {}, devolvendo o pagamento existente", chave);
            return p;
        }

        if (clienteRepository.buscarSaldo(pagamentoRequest.clienteId()).isEmpty()) {
            log.warn("Cliente ID {} referente ao pagamento não localizado", pagamentoRequest.clienteId());
            throw new ClienteNaoLocalizadoException(pagamentoRequest.clienteId());
        }

        if (pagamentoRepository.existsByCompraIdAndStatusPagamentoIn(
                pagamentoRequest.compraId(),
                List.of(StatusPagamento.PENDENTE, StatusPagamento.APROVADO))) {
            log.warn("Compra {} já possui pagamento em andamento ou aprovado", pagamentoRequest.compraId());
            throw new PagamentoJaExisteParaCompra(pagamentoRequest.compraId());
        }

        Pagamento pagamento = new Pagamento();

        pagamento.setClienteId(pagamentoRequest.clienteId());
        pagamento.setCompraId(pagamentoRequest.compraId());
        pagamento.setMetodoPagamento(pagamentoRequest.metodo());
        pagamento.setValorPago(pagamentoRequest.valor());
        pagamento.setMoeda(pagamentoRequest.moeda());
        pagamento.setStatusPagamento(StatusPagamento.PENDENTE);
        pagamento.setChaveIdempotencia(chave);

        Pagamento salvo = pagamentoRepository.save(pagamento);

        log.info("Pagamento {} criado para a compra {}", salvo.getId(), salvo.getCompraId());

        return salvo;
    }

    @Transactional(noRollbackFor = ClienteNaoLocalizadoException.class)
    public Pagamento processar(UUID id) {
        Pagamento pagamento = buscar(id);

        if (pagamento.getStatusPagamento() != StatusPagamento.PENDENTE) {
            throw new PagamentoJaProcessadoException(id);
        }

        if (!validarPagamento(id)) {
            throw new SaldoInsuficienteException(id);
        }

        int linhas = clienteRepository.debitar(pagamento.getClienteId(), pagamento.getValorPago());

        if (linhas == 1) {
            pagamento.aprovar();
            log.info("Pagamento {} aprovado", id);
            return pagamentoRepository.save(pagamento);
        }

        pagamento.setStatusPagamento(StatusPagamento.NEGADO);
        pagamentoRepository.save(pagamento);

        if (clienteRepository.buscarSaldo(pagamento.getClienteId()).isEmpty()) {
            log.warn("Pagamento {} negado: cliente {} não localizado", id, pagamento.getClienteId());
            throw new ClienteNaoLocalizadoException(pagamento.getClienteId());
        }

        log.warn("Pagamento {} negado: saldo insuficiente", id);
        return pagamento;
    }

    @Transactional(readOnly = true)
    public Pagamento buscar(UUID id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new PagamentoNaoEncontradoException(id));
    }

    @Transactional(readOnly = true)
    public boolean validarPagamento(UUID idPagamento) {
        Pagamento pagamento = buscar(idPagamento);

        return clienteRepository.buscarSaldo(pagamento.getClienteId())
                .map(saldo -> saldo.compareTo(pagamento.getValorPago()) >= 0)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public List<PagamentoResponse> listarPorCliente(Integer clienteId) {
        return pagamentoRepository.findByClienteId(clienteId).stream()
                .map(PagamentoResponse::de)
                .toList();
    }

    /** Compara a requisição com o pagamento já salvo sob a mesma chave. */
    private boolean mesmosDados(Pagamento p, PagamentoRequest r) {
        return p.getClienteId().equals(r.clienteId())
                && p.getCompraId().equals(r.compraId())
                && p.getValorPago().compareTo(r.valor()) == 0
                && p.getMetodoPagamento() == r.metodo()
                && p.getMoeda() == r.moeda();
    }
}