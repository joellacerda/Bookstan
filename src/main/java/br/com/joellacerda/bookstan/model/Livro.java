package br.com.joellacerda.bookstan.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // Lombok: Gera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok: Gera um construtor sem argumentos
@AllArgsConstructor // Lombok: Gera um construtor com todos os argumentos
@Entity // JPA: Indica que esta classe é uma entidade JPA (mapeada para uma tabela)
@Table(name = "livros") // JPA: Especifica o nome da tabela no banco de dados
public class Livro {

    @Id // JPA: Indica que este atributo é a chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // JPA: Define como a chave primária é gerada (auto-incremento pelo banco)
    private Long id;

    @Column(nullable = false) // JPA: Mapeia para uma coluna, nullable = false significa que não pode ser nula
    private String titulo;

    @Column(nullable = false)
    private String autor;

    private String genero; // Coluna normal, pode ser nula

    @Column(name = "ano_publicacao") // JPA: Nome da coluna no banco será "ano_publicacao"
    private Integer anoPublicacao;

    @Column(unique = true) // JPA: Garante que o valor nesta coluna seja único (opcional para ISBN)
    private String isbn; // Opcional
}
