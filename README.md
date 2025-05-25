# Bookstan - API de Gerenciamento de Livros 📚

Bookstan é uma API RESTful para gerenciamento de uma coleção de livros, desenvolvida como um projeto de estudo e portfólio para demonstrar habilidades em desenvolvimento backend com Java e Spring Boot.

## ✨ Funcionalidades Principais

* CRUD completo para Livros:
    * **C**riar novos livros.
    * **R**ecuperar livros (todos ou por ID).
    * **U**pdate (atualizar) informações de livros existentes.
    * **D**elete (remover) livros.
* Validação de dados de entrada para garantir a integridade das informações.
* Tratamento global de exceções para respostas de erro consistentes.
* Documentação da API gerada automaticamente com Springdoc OpenAPI (Swagger UI).
* Cobertura de testes unitários para a camada de serviço.
* Cobertura de testes de integração para os endpoints da API.

## 🚀 Tecnologias Utilizadas

* **Linguagem:** Java 17+
* **Framework Principal:** Spring Boot 3.x
    * Spring Web: Para construção de APIs RESTful.
    * Spring Data JPA: Para persistência de dados.
    * Spring Boot Validation: Para validação de dados (Bean Validation).
    * Spring Boot Test: Para testes unitários e de integração.
* **Provedor JPA:** Hibernate
* **Banco de Dados:**
    * H2 Database (em memória, para desenvolvimento e testes)
    * _Planejado/Configurável para PostgreSQL_
* **Build & Gerenciamento de Dependências:** Apache Maven
* **Testes:**
    * JUnit 5
    * Mockito
    * MockMvc
* **Documentação da API:** Springdoc OpenAPI (Swagger UI)
* **Controle de Versão:** Git & GitHub

## 📋 Pré-requisitos

Antes de começar, você vai precisar ter instalado em sua máquina:
* [JDK](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) (Java Development Kit) - Versão 17 ou superior.
* [Apache Maven](https://maven.apache.org/download.cgi) - Versão 3.6 ou superior.
* [Git](https://git-scm.com/downloads).
* (Opcional, se for usar PostgreSQL) [PostgreSQL](https://www.postgresql.org/download/) ou Docker para rodar uma instância.

## ⚙️ Como Executar o Projeto Localmente

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/joellacerda/bookstan.git
    cd bookstan
    ```

2.  **Configuração do Banco de Dados (H2 - Padrão):**
    O projeto está configurado por padrão para usar o banco de dados H2 em memória. Nenhuma configuração adicional é necessária para este modo.
    * O console do H2 pode ser acessado em `http://localhost:8080/h2-console` (com a aplicação rodando) usando as credenciais definidas em `src/main/resources/application.properties` (URL JDBC: `jdbc:h2:mem:bookstandb`, User: `sa`, Password: [vazio]).

3.  **(Opcional) Configuração para PostgreSQL:**
    Se desejar usar PostgreSQL:
    * Crie um banco de dados no seu servidor PostgreSQL (ex: `bookstan_db`).
    * Edite o arquivo `src/main/resources/application.properties` e comente as configurações do H2.
    * Descomente ou adicione as seguintes propriedades, ajustando conforme sua configuração do PostgreSQL:
        ```properties
        #spring.datasource.url=jdbc:postgresql://localhost:5432/bookstan_db
        #spring.datasource.username=<SEU_USUARIO_POSTGRES>
        #spring.datasource.password=<SUA_SENHA_POSTGRES>
        #spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
        #spring.jpa.hibernate.ddl-auto=update
        ```
    * Adicione a dependência do driver PostgreSQL ao `pom.xml` se ainda não estiver lá:
        ```xml
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        ```
    * Recarregue as dependências do Maven.

4.  **Execute a aplicação Spring Boot:**
    Você pode rodar a aplicação usando o Maven:
    ```bash
    mvn spring-boot:run
    ```
    Ou através da sua IDE (ex: IntelliJ IDEA, Eclipse) clicando com o botão direito na classe `BookstanApplication.java` e selecionando "Run".

A aplicação estará disponível em `http://localhost:8080`.

## 📖 Documentação da API (Swagger UI)

Com a aplicação rodando, a documentação interativa da API (Swagger UI) pode ser acessada em:
* `http://localhost:8080/swagger-ui.html`

Lá você poderá ver todos os endpoints, seus parâmetros, corpos de requisição/resposta esperados e até mesmo testá-los diretamente pelo navegador.

## 🧪 Testes

Para rodar os testes unitários e de integração do projeto, utilize o comando Maven:
```bash
mvn test