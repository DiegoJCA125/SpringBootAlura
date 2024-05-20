package com.aluracursos.screenmatch.principal;

import java.util.Arrays;
import java.util.List;

public class EjemploStreams {
    public void muestraEjemplo(){
        List<String> nombres = Arrays.asList("Juan Diego" , "Karen","Yuls", "David", "Valen");
        nombres.stream()
                .sorted()
                .limit(4)
                .filter(n-> n.startsWith("K"))
                .map(n -> n.toUpperCase())
                .forEach(System.out::println);
    }
}
