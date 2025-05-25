package br.com.joellacerda.bookstan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LivroRequestDTO {
    @NotBlank(message = "O título não pode estar em branco.")
    @Size(min = 2, max = 100, message = "O título deve ter entre 2 e 100 caracteres.")
    private String titulo;

    @NotBlank(message = "O autor não pode estar em branco.")
    @Size(min = 2, max = 100, message = "O nome do autor deve ter entre 2 e 100 caracteres.")
    private String autor;

    @Size(max = 50, message = "O gênero deve ter no máximo 50 caracteres.")
    private String genero;

    @NotNull(message = "O ano de publicação não pode ser nulo.")
    private Integer anoPublicacao;

    @Pattern(regexp = "(ISBN-*(1[03])* *(: )?)*(([0-9Xx][- ]*){13}|([0-9Xx][- ]*){10})",
            message = "Formato de ISBN inválido.")
    @Size(max = 20, message = "ISBN deve ter no máximo 20 caracteres.")
    private String isbn;
}
