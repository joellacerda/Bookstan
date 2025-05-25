package br.com.joellacerda.bookstan.service;

import br.com.joellacerda.bookstan.dto.LivroRequestDTO;
import br.com.joellacerda.bookstan.dto.LivroResponseDTO;
import br.com.joellacerda.bookstan.model.Livro;
import br.com.joellacerda.bookstan.repository.LivroRepository;
import br.com.joellacerda.bookstan.exception.LivroNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service // Indica ao Spring que esta classe é um componente de serviço
public class LivroService {

    private final LivroRepository livroRepository;

    @Autowired // Injeção de dependência via construtor
    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    // Métodos de Mapeamento (privados ou em uma classe Mapper separada)
    private Livro toEntity(LivroRequestDTO dto) {
        return new Livro(
                dto.getTitulo(),
                dto.getAutor(),
                dto.getGenero(),
                dto.getAnoPublicacao(),
                dto.getIsbn()
        );
    }

    private LivroResponseDTO toResponseDTO(Livro entity) {
        LivroResponseDTO dto = new LivroResponseDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setAutor(entity.getAutor());
        dto.setGenero(entity.getGenero());
        dto.setAnoPublicacao(entity.getAnoPublicacao());
        dto.setIsbn(entity.getIsbn());
        return dto;
    }

    // Metodo para CRIAR um novo livro
    @Transactional
    public LivroResponseDTO criarLivro(LivroRequestDTO livroRequestDTO) {
        Livro livro = toEntity(livroRequestDTO);
        // Aqui você poderia adicionar lógicas de negócio, como verificar se ISBN já existe
        // if (livroRepository.findByIsbn(livro.getIsbn()).isPresent()) {
        //     throw new IllegalArgumentException("Livro com este ISBN já existe.");
        // }
        Livro livroSalvo = livroRepository.save(livro);
        return toResponseDTO(livroSalvo);
    }

    // Metodo para BUSCAR todos os livros
    @Transactional(readOnly = true)
    public List<LivroResponseDTO> buscarTodosLivros() {
        return livroRepository.findAll()
                .stream()
                .map(this::toResponseDTO) // Equivalente a .map(livro -> toResponseDTO(livro))
                .collect(Collectors.toList());
    }

    // Metodo para BUSCAR um livro por ID
    @Transactional(readOnly = true)
    public LivroResponseDTO buscarLivroPorId(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new LivroNaoEncontradoException("Livro não encontrado com ID: " + id));
        return toResponseDTO(livro);
    }

    // Metodo para ATUALIZAR um livro existente
    @Transactional
    public LivroResponseDTO atualizarLivro(Long id, LivroRequestDTO livroRequestDTO) {
        Livro livroExistente = livroRepository.findById(id)
                .orElseThrow(() -> new LivroNaoEncontradoException("Livro não encontrado com ID: " + id));

        // Atualiza os campos da entidade com os valores do DTO
        livroExistente.setTitulo(livroRequestDTO.getTitulo());
        livroExistente.setAutor(livroRequestDTO.getAutor());
        livroExistente.setGenero(livroRequestDTO.getGenero());
        livroExistente.setAnoPublicacao(livroRequestDTO.getAnoPublicacao());
        livroExistente.setIsbn(livroRequestDTO.getIsbn());

        Livro livroAtualizado = livroRepository.save(livroExistente);
        return toResponseDTO(livroAtualizado);
    }

    // Metodo para DELETAR um livro por ID
    @Transactional
    public void deletarLivro(Long id) {
        if (!livroRepository.existsById(id)) {
            throw new LivroNaoEncontradoException("Livro não encontrado com ID: " + id);
        }
        livroRepository.deleteById(id);
    }
}
