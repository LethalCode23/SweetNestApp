package com.dh.demo.common;

public final class ResponseMessage    {

    private ResponseMessage() {
        throw new UnsupportedOperationException("Esta es una clase de constantes y no debe ser instanciada");
    }

    public static final String HOTEL_CREATED_SUCCESS = "El hotel fue registrado correctamente";
    public static final String HOTEL_NOT_FOUND = "No se encontró el hotel con el ID especificado";
    public static final String INVALID_PAYLOAD = "Los datos enviados no cumplen con el formato requerido";
}