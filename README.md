# 💳 API de Pagamentos

API REST de pagamentos construída com **Java e Spring Boot**, pensada para funcionar como um serviço independente que se integra à [API de E-commerce](https://github.com/ErickSScarneiro/api-ecommerce-spring-boot). Ela cuida de criar, validar e processar pagamentos, com débito de saldo atômico e proteção contra cobranças duplicadas.

![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-database-4479A1?logo=mysql&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-springdoc-85EA2D?logo=swagger&logoColor=black)

## 📌 Sobre o projeto

Em vez de deixar o pagamento misturado com o resto do e-commerce, este projeto isola essa responsabilidade em um microsserviço próprio, com seu **próprio schema no MySQL**. O e-commerce chama a API de pagamento via HTTP, e a decisão de aprovar ou negar um pagamento fica toda aqui.

## ✨ Funcionalidades

- Criação de pagamentos com método, valor, moeda e status
- Processamento do pagamento com **débito atômico de saldo** (um único `UPDATE` condicional, evitando condição de corrida)
- **Idempotência** via header `Idempotency-Key`: reenviar a mesma requisição não gera cobrança duplicada
- Validação de pagamento (aprovado, negado, já processado)
- Consulta de pagamento por id e listagem por cliente
- Tratamento de erros centralizado com `@RestControllerAdvice` e respostas padronizadas
- Documentação interativa com Swagger UI

## 🛠️ Tecnologias

| Tecnologia | Uso |
|---|---|
| Java 25 | Linguagem |
| Spring Boot 4.1.1 | Framework |
| Spring Data JPA / Hibernate | Persistência |
| MySQL | Banco de dados |
| Bean Validation | Validação de entrada |
| springdoc-openapi 3.1.0 | Documentação Swagger |
| Maven | Build e dependências |

## 🧱 Arquitetura

Arquitetura em camadas, com DTOs separando a API do modelo de dados:

```
src/main/java/payment/ecommerce/paymentEco
├── controller    # Endpoints REST
├── service       # Regras de negócio (criar, processar, validar)
├── repository    # Acesso ao banco (Spring Data JPA)
├── entity        # Pagamento, Cliente
├── dto           # Request / Response (incluindo ErroResponse)
└── exception     # Exceptions customizadas + GlobalExceptionHandler
```

### Decisões de design

- **Banco separado por serviço:** o e-commerce e o pagamento usam schemas diferentes. O pagamento referencia cliente e compra apenas por id (`clienteId`, `compraId`), sem acoplamento entre as entidades dos dois projetos.
- **Débito atômico:** o saldo é debitado com um `UPDATE` condicional no repositório, então duas requisições simultâneas não conseguem gastar o mesmo saldo.
- **Idempotência:** o cliente envia um `Idempotency-Key` na criação, e uma chave repetida devolve o resultado já existente em vez de criar outro pagamento.

## 🚀 Como executar

### Pré-requisitos

- Java 25
- Maven 3.9+
- MySQL em execução

### Passo a passo

```bash
# 1. Clone o repositório
git clone https://github.com/ErickSScarneiro/API-de-Pagamentos.git
cd API-de-Pagamentos

# 2. Crie o schema no MySQL
#    CREATE DATABASE pagamento;

# 3. Configure o banco em src/main/resources/application.properties

# 4. Rode a aplicação
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8081`.

### Configuração

```properties
server.port=8081

spring.datasource.url=jdbc:mysql://localhost:3306/pagamento
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA

spring.jpa.hibernate.ddl-auto=update
```

> ⚠️ Não suba suas credenciais reais para o GitHub. Use variáveis de ambiente ou um `application-local.properties` no `.gitignore`.

## 📖 Documentação da API

Com a aplicação rodando, acesse o Swagger UI:

👉 **http://localhost:8081/swagger-ui/index.html**

### Endpoints principais

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/pagamento` | Cria um pagamento (aceita `Idempotency-Key`) |
| `POST` | `/pagamento/{id}/processar` | Processa o pagamento e debita o saldo |
| `GET` | `/pagamento/{id}` | Busca um pagamento por id |
| `GET` | `/pagamento/cliente/{clienteId}` | Lista os pagamentos de um cliente |

### Exemplo de requisição

```bash
curl -X POST http://localhost:8081/pagamento \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: 7f3c2a1e-9b4d-4c1a-8e55-1d2f3a4b5c6d" \
  -d '{
    "clienteId": "UUID-DO-CLIENTE",
    "compraId": "UUID-DA-COMPRA",
    "metodoPagamento": "SALDO",
    "valor": 150.00,
    "moeda": "BRL"
  }'
```

### Respostas de erro

Os erros seguem um formato único, tratado pelo `GlobalExceptionHandler`:

| Situação | Exception |
|---|---|
| Cliente inexistente | `ClienteNaoLocalizado` |
| Pagamento inexistente | `PagamentoNaoEncontrado` |
| Pagamento já processado | `PagamentoJaProcessado` |
| Saldo insuficiente | `SaldoInsuficiente` |

## 🔗 Integração com o E-commerce

No projeto de e-commerce, um `PagamentoClient` chama esta API durante o fluxo de compra:

`criar compra pendente → solicitar pagamento → finalizar compra`

Cada etapa roda em sua própria transação (`TransactionTemplate`) para não manter locks abertos durante a chamada HTTP.

## 🗺️ Próximos passos

- [ ] Testes unitários com JUnit 5 e Mockito
- [ ] Spring Security + JWT
- [ ] Docker e Docker Compose (API + MySQL)
- [ ] Novos métodos de pagamento

## 👨‍💻 Autor

**Erick Scarneiro**

[![GitHub](https://img.shields.io/badge/GitHub-ErickSScarneiro-181717?logo=github)](https://github.com/ErickSScarneiro)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-conectar-0A66C2?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/erick-carneiro-87b23819a/)
