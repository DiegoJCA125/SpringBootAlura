package com.aluracursos.screenmatch.service;

public interface IConvierteDatos {
    //convierte los datos genericos
    <T> T obtenerDatos(String json, Class<T> clase);
}
