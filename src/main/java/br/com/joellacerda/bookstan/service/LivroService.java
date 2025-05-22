package br.com.joellacerda.bookstan.service;

import br.com.joellacerda.bookstan.model.Livro;
import br.com.joellacerda.bookstan.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LivroService {

    private final LivroRepository livroRepository;

    @Autowired
    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    // Metodo para CRIAR um novo livro
    @Transactional
    public Livro criarLivro(Livro livro) {
        // Aqui você pode adicionar lógicas de negócio antes de salvar,
        // por exemplo, verificar se um livro com o mesmo ISBN já existe.
        // Ex: if(livroRepository.findByIsbn(livro.getIsbn()).isPresent()) {
        //         throw new IllegalArgumentException("Livro com este ISBN já existe.");
        //     }
        return livroRepository.save(livro);
    }

    // Metodo para BUSCAR todos os livros
    @Transactional(readOnly = true)
    public List<Livro> buscarTodosLivros() {
        return livroRepository.findAll();
    }

    // Metodo para BUSCAR um livro por ID
    @Transactional(readOnly = true)
    public Optional<Livro> buscarLivroPorId(Long id) {
        // O metodo findById do JpaRepository retorna um Optional,
        // que é uma forma elegante de lidar com valores que podem ser nulos.
        return livroRepository.findById(id);
        // No controller, você tratará o Optional (ex: se presente, retorna o livro; senão, retorna 404 Not Found)
        // Ou, você pode lançar uma exceção aqui se o livro não for encontrado:
        // return livroRepository.findById(id)
        //        .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado com ID: " + id));
    }

    // Metodo para ATUALIZAR um livro existente
    @Transactional
    public Livro atualizarLivro(Long id, Livro livroDetalhes) {
        // Primeiro, verifica se o livro existe
        Livro livroExistente = livroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado com ID: " + id));
        // (Idealmente, use uma exceção customizada como RecursoNaoEncontradoException)

        // Atualiza os campos do livro existente com os detalhes fornecidos
        livroExistente.setTitulo(livroDetalhes.getTitulo());
        livroExistente.setAutor(livroDetalhes.getAutor());
        livroExistente.setGenero(livroDetalhes.getGenero());
        livroExistente.setAnoPublicacao(livroDetalhes.getAnoPublicacao());
        livroExistente.setIsbn(livroDetalhes.getIsbn());
        // Não atualizamos o ID

        return livroRepository.save(livroExistente);
    }

    // Metodo para DELETAR um livro por ID
    @Transactional
    public void deletarLivro(Long id) {
        // Verifica se o livro existe antes de tentar deletar
        if (!livroRepository.existsById(id)) {
            throw new RuntimeException("Livro não encontrado com ID: " + id + " para exclusão.");
            // (Idealmente, use uma exceção customizada)
        }
        livroRepository.deleteById(id);
    }

    // --- (Opcional) Métodos de consulta personalizados que você pode adicionar ao Repository e usar aqui ---
    // Exemplo, se você adicionar `Optional<Livro> findByIsbn(String isbn);` em LivroRepository:
    //
    // @Transactional(readOnly = true)
    // public Optional<Livro> buscarLivroPorIsbn(String isbn) {
    //     return livroRepository.findByIsbn(isbn);
    // }
}
