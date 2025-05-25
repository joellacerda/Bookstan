package br.com.joellacerda.bookstan.controller;

import br.com.joellacerda.bookstan.dto.LivroRequestDTO;
import br.com.joellacerda.bookstan.model.Livro;
import br.com.joellacerda.bookstan.repository.LivroRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest // Carrega o contexto completo da aplicação Spring Boot para o teste
@AutoConfigureMockMvc // Configura automaticamente o MockMvc
@ActiveProfiles("test") // Opcional: Ativa um perfil "test" se você tiver um application-test.properties
                        // Útil para, por exemplo, garantir que está usando H2
@Transactional // Garante que cada teste rode em sua própria transação, que é revertida ao final.
               // Isso ajuda a manter o estado do banco de dados limpo entre os testes.
public class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc; // Permite simular requisições HTTP para seus controllers

    @Autowired
    private ObjectMapper objectMapper; // Usado para converter objetos Java para JSOn e vice-versa

    @Autowired
    private LivroRepository livroRepository; // Para manipular dados no banco diretamente para setup de testes

    private Livro livroExemplo1;
    private Livro livroExemplo2;

    // Para requisições, usaremos LivroRequestDTO
    private LivroRequestDTO livroRequestExemplo;

    @BeforeEach
    void setUp() {
        // Limpa o repositório antes de cada teste para evitar interferências
        // Embora @Transactional já ajude, uma limpeza explícita pode ser útil em cenários mais complexos
        // ou se @Transactional não for usado em todos os métodos.
        livroRepository.deleteAll();

        // Configura alguns livros de exemplo
        livroExemplo1 = new Livro(null, "A Revolução dos Bichos", "George Orwell", "Sátira Política", 1945, "978-8535902776");
        livroExemplo2 = new Livro(null, "1984", "George Orwell", "Distopia", 1949, "978-0451524935");

        // DTO para usar nas requisições POST/PUT
        livroRequestExemplo = new LivroRequestDTO();
        livroRequestExemplo.setTitulo("O Hobbit");
        livroRequestExemplo.setAutor("J.R.R. Tolkien");
        livroRequestExemplo.setGenero("Fantasia");
        livroRequestExemplo.setAnoPublicacao(1937);
        livroRequestExemplo.setIsbn("978-0547928227");
    }

    @Test
    @DisplayName("POST /api/livros - Deve criar um novo livro e retornar status 201")
    void criarLivro_quandoDadosValidos_retornaLivroCriadoComStatus201() throws Exception {
        // Arrange
        String livroJson = objectMapper.writeValueAsString(livroRequestExemplo);

        // Act & Assert
        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(livroJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists()) // ID existe na resposta (LivroResponseDTO)
                .andExpect(jsonPath("$.titulo", is(livroRequestExemplo.getTitulo())))
                .andExpect(jsonPath("$.autor", is(livroRequestExemplo.getAutor())));
    }

    @Test
    @DisplayName("POST /api/livros - Deve retornar status 400 quando dados inválidos (título em branco)")
    void criarLivro_quandoTituloEmBranco_retornaStatusBadRequest() throws Exception {
        // Arrange
        LivroRequestDTO livroInvalidoRequest = new LivroRequestDTO();
        livroInvalidoRequest.setTitulo(""); // Título inválido
        livroInvalidoRequest.setAutor("Autor Válido");
        livroInvalidoRequest.setAnoPublicacao(2000);
        livroInvalidoRequest.setIsbn("978-0451524936"); // ISBN válido para focar no erro do título
        // Outros campos válidos para isolar o erro do título
        livroInvalidoRequest.setGenero("Qualquer");

        String livroJson = objectMapper.writeValueAsString(livroInvalidoRequest);

        // Act & Assert
        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(livroJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", hasItem("titulo: O título não pode estar em branco.")))
                .andExpect(jsonPath("$.messages", hasItem("titulo: O título deve ter entre 2 e 100 caracteres.")));
    }

    @Test
    @DisplayName("GET /api/livros/{id} - Deve retornar livro quando ID existente e status 200")
    void buscarLivroPorId_quandoIdExistente_retornaLivroComStatus200() throws Exception {
        // Arrange: Salva uma entidade no banco
        Livro livroSalvo = livroRepository.save(livroExemplo1);
        Long idExistente = livroSalvo.getId();

        // Act & Assert
        mockMvc.perform(get("/api/livros/{id}", idExistente)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(idExistente.intValue())))
                .andExpect(jsonPath("$.titulo", is(livroExemplo1.getTitulo())));
    }

    @Test
    @DisplayName("GET /api/livros/{id} - Deve retornar status 404 quando ID inexistente")
    void buscarLivroPorId_quandoIdInexistente_retornaStatus404() throws Exception {
        // Arrange
        Long idInexistente = 999L;

        // Act & Assert
        mockMvc.perform(get("/api/livros/{id}", idInexistente)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Livro não encontrado com ID: " + idInexistente)));
    }

    @Test
    @DisplayName("GET /api/livros - Deve retornar lista de livros e status 200")
    void buscarTodosLivros_retornaListaDeLivrosComStatus200() throws Exception {
        // Arrange: Salva alguns livros
        livroRepository.save(livroExemplo1);
        livroRepository.save(livroExemplo2);

        // Act & Assert
        mockMvc.perform(get("/api/livros")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].titulo", is(livroExemplo1.getTitulo())))
                .andExpect(jsonPath("$[1].titulo", is(livroExemplo2.getTitulo())));
    }

    @Test
    @DisplayName("PUT /api/livros/{id} - Deve retornar livro atualizado e status 200")
    void atualizarLivro_quandoIdExistente_retornaLivroComStatus200() throws Exception {
        // Arrange
        Livro livroSalvo = livroRepository.save(livroExemplo1);
        Long idDoLivroSalvo = livroSalvo.getId();

        LivroRequestDTO dadosAtualizacao = new LivroRequestDTO();
        dadosAtualizacao.setTitulo("A Revolução dos Bichos [Editado]");
        dadosAtualizacao.setAutor(livroSalvo.getAutor());
        dadosAtualizacao.setGenero("Fábula Política");
        dadosAtualizacao.setAnoPublicacao(livroSalvo.getAnoPublicacao());
        dadosAtualizacao.setIsbn(livroSalvo.getIsbn());

        String livroAtualizadoJson = objectMapper.writeValueAsString(dadosAtualizacao);

        // Act & Assert
        mockMvc.perform(put("/api/livros/{id}", idDoLivroSalvo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(livroAtualizadoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(idDoLivroSalvo.intValue())))
                .andExpect(jsonPath("$.titulo", is(dadosAtualizacao.getTitulo())))
                .andExpect(jsonPath("$.genero", is(dadosAtualizacao.getGenero())));
    }

    @Test
    @DisplayName("PUT /api/livros/{id} - Deve retornar status 404 quando ID inexistente")
    void atualizarLivro_quandoIdInexistente_retornaStatus404() throws Exception {
        // Arrange
        Long idInexistente = 999L;
        // Usamos o livroRequestExemplo apenas como um corpo de requisição válido
        String livroAtualizadoJson = objectMapper.writeValueAsString(livroRequestExemplo);

        // Act & Assert
        mockMvc.perform(put("/api/livros/{id}", idInexistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(livroAtualizadoJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Livro não encontrado com ID: " + idInexistente)));
    }

    @Test
    @DisplayName("PUT /api/livros/{id} - Deve retornar status 400 quando dados inválidos")
    void atualizarLivro_quandoDadosInvalidos_retornaStatus400() throws Exception {
        // Arrange
        Livro livroSalvo = livroRepository.save(livroExemplo1);
        Long idDoLivroSalvo = livroSalvo.getId();

        LivroRequestDTO livroInvalidoRequest = new LivroRequestDTO();
        livroInvalidoRequest.setTitulo(""); // Título inválido
        livroInvalidoRequest.setAutor("George Orwell");
        livroInvalidoRequest.setAnoPublicacao(1945);
        livroInvalidoRequest.setIsbn("12345INVALIDO"); // ISBN inválido
        livroInvalidoRequest.setGenero("Sátira Política");


        String livroAtualizadoJson = objectMapper.writeValueAsString(livroInvalidoRequest);

        // Act & Assert
        mockMvc.perform(put("/api/livros/{id}", idDoLivroSalvo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(livroAtualizadoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", containsInAnyOrder(
                        "titulo: O título não pode estar em branco.",
                        "titulo: O título deve ter entre 2 e 100 caracteres.",
                        "isbn: Formato de ISBN inválido.")));
    }

    @Test
    @DisplayName("DELETE /api/livros/{id} - Deve retornar status 204 quando livro deletado")
    void deletarLivro_quandoIdExistente_retornaStatus204() throws Exception {
        // Arrange: Salva um livro no banco para que ele exista
        Livro livroSalvo = livroRepository.save(livroExemplo1);
        Long idDoLivroSalvo = livroSalvo.getId();

        // Act & Assert
        mockMvc.perform(delete("/api/livros/{id}", idDoLivroSalvo))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/livros/{id}", idDoLivroSalvo)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/livros/{id} - Deve retornar status 404 quando ID inexistente")
    void deletarLivro_quandoIdInexistente_retornaStatus404() throws Exception {
        // Arrange
        Long idInexistente = 999L;

        // Act & Assert
        mockMvc.perform(delete("/api/livros/{id}", idInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Livro não encontrado com ID: " + idInexistente)));
    }
}
