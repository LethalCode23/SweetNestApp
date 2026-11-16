package com.dh.demo.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Crear o actualizar la valoracion del usuario del token sobre un hotel. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    @NotNull(message = "Elige de 1 a 5 estrellas")
    @Min(value = 1, message = "Elige de 1 a 5 estrellas")
    @Max(value = 5, message = "Elige de 1 a 5 estrellas")
    private Integer stars;

    /** Opcional. Vacio o solo espacios = sin comentario. */
    @Size(max = 1000, message = "La resena admite maximo 1000 caracteres")
    private String comment;
}
