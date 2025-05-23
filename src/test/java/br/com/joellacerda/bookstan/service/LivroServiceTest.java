package br.com.joellacerda.bookstan.service;

import br.com.joellacerda.bookstan.exception.LivroNaoEncontradoException;
import br.com.joellacerda.bookstan.model.Livro;
import br.com.joellacerda.bookstan.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Habilita as anotações do Mockito para JUnit 5
public class LivroServiceTest {

    @Mock // Cria um mock (simulação) do LivroRepository
    private LivroRepository livroRepository;

    @InjectMocks // Cria uma instância de LivroService e injeta os mocks (como livroRepository) nela
    private LivroService livroService;

    private Livro livroValido;

    @BeforeEach // Metodo executado antes de cada teste (opcional, útil para setup comum)
    void setUp() {
        // Configura um objeto Livro padrão para usar nos testes
        livroValido = new Livro(1L, "O Senhor dos Anéis", "J.R.R. Tolkien", "Fantasia", 1954, "978-8533613379");
    }

    @Test
    @DisplayName("Deve retornar Livro quando ID existente é fornecido")
    void buscarLivroPorId_quandoIdExistente_retornaLivro() {
        // Arrange (Organizar/Configurar)
        Long idExistente = 1L;
        // Configura o mock do repositório: quando findById com idExistente for chamado,
        // retorne um Optional contendo o livroValido.
        when(livroRepository.findById(idExistente)).thenReturn(Optional.of(livroValido));

        // Act (Agir/Executar)
        // Chama o metodo do serviço que queremos testar
        Livro livroEncontrado = livroService.buscarLivroPorId(idExistente);

        // Assert (Verificar)
        // Verifica se o livro retornado não é nulo e se os atributos são os esperados
        assertNotNull(livroEncontrado);
        assertEquals(livroValido.getId(), livroEncontrado.getId());
        assertEquals(livroValido.getTitulo(), livroEncontrado.getTitulo());
        assertEquals("J.R.R. Tolkien", livroEncontrado.getAutor());

        // Verifica se o metodo findById do repositório foi chamado exatamente uma vez com o idExistente
        verify(livroRepository, times(1)).findById(idExistente);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando ID inexistente é fornecido")
    void buscarLivroPorId_quandoIdInexistente_lancaRecursoNaoEncontradoException() {
        // Arrange
        Long idInexistente = 99L;
        String mensagemEsperada = "Livro não encontrado com ID: " + idInexistente;
        // Configura o mock do repositório: quando findById com idInexistente for chamado,
        // retorne um Optional vazio (simulando que o livro não foi encontrado).
        when(livroRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        // Verifica se a exceção RecursoNaoEncontradoException é lançada
        // ao chamar o metodo do serviço com o idInexistente.
        LivroNaoEncontradoException exception = assertThrows(
                LivroNaoEncontradoException.class,
                () -> livroService.buscarLivroPorId(idInexistente)
        );

        // Verifica se a mensagem da exceção é a esperada
        assertEquals(mensagemEsperada, exception.getMessage());

        // Verifica se o metodo findById do repositório foi chamado exatamente uma vez com o idInexistente
        verify(livroRepository, times(1)).findById(idInexistente);
    }

    // --- Outros testes para LivroService podem vir aqui ---
    // Exemplo para criarLivro:
    @Test
    @DisplayName("Deve salvar e retornar Livro quando dados válidos são fornecidos para criar")
    void criarLivro_comDadosValidos_retornaLivroSalvo() {
        // Arrange
        // O livroValido já foi instanciado no setUp
        // Configura o mock do repositório: quando save for chamado com qualquer instância de Livro,
        // retorne o mesmo livro que foi passado como argumento (simulando que foi salvo).
        // Poderíamos ser mais específicos: when(livroRepository.save(livroValido)).thenReturn(livroValido);
        when(livroRepository.save(any(Livro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Livro livroSalvo = livroService.criarLivro(livroValido);

        // Assert
        assertNotNull(livroSalvo);
        assertEquals(livroValido.getTitulo(), livroSalvo.getTitulo());
        // Se o ID é gerado no save, e o mock retorna o objeto original, o ID pode ser nulo
        // ou o ID que você definiu no objeto 'livroValido'.
        // Se você quer simular a geração de ID:
        // Livro livroComIdGerado = new Livro(1L, "O Hobbit", ...);
        // when(livroRepository.save(any(Livro.class))).thenReturn(livroComIdGerado);

        // Verifica se o metodo save do repositório foi chamado exatamente uma vez com o livroValido
        verify(livroRepository, times(1)).save(livroValido);
    }
}
