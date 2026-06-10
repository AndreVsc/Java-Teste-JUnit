# Postman Guide for Reserva API (CT01–CT09)

Este documento explica como testar cada caso de teste do serviço de reservas usando o Postman.

## Visão geral

A aplicação expõe o endpoint:

- `POST http://localhost:8080/api/reservas`

Cabeçalhos obrigatórios:

- `Content-Type: application/json`

Cabeçalho de teste opcional para cenário:

- `X-Test-Scenario: ct01 | ct02 | ct04 | ct05`

Também existem endpoints de consulta:

- `GET http://localhost:8080/api/reservas`
- `GET http://localhost:8080/api/reservas/{id}`

---

## Passo 0: executar a aplicação

1. Abra um terminal no diretório `teste`.
2. Execute:

```powershell
./mvnw spring-boot:run
```

3. Aguarde a aplicação subir.
4. Verifique se `http://localhost:8080` está disponível.

---

## Passo 1: dados de teste disponíveis

A aplicação já inicializa automaticamente um carro com:

* `carroId`: `carro-01`
* `modelo`: `Civic`
* `disponivel`: `true`
* `precoDiaria`: `200.0`

Portanto, use sempre `carro-01` nas requisições abaixo.

---

## Corpo de requisição base

Use este JSON como modelo para testes básicos:

```json
{
  "clienteId": "cliente-01",
  "carroId": "carro-01",
  "dataInicio": "2025-06-01",
  "dataFim": "2025-06-05",
  "pagamento": {
    "formaPagamento": "PIX",
    "numeroParcelas": 1
  }
}

```

> Observação: `dataFim` deve ser posterior a `dataInicio`.

---

## CT01 — CNH inválida do condutor principal

### Objetivo

Validar que a reserva é rejeitada quando a CNH do condutor principal está inválida.

### Requisição

* Método: `POST`
* URL: `http://localhost:8080/api/reservas`
* Headers:
* `Content-Type: application/json`
* `X-Test-Scenario: ct01`


* Body:

```json
{
  "clienteId": "cliente-01",
  "carroId": "carro-01",
  "dataInicio": "2025-06-01",
  "dataFim": "2025-06-05",
  "pagamento": {
    "formaPagamento": "PIX",
    "numeroParcelas": 1
  }
}

```

### Resposta esperada

* Status: `422 Unprocessable Entity`
* Body:

```json
{
  "erro": "CNH inválida para: Cliente"
}

```

---

## CT02 — CNH inválida do outro condutor

### Objetivo

Validar que a reserva é rejeitada quando a CNH do outro condutor está inválida.

### Requisição

* Método: `POST`
* URL: `http://localhost:8080/api/reservas`
* Headers:
* `Content-Type: application/json`
* `X-Test-Scenario: ct02`


* Body:

```json
{
  "clienteId": "cliente-01",
  "carroId": "carro-01",
  "dataInicio": "2025-06-01",
  "dataFim": "2025-06-05",
  "outroCondutor": {
    "nome": "Lucas",
    "numeroCnh": "CNH_LUCAS"
  },
  "pagamento": {
    "formaPagamento": "PIX",
    "numeroParcelas": 1
  }
}

```

### Resposta esperada

* Status: `422 Unprocessable Entity`
* Body:

```json
{
  "erro": "CNH inválida para: Lucas"
}

```

---

## CT03 — Pagamento recusado no cartão à vista

### Objetivo

Validar que a reserva é cancelada quando o pagamento à vista é recusado.

### Requisição

* Método: `POST`
* URL: `http://localhost:8080/api/reservas`
* Headers:
* `Content-Type: application/json`


* Body:

```json
{
  "clienteId": "cliente-01",
  "carroId": "carro-01",
  "dataInicio": "2025-06-01",
  "dataFim": "2025-06-05",
  "pagamento": {
    "formaPagamento": "CARTAO_CREDITO_VISTA",
    "numeroParcelas": 1
  }
}

```

### Resposta esperada

* Status: `402 Payment Required`
* Body:

```json
{
  "erro": "Pagamento não aprovado pela operadora."
}

```

---

## CT04 — Pagamento recusado no PIX

### Objetivo

Validar que a reserva é cancelada quando o pagamento PIX é recusado.

### Requisição

* Método: `POST`
* URL: `http://localhost:8080/api/reservas`
* Headers:
* `Content-Type: application/json`
* `X-Test-Scenario: ct04`


* Body:

```json
{
  "clienteId": "cliente-01",
  "carroId": "carro-01",
  "dataInicio": "2025-06-01",
  "dataFim": "2025-06-05",
  "pagamento": {
    "formaPagamento": "PIX",
    "numeroParcelas": 1
  }
}

```

### Resposta esperada

* Status: `402 Payment Required`
* Body:

```json
{
  "erro": "Pagamento não aprovado pela operadora."
}

```

---

## CT05 — Pagamento recusado em parcelamento 3x

### Objetivo

Validar que a reserva é cancelada quando o parcelamento em 3x é recusado.

### Requisição

* Método: `POST`
* URL: `http://localhost:8080/api/reservas`
* Headers:
* `Content-Type: application/json`
* `X-Test-Scenario: ct05`


* Body:

