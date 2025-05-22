package br.com.joellacerda.bookstan.service;

import br.com.joellacerda.bookstan.model.Livro;
import br.com.joellacerda.bookstan.repository.LivroRepository;
import br.com.joellacerda.bookstan.exception.RecursoNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service // Indica ao Spring que esta classe é um componente de serviço
public class LivroService {

    private final LivroRepository livroRepository;

    @Autowired // Injeção de dependência via construtor
    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    // Metodo para CRIAR um novo livro
    @Transactional // Garante que a operação seja atômica (ou tudo funciona, ou nada é alterado)
    public Livro criarLivro(Livro livro) {
        // Aqui você pode adicionar lógicas de negócio antes de salvar,
        // por exemplo, verificar se um livro com o mesmo ISBN já existe.
        // Ex: if(livroRepository.findByIsbn(livro.getIsbn()).isPresent()) {
        //         throw new IllegalArgumentException("Livro com este ISBN já existe.");
        //     }
        return livroRepository.save(livro);
    }

    // Metodo para BUSCAR todos os livros
    @Transactional(readOnly = true) // Indica que é uma transação apenas de leitura (otimizações)
        public List<Livro> buscarTodosLivros() {
        return livroRepository.findAll();
    }

    // Metodo para BUSCAR um livro por ID
    @Transactional(readOnly = true)
    public Livro buscarLivroPorId(Long id) {
        // Tenta buscar o livro. Se não encontrar, lança RecursoNaoEncontradoException.
        return livroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado com ID: " + id));
    }

    // Metodo para ATUALIZAR um livro existente
    @Transactional
    public Livro atualizarLivro(Long id, Livro livroDetalhes) {
        // Primeiro, verifica se o livro existe
        Livro livroExistente = livroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado com ID: " + id));

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
            throw new RecursoNaoEncontradoException("Livro não encontrado com ID: " + id + " para exclusão.");
        }
        livroRepository.deleteById(id);
    }

    // Opcional: Se quiser que buscarLivroPorId lance exceção em vez de retornar Optional para o controller tratar
    // @Transactional(readOnly = true)
    // public Livro buscarLivroPorId(Long id) {
    //     return livroRepository.findById(id)
    //            .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado com ID: " + id));
    // }
    }
