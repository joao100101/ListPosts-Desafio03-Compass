
# Sistema de Listagem de Posts/Comentários

Um sistema que faz uma requisição pra uma API externa para coletar os posts e comentários e mostrar ambos juntos,
salvando no banco de dados embarcado H2.
O Sistema faz o processamento dos posts/comentários de maneira assíncrona.

## Funcionalidades

- Adicionar Post para processamento.
- Listar posts processados.
- Atualizar o post/comentários.
- Desabilitar post.



## Documentação da API

#### Retorna todos os itens

```http
  GET /posts
```

#### Coloca um post para ser processado

```http
  POST /posts/${id}
```

| Parâmetro   | Tipo       | Descrição                                   |
| :---------- | :--------- | :------------------------------------------ |
| `id`      | `Long` | **Obrigatório**. O ID do item que você quer |

#### Coloca um Post para ser reprocessado

```http
  PUT /posts/${id}
```

| Parâmetro   | Tipo       | Descrição                                   |
| :---------- | :--------- | :------------------------------------------ |
| `id`      | `Long` | **Obrigatório**. O ID do item que você quer |


#### Desabilitar um Post

```http
  DELETE /posts/${id}
```

| Parâmetro   | Tipo       | Descrição                                   |
| :---------- | :--------- | :------------------------------------------ |
| `id`      | `Long` | **Obrigatório**. O ID do item que você quer |








## Requisitos

- Java 17


## Tecnologias utilizadas

- Spring Boot
- JPA
- Lombok
- H2 Database


## Autores

- Desenvolvido por [Joao Victor Mundel](https://www.github.com/joao100101).

