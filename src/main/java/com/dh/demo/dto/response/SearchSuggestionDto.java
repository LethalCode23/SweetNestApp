package com.dh.demo.dto.response;

/**
 * Sugerencia del buscador (autocompletado).
 * type = "CITY"  -> id = citSec, el front busca por citName.
 * type = "HOTEL" -> id = hotSec, el front navega al detalle.
 */
public record SearchSuggestionDto(String type, Integer id, String name) {
}
