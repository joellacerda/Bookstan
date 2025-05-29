# Bookstan - API de Gerenciamento de Livros 📚

Bookstan é uma API RESTful para gerenciamento de uma coleção de livros, desenvolvida como um projeto de estudo e portfólio para demonstrar habilidades em desenvolvimento backend com Java e Spring Boot.

## ✨ Funcionalidades Principais

* CRUD completo para Livros:
    * **C**riar novos livros.
    * **R**ecuperar livros (todos de forma paginada e ordenada, ou por ID específico).
    * **U**pdate (atualizar) informações de livros existentes.
    * **D**elete (remover) livros.
* Validação de dados de entrada (usando DTOs) para garantir a integridade das informações.
* Tratamento global de exceções para respostas de erro consistentes.
* Uso de Data Transfer Objects (DTOs) para desacoplar a API da camada de persistência e controlar os dados expostos.
* Paginação e ordenação na listagem de livros.
* Documentação da API gerada automaticamente com Springdoc OpenAPI (Swagger UI).
* Cobertura de testes unitários para a camada de serviço.
* Cobertura de testes de integração para os endpoints da API.
* Persistência de dados configurada para PostgreSQL, com configurações sensíveis externalizadas.

## 🚀 Tecnologias Utilizadas

* **Linguagem:** Java 17+
* **Framework Principal:** Spring Boot 3.x
    * Spring Web: Para construção de APIs RESTful.
    * Spring Data JPA: Para persistência de dados (incluindo suporte a Paginação e Ordenação).
    * Spring Boot Validation: Para validação de dados (Bean Validation nos DTOs).
    * Spring Boot Test: Para testes unitários e de integração.
* **Provedor JPA:** Hibernate
* **Banco de Dados:**
    * PostgreSQL (para persistência de dados)
    * H2 Database (em memória, configurável para testes ou desenvolvimento rápido)
* **Build & Gerenciamento de Dependências:** Apache Maven
* **Testes:**
    * JUnit 5
    * Mockito
    * MockMvc (Spring Test)
* **Documentação da API:** Springdoc OpenAPI (Swagger UI)
* **Padrões:** Data Transfer Objects (DTOs)
* **Controle de Versão:** Git & GitHub

## 📋 Pré-requisitos

Antes de começar, você vai precisar ter instalado em sua máquina:
* [JDK](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) (Java Development Kit) - Versão 17 ou superior.
* [Apache Maven](https://maven.apache.org/download.cgi) - Versão 3.6 ou superior.
* [Git](https://git-scm.com/downloads).
* [PostgreSQL](https://www.postgresql.org/download/) (ou Docker para rodar uma instância do PostgreSQL).

## ⚙️ Como Executar o Projeto Localmente

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/](https://github.com/)<SEU_NOME_OU_USUARIO_GITHUB>/bookstan.git
    cd bookstan
    ```

2.  **Configuração do Banco de Dados (PostgreSQL):**
    * Certifique-se de que você tem uma instância do PostgreSQL rodando (localmente ou via Docker).
    * Crie um banco de dados para a aplicação (ex: `bookstan_db`) e um usuário com as devidas permissões, se ainda não o fez.
    * **Crie o arquivo `src/main/resources/application-local.properties`** (este arquivo **NÃO** deve ser comitado no Git se contiver senhas). Adicione suas credenciais do PostgreSQL nele:
        ```properties
        # src/main/resources/application-local.properties
        spring.datasource.url=jdbc:postgresql://localhost:5432/bookstan_db
        spring.datasource.username=<SEU_USUARIO_POSTGRES>
        spring.datasource.password=<SUA_SENHA_POSTGRES>

        # Opcional: para ver SQL gerado apenas localmente
        spring.jpa.show-sql=true
        spring.jpa.properties.hibernate.format_sql=true
        ```
    * Certifique-se que o arquivo `application-local.properties` está listado no seu `.gitignore`.
    * O arquivo `src/main/resources/application.properties` principal deve conter `spring.profiles.active=local` para carregar essas configurações e outras configurações não sensíveis do JPA/Hibernate:
        ```properties
        # src/main/resources/application.properties
        spring.profiles.active=local

        server.port=8080

        spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
        spring.jpa.hibernate.ddl-auto=update
        # ... outras configurações gerais ...
        ```
    * A dependência do driver PostgreSQL já deve estar no `pom.xml`.

3.  **Execute a aplicação Spring Boot:**
    Você pode rodar a aplicação usando o Maven:
    ```bash
    mvn spring-boot:run
    ```
    Ou através da sua IDE (ex: IntelliJ IDEA, Eclipse) clicando com o botão direito na classe `BookstanApplication.java` e selecionando "Run".

A aplicação estará disponível em `http://localhost:8080`.

## 📖 Endpoints da API e Documentação (Swagger UI)

Com a aplicação rodando, a documentação interativa da API (Swagger UI) pode ser acessada em:
* `http://localhost:8080/swagger-ui.html`

Principais Endpoints:
* `POST /api/livros`: Cria um novo livro.
* `GET /api/livros`: Lista todos os livros. Suporta os seguintes query parameters para **paginação e ordenação**:
    * `page`: Número da página (começando em 0). Ex: `page=0`
    * `size`: Quantidade de itens por página. Ex: `size=10`
    * `sort`: Campo para ordenação, seguido opcionalmente por `,asc` ou `,desc`. Ex: `sort=titulo,asc` ou `sort=anoPublicacao,desc`. Múltiplos campos de ordenação podem ser fornecidos.
* `GET /api/livros/{id}`: Busca um livro pelo seu ID.
* `PUT /api/livros/{id}`: Atualiza um livro existente.
* `DELETE /api/livros/{id}`: Deleta um livro.

Consulte a Swagger UI para detalhes completos sobre os corpos de requisição/resposta e outros parâmetros.

## 🧪 Testes

Para rodar os testes unitários e de integração do projeto, utilize o comando Maven:
```bash
mvn test