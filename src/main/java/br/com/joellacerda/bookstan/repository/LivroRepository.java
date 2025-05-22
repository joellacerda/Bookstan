package br.com.joellacerda.bookstan.repository;

import br.com.joellacerda.bookstan.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    // JpaRepository<TipoDaEntidade, TipoDoIdDaEntidade>

    // O Spring Data JPA automaticamente fornecerá implementações para métodos CRUD básicos:
    // save(), findById(), findAll(), deleteById(), etc.

    // Você pode adicionar métodos de consulta personalizados aqui, se necessário.
    // Exemplo (não precisa implementar agora):
    // List<Livro> findByAutor(String autor);
    // Optional<Livro> findByIsbn(String isbn);
}
