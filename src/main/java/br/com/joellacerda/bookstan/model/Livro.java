package br.com.joellacerda.bookstan.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "O título não pode estar em branco.")
    @Size(min = 2, max = 100, message = "O título deve ter entre 2 e 100 caracteres.")
    @Column(nullable = false) // JPA: Mapeia para uma coluna, nullable = false significa que não pode ser nula
    private String titulo;

    @NotBlank(message = "O autor não pode estar em branco.")
    @Size(min = 2, max = 100, message = "O nome do autor deve ter entre 2 e 100 caracteres.")
    @Column(nullable = false)
    private String autor;

    @Size(max = 50, message = "O gênero deve ter no máximo 50 caracteres.")
    private String genero; // Coluna normal, pode ser nula

    @NotNull(message = "O ano de publicação não pode ser nulo.")
    @Min(value = 1000, message = "O ano de publicação deve ser um ano válido.")
    @Column(name = "ano_publicacao") // JPA: Nome da coluna no banco será "ano_publicacao"
    private Integer anoPublicacao;

    @Pattern(regexp = "^(?:ISBN(?:-13)?:? )?(?=[0-9]{13}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)97[89][- ]?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9]$",
            message = "Formato de ISBN inválido.")
    @Size(max = 20, message = "ISBN deve ter no máximo 20 caracteres.")
    @Column(unique = true) // JPA: Garante que o valor nesta coluna seja único (opcional para ISBN)
    private String isbn; // Opcional
}
