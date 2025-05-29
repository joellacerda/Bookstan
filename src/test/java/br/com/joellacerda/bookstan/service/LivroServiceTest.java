package br.com.joellacerda.bookstan.service;

import br.com.joellacerda.bookstan.dto.LivroRequestDTO;
import br.com.joellacerda.bookstan.dto.LivroResponseDTO;
import br.com.joellacerda.bookstan.exception.LivroNaoEncontradoException;
import br.com.joellacerda.bookstan.model.Livro;
import br.com.joellacerda.bookstan.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Habilita as anotações do Mockito para JUnit 5
public class LivroServiceTest {

    @Mock // Cria um mock (simulação) do LivroRepository
    private LivroRepository livroRepository;

    @InjectMocks // Cria uma instância de LivroService e injeta os mocks (como livroRepository) nela
    private LivroService livroService;

    private LivroRequestDTO livroRequestDTO;
    private Livro livroEntidade; // Entidade como seria salva ou recuperada do repo
    private Livro livroEntidadeComId; // Entidade após ser salva (com ID)

    @BeforeEach // Metodo executado antes de cada teste (opcional, útil para setup comum)
    void setUp() {
        livroRequestDTO = new LivroRequestDTO();
        livroRequestDTO.setTitulo("O Hobbit");
        livroRequestDTO.setAutor("J.R.R. Tolkien");
        livroRequestDTO.setGenero("Fantasia");
        livroRequestDTO.setAnoPublicacao(1937);
        livroRequestDTO.setIsbn("978-0547928227");

        // Entidade como seria mapeada a partir do DTO (sem ID ainda)
        livroEntidade = new Livro(
                livroRequestDTO.getTitulo(),
                livroRequestDTO.getAutor(),
                livroRequestDTO.getGenero(),
                livroRequestDTO.getAnoPublicacao(),
                livroRequestDTO.getIsbn()
        );

        // Entidade como seria retornada do repo após o save (com ID)
        livroEntidadeComId = new Livro(
                1L, // ID simulado
                livroRequestDTO.getTitulo(),
                livroRequestDTO.getAutor(),
                livroRequestDTO.getGenero(),
                livroRequestDTO.getAnoPublicacao(),
                livroRequestDTO.getIsbn()
        );
    }

