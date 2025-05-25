package br.com.joellacerda.bookstan.dto;

import lombok.Data;

@Data
public class LivroResponseDTO {
    private Long id;
    private String titulo;
    private String autor;
    private String genero;
    private Integer anoPublicacao;
    private String isbn;
}
