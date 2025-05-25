package br.com.joellacerda.bookstan.controller;

import br.com.joellacerda.bookstan.dto.LivroRequestDTO;
import br.com.joellacerda.bookstan.dto.LivroResponseDTO;
import br.com.joellacerda.bookstan.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Combina @Controller e @ResponseBody, indicando que os retornos dos métodos serão o corpo da resposta HTTP
@RequestMapping("/api/livros") // Define o caminho base para todos os endpoints neste controller
@Tag(name = "Livros", description = "API para gerenciamento de livros") // Agrupa os endpoints
public class LivroController {

    private final LivroService livroService;

    @Autowired // Injeção de dependência do serviço
    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    // Endpoint para CRIAR um novo livro
    // HTTP POST para /api/livros
    @Operation(summary = "Cria um novo livro",
            description = "Adiciona um novo livro ao sistema. O ID será gerado automaticamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Livro criado com sucesso", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = LivroResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos",
                    content = @Content) // Você pode detalhar o schema do erro 400 se quiser
    })
    @PostMapping
    public ResponseEntity<LivroResponseDTO> criarLivro(@Valid @RequestBody LivroRequestDTO livroRequestDTO) {
        // O @RequestBody indica que o objeto Livro virá do corpo da requisição HTTP (JSON)
        // @Valid (após adicionar a dependência de validação) acionaria as validações da entidade
        LivroResponseDTO novoLivro = livroService.criarLivro(livroRequestDTO);
        return new ResponseEntity<>(novoLivro, HttpStatus.CREATED);
    }

    // Endpoint para BUSCAR todos os livros
    // HTTP GET para /api/livros
    @Operation(summary = "Lista todos os livros", description = "Retorna uma lista de todos os livros cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de livros recuperada com sucesso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LivroResponseDTO.class)) }) // Indica que retorna uma lista de Livro
    })
    @GetMapping
    public ResponseEntity<List<LivroResponseDTO>> buscarTodosLivros() {
        List<LivroResponseDTO> livros = livroService.buscarTodosLivros();
        return ResponseEntity.ok(livros);
    }

    // Endpoint para BUSCAR um livro por ID
    // HTTP GET para /api/livros/{id}
    @Operation(summary = "Busca um livro por ID", description = "Retorna um livro específico baseado no seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livro encontrado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LivroResponseDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado com o ID fornecido",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> buscarLivroPorId(
            @Parameter(description = "ID do livro a ser buscado", required = true, example = "1")
            @PathVariable Long id) {
        LivroResponseDTO livro = livroService.buscarLivroPorId(id);
        return ResponseEntity.ok(livro);
    }

    // Endpoint para ATUALIZAR um livro existente
    // HTTP PUT para /api/livros/{id}
    @Operation(summary = "Atualiza um livro existente", description = "Atualiza os dados de um livro existente baseado no seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = LivroResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado com o ID fornecido")
    })
    @PutMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> atualizarLivro(
            @Parameter(description = "ID do livro a ser atualizado", required = true) @PathVariable Long id,
            @Valid @RequestBody LivroRequestDTO livroRequestDTO) {
        LivroResponseDTO livroAtualizado = livroService.atualizarLivro(id, livroRequestDTO);
        return ResponseEntity.ok(livroAtualizado);
    }

    // Endpoint para DELETAR um livro por ID
    // HTTP DELETE para /api/livros/{id}
    @Operation(summary = "Deleta um livro por ID", description = "Remove um livro do sistema baseado no seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Livro deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado com o ID fornecido")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarLivro(
            @Parameter(description = "ID do livro a ser deletado", required = true) @PathVariable Long id) {
        livroService.deletarLivro(id);
        return ResponseEntity.noContent().build();
    }
}
