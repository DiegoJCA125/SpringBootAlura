package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.DatosEpisodio;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporadas;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=61a8600e";
    private ConvierteDatos conversor = new ConvierteDatos();

    public void muestraElMenu(){
        System.out.println("Por favor escribe el nombre de la serie que desea buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        var datos = conversor.obtenerDatos(json, DatosSerie.class);
        System.out.println(datos);

        //Busca los datos de todas las temporadas
        List<DatosTemporadas> temporadas = new ArrayList<>();
        for (int i = 1; i <= datos.totalDeTemporadas() ; i++) {
            json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + "&Season=" + i + API_KEY);
            var datosTemporadas = conversor.obtenerDatos(json, DatosTemporadas.class);
            temporadas.add(datosTemporadas);
        }
        // temporadas.forEach(System.out::println);

        //Mostrar solo el titulo de los episodios para las temporadas
        // for (int i = 0; i < datos.totalDeTemporadas() ; i++) {
        //    List<DatosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
        //    for (int j = 0; j < episodiosTemporada.size(); j++) {
        //        System.out.println(episodiosTemporada.get(j).titulo());
        //    }
        //}

        // con esta forma ahorramos lineas de codigo lo cual realiza la misma funcion del codigo de arriba
        // temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        // Convertir todas las informacion a una lista del tipo DatosEpisodio

        List<DatosEpisodio> datosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        // Top 5 episodios
        // System.out.println("Top 5 Episodios");
        // datosEpisodios.stream()
        //        .filter(e -> !e.evaluacion().equalsIgnoreCase("N/A"))
        //        .peek(e -> System.out.println("Primer Filtro (N/A)" + e))
        //        .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
        //       .peek(e -> System.out.println("Segundo Filtro ordenacion (M>m)" + e))
        //        .map(e -> e.titulo().toUpperCase())
        //        .peek(e -> System.out.println("Tercer Filtro Mayusculas (m>M)" + e))
        //        .limit(5)
        //        .forEach(System.out::println);

        // Convirtiendo los datos a una lista tipo episodio
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(),d)))
                .collect(Collectors.toList());

        // episodios.forEach(System.out::println);

        // Busqueda de episodios por fecha
        // System.out.println("Por favor indica el año que quieras buscar los episodios:");
        // var fecha = teclado.nextInt();
        // teclado.nextLine();

        // LocalDate fechaBusqueda = LocalDate.of(fecha,01,01);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        // episodios.stream()
        //        .filter(e -> e.getFechaDeLanzamiento() != null && e.getFechaDeLanzamiento().isAfter(fechaBusqueda))
        //        .forEach(e -> System.out.println(
        //                "Tempoarada " + e.getTemporada() +
        //                        "Episodio " + e.getTitulo() +
        //                        "Fecha de Lanzamiento: " + e.getFechaDeLanzamiento().format(dtf)
        //        ));

        // Busca episodio por un pedazo del titulo
        // System.out.println("Ingrese el nombre del episodio que desea ver");;
//        var pedazoTitulo = teclado.nextLine();
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(pedazoTitulo.toUpperCase()))
//                .findFirst();
//        if (episodioBuscado.isPresent()){
//            System.out.println(" Episodio encontrado");
//            System.out.println(" Los datos son: " + episodioBuscado.get());
//        } else {
//            System.out.println("Episodio no encontrado");
//        }
        Map<Integer , Double> evaluacionPorTemporada = episodios.stream()
                .filter(e -> e.getEvaluacion() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada, Collectors.averagingDouble(Episodio::getEvaluacion)));

        System.out.println(evaluacionPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getEvaluacion() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
        System.out.println("La Media de las evaluación: " + est.getAverage());
        System.out.println("Episodio mejor evaluado: " + est.getMax());
        System.out.println("Episodio peor evaluado: " + est.getMin());
    }
}
