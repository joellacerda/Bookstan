package br.com.joellacerda.bookstan.controller;

import br.com.joellacerda.bookstan.model.Livro;
import br.com.joellacerda.bookstan.service.LivroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Combina @Controller e @ResponseBody, indicando que os retornos dos métodos serão o corpo da resposta HTTP
@RequestMapping("/api/livros") // Define o caminho base para todos os endpoints neste controller
public class LivroController {

    private final LivroService livroService;

    @Autowired // Injeção de dependência do serviço
    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    // Endpoint para CRIAR um novo livro
    // HTTP POST para /api/livros
    @PostMapping
    public ResponseEntity<Livro> criarLivro(/*@Valid*/ @RequestBody Livro livro) {
        // O @RequestBody indica que o objeto Livro virá do corpo da requisição HTTP (JSON)
        // @Valid (após adicionar a dependência de validação) acionaria as validações da entidade
        Livro novoLivro = livroService.criarLivro(livro);
        return new ResponseEntity<>(novoLivro, HttpStatus.CREATED); // Retorna 201 Created
    }

    // Endpoint para BUSCAR todos os livros
    // HTTP GET para /api/livros
    @GetMapping
    public ResponseEntity<List<Livro>> buscarTodosLivros() {
        List<Livro> livros = livroService.buscarTodosLivros();
        return ResponseEntity.ok(livros); // Retorna 200 OK
    }

    // Endpoint para BUSCAR um livro por ID
    // HTTP GET para /api/livros/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Livro> buscarLivroPorId(@PathVariable Long id) {
        // @PathVariable indica que o 'id' virá da URL (ex: /api/livros/1)
        Optional<Livro> livroOptional = livroService.buscarLivroPorId(id);

        return livroOptional.map(ResponseEntity::ok) // Se o livro existir, retorna 200 OK com o livro
                .orElseGet(() -> ResponseEntity.notFound().build()); // Senão, retorna 404 Not Found
    }

    // Endpoint para ATUALIZAR um livro existente
    // HTTP PUT para /api/livros/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Livro> atualizarLivro(@PathVariable Long id, /*@Valid*/ @RequestBody Livro livroDetalhes) {
        try {
            Livro livroAtualizado = livroService.atualizarLivro(id, livroDetalhes);
            return ResponseEntity.ok(livroAtualizado); // Retorna 200 OK com o livro atualizado
        } catch (RuntimeException e) { // Idealmente, uma exceção mais específica como RecursoNaoEncontradoException
            // Se o livro não for encontrado no serviço (ao lançar exceção), retorna 404
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint para DELETAR um livro por ID
    // HTTP DELETE para /api/livros/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarLivro(@PathVariable Long id) {
        try {
            livroService.deletarLivro(id);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content (sucesso, sem corpo na resposta)
        } catch (RuntimeException e) { // Idealmente, uma exceção mais específica
            // Se o livro não for encontrado para exclusão, retorna 404
            return ResponseEntity.notFound().build();
        }
    }
}
