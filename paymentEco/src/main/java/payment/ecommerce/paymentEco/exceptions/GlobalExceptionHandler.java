package payment.ecommerce.paymentEco.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import payment.ecommerce.paymentEco.exceptions.errors.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---------- 404 ----------

    @ExceptionHandler(ClienteNaoLocalizadoException.class)
    public ResponseEntity<ErroResponse> handleClienteNaoLocalizado(ClienteNaoLocalizadoException ex) {
        return montarResposta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(PagamentoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handlePagamentoNaoEncontrado(PagamentoNaoEncontradoException ex) {
        return montarResposta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ---------- 409 ----------

    @ExceptionHandler(PagamentoJaProcessadoException.class)
    public ResponseEntity<ErroResponse> handlePagamentoJaProcessado(PagamentoJaProcessadoException ex) {
        return montarResposta(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Dois processamentos simultâneos do mesmo pagamento (quando usar @Version)
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErroResponse> handleConflitoConcorrencia(ObjectOptimisticLockingFailureException ex) {
        log.warn("Conflito de concorrência: {}", ex.getMessage());
        return montarResposta(HttpStatus.CONFLICT, "O pagamento foi alterado por outra requisição. Tente novamente.");
    }

    // Violação de unique (ex.: mesma chave de idempotência gravada ao mesmo tempo)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> handleIntegridade(DataIntegrityViolationException ex) {
        log.warn("Violação de integridade: {}", ex.getMostSpecificCause().getMessage());
        return montarResposta(HttpStatus.CONFLICT, "Requisição duplicada ou conflitante com um registro existente.");
    }

    // ---------- 422 ----------

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ErroResponse> handleSaldoInsuficiente(SaldoInsuficienteException ex) {
        return montarResposta(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(ChaveIdempotenciaConflitanteException.class)
    public ResponseEntity<ErroResponse> handleChaveConflitante(ChaveIdempotenciaConflitanteException ex) {
        return montarResposta(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    // ---------- 400 ----------

    @ExceptionHandler(ValorInvalidoException.class)
    public ResponseEntity<ErroResponse> handleValorInvalido(ValorInvalidoException ex) {
        return montarResposta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ChaveIdempotenciaObrigatoriaException.class)
    public ResponseEntity<ErroResponse> handleChaveObrigatoria(ChaveIdempotenciaObrigatoriaException ex) {
        return montarResposta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Erros do @Valid no corpo da requisição
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidacao(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return montarResposta(HttpStatus.BAD_REQUEST, mensagem);
    }

    // JSON malformado ou valor fora do enum (ex.: metodo = "XYZ")
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> handleJsonInvalido(HttpMessageNotReadableException ex) {
        return montarResposta(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido");
    }

    // Header obrigatório ausente (ex.: Idempotency-Key com required = true)
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErroResponse> handleHeaderAusente(MissingRequestHeaderException ex) {
        return montarResposta(HttpStatus.BAD_REQUEST, "Header obrigatório ausente: " + ex.getHeaderName());
    }

    // Parâmetro de URL com tipo errado (ex.: UUID malformado)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> handleTipoInvalido(MethodArgumentTypeMismatchException ex) {
        return montarResposta(HttpStatus.BAD_REQUEST, "Valor inválido para o parâmetro: " + ex.getName());
    }

    // ---------- 500 (rede de segurança) ----------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGenerico(Exception ex) {
        log.error("Erro inesperado", ex);
        return montarResposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor");
    }

    private ResponseEntity<ErroResponse> montarResposta(HttpStatus status, String mensagem) {
        ErroResponse erro = new ErroResponse(status.value(), mensagem, LocalDateTime.now());
        return ResponseEntity.status(status).body(erro);
    }
}