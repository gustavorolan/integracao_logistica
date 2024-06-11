# Projeto de Integração
## Stack Utilizada

Optei por utilizar Kotlin, Spring, RabbitMQ e Postgres. Escolhi essa stack, pois acredito que seja a utilizada pela equipe, apesar de nunca ter utilizado RabbitMQ anteriormente.

## Implementação do Endpoint Assíncrono

Implementei um endpoint assíncrono com coroutines para inserção na fila. Ao extrapolar os dados enviados, notamos a diferença significativa no desempenho. Gerei um arquivo de 60 MB com 619.200 linhas, no qual o tempo de resposta depende apenas do envio do arquivo. Após o envio, é retornado um `orderBatchId`.

Criei a entidade `OrderBatch` para controle da inserção, permitindo que quem adicionou o arquivo tenha o controle dos erros que ocorreram na inserção e saiba qual linha e qual erro aconteceu.

### Exemplo de Payload

```json
{
  "orderBatchId": "f19e5924-abb3-4656-a6d2-305af13266ca",
  "status": "FAILED",
  "totalErrorLines": 1,
  "totalLines": 3,
  "metadatas": [
    {
      "errorLine": 3,
      "errorMessage": "Error trying to parse order information."
    }
  ]
}
```

# README

## Sobre as Entidades de Produto, User e Order

Este serviço é baseado na entidade **Order**. O retorno da função `findAll` é feito sobre as Orders, agrupando-as por usuários conforme solicitado.

### Funcionalidade

- Sempre que uma Order se repete no arquivo, é feita uma pesquisa para verificar se ela já existe. Se já estiver inserida, apenas um novo produto é adicionado.
- Da mesma forma, se o usuário já existir, é adicionada mais uma order para ele.
- Foram implementadas travas para evitar que usuários diferentes tenham as mesmas orders.
- Foi considerada a possibilidade de existir mais de uma vez o mesmo produto na order. Como eram poucos dados no produto, decidiu-se salvar o produto duplicadamente caso haja dois produtos iguais, possibilitando uma mudança futura e levando em conta que não há garantia de que o preço do mesmo produto não varie com o tempo.

# Considerações Finais

- Gostaria de ter realizado alguns testes automatizados; porém, não houve tempo devido às diversas configurações de TestContainers que seriam necessárias.

### Variáveis no Docker Compose e Application.yml

- As variáveis no YAML foram deixadas sem variáveis de ambiente para facilitar a execução.
- Em produção, isso não seria feito dessa maneira.

### Scripts de Inserção no Banco

- Foi utilizado o `create-drop` do próprio Spring para a criação do banco de dados.
- No entanto, em produção, os scripts seriam feitos manualmente.

# Objetivo do Projeto

- O objetivo foi focar em uma solução um pouco diferente do que já havia sido feito, aproveitando para conhecer o RabbitMQ e tornar o tempo de resposta mais rápido.

# Como executar

- Deixei um docker-compose pronto, apenas execute o docker compose up com o terminal aberto na pasta raiz do seu computador
- Após isso é só executar a aplicação na sua IDE preferida