    @Test
    @DisplayName("Deve criar um livro e retornar LivroResponseDTO")
    void criarLivro_comLivroRequestDTO_retornaLivroResponseDTO() {
        // Arrange
        // Configura o mock do repositório para retornar a entidade com ID quando 'save' for chamado.
        // Usamos any(Livro.class) porque o ID será nulo na entidade passada para save,
        // mas o objeto em si terá os outros campos do DTO.
        when(livroRepository.save(any(Livro.class))).thenReturn(livroEntidadeComId);

        // Act
        LivroResponseDTO responseDTO = livroService.criarLivro(livroRequestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertEquals(livroEntidadeComId.getId(), responseDTO.getId()); // Verifica o ID
        assertEquals(livroRequestDTO.getTitulo(), responseDTO.getTitulo());
        assertEquals(livroRequestDTO.getAutor(), responseDTO.getAutor());

        // Opcional: verificar se o objeto Livro correto foi passado para o método save
        ArgumentCaptor<Livro> livroArgumentCaptor = ArgumentCaptor.forClass(Livro.class);
        verify(livroRepository).save(livroArgumentCaptor.capture());
        Livro livroSalvoParaRepo = livroArgumentCaptor.getValue();

        assertNull(livroSalvoParaRepo.getId()); // O ID deve ser nulo antes de salvar
        assertEquals(livroRequestDTO.getTitulo(), livroSalvoParaRepo.getTitulo());
    }

    @Test
    @DisplayName("Deve retornar LivroResponseDTO quando ID existente é fornecido")
    void buscarLivroPorId_quandoIdExistente_retornaLivroResponseDTO() {
        // Arrange
        Long idExistente = 1L;
        // O repositório retorna a entidade com ID
        when(livroRepository.findById(idExistente)).thenReturn(Optional.of(livroEntidadeComId));

        // Act
        LivroResponseDTO responseDTO = livroService.buscarLivroPorId(idExistente);

        // Assert
        assertNotNull(responseDTO);
        assertEquals(idExistente, responseDTO.getId());
        assertEquals(livroEntidadeComId.getTitulo(), responseDTO.getTitulo());
        verify(livroRepository, times(1)).findById(idExistente);
    }


    @Test
    @DisplayName("Deve lançar LivroNaoEncontradoException quando ID inexistente é fornecido para busca")
    void buscarLivroPorId_quandoIdInexistente_lancaLivroNaoEncontradoException() {
        // Arrange
        Long idInexistente = 99L;
        when(livroRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        LivroNaoEncontradoException exception = assertThrows(
                LivroNaoEncontradoException.class,
                () -> livroService.buscarLivroPorId(idInexistente)
        );
        assertEquals("Livro não encontrado com ID: " + idInexistente, exception.getMessage());
        verify(livroRepository, times(1)).findById(idInexistente);
    }

    @Test
    @DisplayName("Deve retornar Page de LivroResponseDTO ao buscar todos os livros com paginação")
    void buscarTodosLivros_comPageable_retornaPageDeLivroResponseDTO() {
        // Arrange
        Livro outroLivroEntidade = new Livro(2L, "1984", "George Orwell", "Distopia", 1949, "978-0451524935");
        List<Livro> listaDeEntidades = Arrays.asList(livroEntidadeComId, outroLivroEntidade);
        // Define o Pageable que seria passado para o serviço
        Pageable pageable = PageRequest.of(0, 5, Sort.by("titulo").ascending());
        // Cria um objeto Page<Livro> para ser retornado pelo mock do repositório
        // Parâmetros: conteúdo da página, pageable da requisição, total de elementos no banco
        Page<Livro> paginaDeEntidadesMock = new PageImpl<>(listaDeEntidades, pageable, listaDeEntidades.size());
        // Configura o mock do repositório
        when(livroRepository.findAll(pageable)).thenReturn(paginaDeEntidadesMock);

        // Act
        Page<LivroResponseDTO> resultPage = livroService.buscarTodosLivros(pageable);

        // Assert
        assertNotNull(resultPage);
        assertEquals(2, resultPage.getTotalElements(), "Total de elementos deve ser 2");
        assertEquals(1, resultPage.getTotalPages(), "Total de páginas deve ser 1 para 2 elementos com size 5");
        assertEquals(0, resultPage.getNumber(), "Número da página atual deve ser 0");
        assertEquals(2, resultPage.getContent().size(), "Conteúdo da página deve ter 2 livros");
        assertEquals(livroEntidadeComId.getTitulo(), resultPage.getContent().get(0).getTitulo());
        assertEquals(outroLivroEntidade.getTitulo(), resultPage.getContent().get(1).getTitulo());

        verify(livroRepository, times(1)).findAll(pageable);
    }


    @Test
    @DisplayName("Deve atualizar um livro e retornar LivroResponseDTO")
    void atualizarLivro_comIdExistenteELivroRequestDTO_retornaLivroResponseDTO() {
        // Arrange
        Long idExistente = 1L;
        // DTO com os novos dados
        LivroRequestDTO atualizacaoRequestDTO = new LivroRequestDTO();
        atualizacaoRequestDTO.setTitulo("O Hobbit - Edição Revisada");
        atualizacaoRequestDTO.setAutor("J.R.R. Tolkien"); // Mesmo autor
        atualizacaoRequestDTO.setGenero("Alta Fantasia"); // Gênero atualizado
        atualizacaoRequestDTO.setAnoPublicacao(1937);
        atualizacaoRequestDTO.setIsbn("978-0547928227");

        // Entidade existente no banco (antes da atualização)
        Livro livroExistenteNoBanco = new Livro(idExistente, "O Hobbit", "J.R.R. Tolkien", "Fantasia", 1937, "978-0547928227");

        // Entidade como ela deve ficar após a atualização e ser salva
        Livro livroEsperadoAposSalvar = new Livro(idExistente, atualizacaoRequestDTO.getTitulo(), atualizacaoRequestDTO.getAutor(), atualizacaoRequestDTO.getGenero(), atualizacaoRequestDTO.getAnoPublicacao(), atualizacaoRequestDTO.getIsbn());

        when(livroRepository.findById(idExistente)).thenReturn(Optional.of(livroExistenteNoBanco));
        when(livroRepository.save(any(Livro.class))).thenReturn(livroEsperadoAposSalvar); // O save retorna a entidade atualizada

        // Act
        LivroResponseDTO responseDTO = livroService.atualizarLivro(idExistente, atualizacaoRequestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertEquals(idExistente, responseDTO.getId());
        assertEquals(atualizacaoRequestDTO.getTitulo(), responseDTO.getTitulo());
        assertEquals(atualizacaoRequestDTO.getGenero(), responseDTO.getGenero());

        // Verifica se o save foi chamado com a entidade correta (já atualizada)
        ArgumentCaptor<Livro> livroArgumentCaptor = ArgumentCaptor.forClass(Livro.class);
        verify(livroRepository).save(livroArgumentCaptor.capture());
        Livro livroPassadoParaSave = livroArgumentCaptor.getValue();

        assertEquals(idExistente, livroPassadoParaSave.getId()); // O ID deve ser o mesmo
        assertEquals(atualizacaoRequestDTO.getTitulo(), livroPassadoParaSave.getTitulo());
        assertEquals(atualizacaoRequestDTO.getGenero(), livroPassadoParaSave.getGenero());
    }

    @Test
    @DisplayName("Deve lançar LivroNaoEncontradoException ao tentar atualizar livro com ID inexistente")
    void atualizarLivro_quandoIdInexistente_lancaLivroNaoEncontradoException() {
        // Arrange
        Long idInexistente = 99L;
        when(livroRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        LivroNaoEncontradoException exception = assertThrows(
                LivroNaoEncontradoException.class,
                () -> livroService.atualizarLivro(idInexistente, livroRequestDTO) // livroRequestDTO é só um placeholder aqui
        );
        assertEquals("Livro não encontrado com ID: " + idInexistente, exception.getMessage());
        verify(livroRepository, times(1)).findById(idInexistente);
        verify(livroRepository, never()).save(any(Livro.class)); // Garante que save não foi chamado
    }


    @Test
    @DisplayName("Deve deletar livro quando ID existente é fornecido")
    void deletarLivro_quandoIdExistente_naoDeveLancarExcecao() {
        // Arrange
        Long idExistente = 1L;
        when(livroRepository.existsById(idExistente)).thenReturn(true);
        // void não retorna nada, então usamos doNothing() para o mock de deleteById
        doNothing().when(livroRepository).deleteById(idExistente);

        // Act & Assert
        assertDoesNotThrow(() -> livroService.deletarLivro(idExistente));

        verify(livroRepository, times(1)).existsById(idExistente);
        verify(livroRepository, times(1)).deleteById(idExistente);
    }

    @Test
    @DisplayName("Deve lançar LivroNaoEncontradoException ao tentar deletar livro com ID inexistente")
    void deletarLivro_quandoIdInexistente_lancaLivroNaoEncontradoException() {
        // Arrange
        Long idInexistente = 99L;
        when(livroRepository.existsById(idInexistente)).thenReturn(false);

        // Act & Assert
        LivroNaoEncontradoException exception = assertThrows(
                LivroNaoEncontradoException.class,
                () -> livroService.deletarLivro(idInexistente)
        );
        assertEquals("Livro não encontrado com ID: " + idInexistente, exception.getMessage());
        verify(livroRepository, times(1)).existsById(idInexistente);
        verify(livroRepository, never()).deleteById(anyLong()); // Garante que deleteById não foi chamado
    }
}