```json
{
  "clienteId": "cliente-01",
  "carroId": "carro-01",
  "dataInicio": "2025-06-01",
  "dataFim": "2025-06-05",
  "pagamento": {
    "formaPagamento": "CARTAO_CREDITO_PARCELADO",
    "numeroParcelas": 3
  }
}

```

### Resposta esperada

* Status: `402 Payment Required`
* Body:

```json
{
  "erro": "Pagamento não aprovado pela operadora."
}

```

---

## CT06 — Reserva confirmada no parcelamento 3x com pagamento aprovado

### Objetivo

Validar que a reserva é confirmada quando o pagamento parcelado em 3x é aprovado.

### Requisição

* Método: `POST`
* URL: `http://localhost:8080/api/reservas`
* Headers:
* `Content-Type: application/json`


* Body:

```json
{
  "clienteId": "cliente-01",
  "carroId": "carro-01",
  "dataInicio": "2025-06-01",
  "dataFim": "2025-06-05",
  "pagamento": {
    "formaPagamento": "CARTAO_CREDITO_PARCELADO",
    "numeroParcelas": 3
  }
}

```

### Resposta esperada

* Status: `201 Created`
* Body de exemplo:

```json
{
  "id": "...",
  "status": "RESERVA_CONFIRMADA",
  "valorFinal": 800.0,
  "linkConfirmacao": "[https://reservas.example.com/confirmar/](https://reservas.example.com/confirmar/)...",
  "mensagem": "Reserva confirmada! Link enviado para o e-mail do cliente."
}

```

> Observação: `valorFinal = 4 dias * 200 = 800.0`.

---

## CT07 — Parcelamento acima do limite lança IllegalArgumentException

### Objetivo

Validar que a API retorna erro quando o parcelamento excede 5 parcelas.

### Requisição

* Método: `POST`
* URL: `http://localhost:8080/api/reservas`
* Headers:
* `Content-Type: application/json`


* Body:

```json
{
  "clienteId": "cliente-01",
  "carroId": "carro-01",
  "dataInicio": "2025-06-01",
  "dataFim": "2025-06-05",
  "pagamento": {
    "formaPagamento": "CARTAO_CREDITO_PARCELADO",
    "numeroParcelas": 6
  }
}

```

### Resposta esperada

* Status: `400 Bad Request`
* Body:

```json
{
  "erro": "Máximo de 5 parcelas."
}

```

---

## CT08 — PIX aprovado aplica desconto de 10% e confirma reserva

### Objetivo

Validar desconto de 10% em pagamento PIX e reserva confirmada.

### Requisição

* Método: `POST`
* URL: `http://localhost:8080/api/reservas`
* Headers:
* `Content-Type: application/json`


* Body:

```json
{
  "clienteId": "cliente-01",
  "carroId": "carro-01",
  "dataInicio": "2025-06-01",
  "dataFim": "2025-06-05",
  "pagamento": {
    "formaPagamento": "PIX",
    "numeroParcelas": 1
  }
}

```

### Resposta esperada

* Status: `201 Created`
* Body de exemplo:

```json
{
  "id": "...",
  "status": "RESERVA_CONFIRMADA",
  "valorFinal": 720.0,
  "linkConfirmacao": "[https://reservas.example.com/confirmar/](https://reservas.example.com/confirmar/)...",
  "mensagem": "Reserva confirmada! Link enviado para o e-mail do cliente."
}

```

> Observação: `valorFinal = 4 dias * 200 * 0.90 = 720.0`.

---

## CT09 — Cartão à vista aprovado aplica desconto de 5% e confirma reserva

### Objetivo

Validar desconto de 5% em pagamento cartão à vista e reserva confirmada.

### Requisição

* Método: `POST`
* URL: `http://localhost:8080/api/reservas`
* Headers:
* `Content-Type: application/json`


* Body:

```json
{
  "clienteId": "cliente-01",
  "carroId": "carro-01",
  "dataInicio": "2025-06-01",
  "dataFim": "2025-06-05",
  "pagamento": {
    "formaPagamento": "CARTAO_CREDITO_VISTA",
    "numeroParcelas": 1
  }
}

```

### Resposta esperada

* Status: `201 Created`
* Body de exemplo:

```json
{
  "id": "...",
  "status": "RESERVA_CONFIRMADA",
  "valorFinal": 760.0,
  "linkConfirmacao": "[https://reservas.example.com/confirmar/](https://reservas.example.com/confirmar/)...",
  "mensagem": "Reserva confirmada! Link enviado para o e-mail do cliente."
}

```

> Observação: `valorFinal = 4 dias * 200 * 0.95 = 760.0`.

---

## Verificar reservas

Após testar, use:

* `GET http://localhost:8080/api/reservas`
* `GET http://localhost:8080/api/reservas/{id}`

Esses endpoints ajudam a confirmar se a reserva foi criada ou se houve erro.

---

## Notas importantes

* O carro `carro-01` é criado automaticamente quando a aplicação sobe.
* Os cabeçalhos `X-Test-Scenario` não alteram a lógica de produção: eles só ativam os cenários de falha do stub de testes.
* CT06, CT08 e CT09 confirmam a reserva e deixam `carro-01` indisponível na sessão atual. Reinicie a aplicação para resetar o estado in-memory antes de rodar novamente.
* CT03, CT04, CT05 e CT07 falham antes de confirmar a reserva, então o carro permanece disponível.

