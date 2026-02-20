package eus.ferpinan.ausolanmenu.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import eus.ferpinan.ausolanmenu.dto.Dish;
import eus.ferpinan.ausolanmenu.dto.Menu;
import eus.ferpinan.ausolanmenu.properties.AusolanProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class AusolanService {

    private final RestTemplate restTemplate;
    private final AusolanProperties ausolanProperties;
    private final PdfService pdfService;

    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Map<String, String> getMonthlyMenu() {
        LocalDate lastDayOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());

        URI uri = UriComponentsBuilder
                .fromUriString(ausolanProperties.baseUrl())
                .path("/Centros/GetMenu")
                .queryParam("centroPk",      ausolanProperties.centroPk())
                .queryParam("centroEnvioPk", ausolanProperties.centroEnvioPk())
                .queryParam("menuDate",      lastDayOfMonth)
                .queryParam("dietaPk",       ausolanProperties.dietaPk())
                .queryParam("idiomaPk",      ausolanProperties.idiomaPk())
                .build()
                .toUri();

        Menu body = Optional.ofNullable(
                restTemplate.exchange(uri, HttpMethod.GET, HttpEntity.EMPTY, Menu.class).getBody()
        ).orElseThrow(() -> new IllegalStateException("Menu response is null"));

        return generateMenuByDates(body);
    }

    private Map<String, String> generateMenuByDates(Menu menu) {
        LocalDate startDate = LocalDate.parse(menu.menuStartDate(), ISO_DATE_TIME);

        // Dishes are 1-indexed: rotation 1 = week 0 offset, dayOfWeek 1 = day 0 offset
        Map<String, String> menuByDates = new LinkedHashMap<>();

        menu.dishes().stream()
                .collect(Collectors.groupingBy(
                        Dish::rotation,
                        Collectors.groupingBy(Dish::dayOfWeek)
                ))
                .forEach((rotation, byDay) -> byDay.forEach((dayOfWeek, dishes) -> {
                    LocalDate date = startDate
                            .plusWeeks(rotation - 1L)
                            .plusDays(dayOfWeek - 1L);

                    String menuOfTheDay = dishes.stream()
                            .sorted(Comparator.comparingInt(Dish::dishOrder))
                            .map(Dish::name)
                            .collect(Collectors.joining("\n"));

                    menuByDates.put(date.format(ISO_DATE), menuOfTheDay);
                }));

        return menuByDates;
    }

    public byte[] getMonthlyMenuImage() {
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);
        LocalDate lastDay = now.with(TemporalAdjusters.lastDayOfMonth());

        List<Map<String, String>> body = buildRequestBody(now, firstDay, lastDay);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                ausolanProperties.baseUrl() + "/Report/GeneratePdfFigma",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                byte[].class
        );

        byte[] pdfBytes = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new IllegalStateException("PDF response body is null"));

        log.info("PDF obtained, size: {} bytes", pdfBytes.length);

        try {
            return pdfService.extractImageFromPdf(pdfBytes);
        } catch (IOException e) {
            throw new UncheckedIOException("Error extracting image from PDF", e);
        }
    }

    private List<Map<String, String>> buildRequestBody(LocalDate now,
                                                       LocalDate firstDay,
                                                       LocalDate lastDay) {

        return List.of(
                param("FechaIni", firstDay + "T23:00:00.000Z"),
                param("FechaFin", lastDay + "T23:00:00.000Z"),
                param("PkCentro", ausolanProperties.centroPk()),
                param("PkCentroEnvio", ausolanProperties.centroEnvioPk()),
                param("PkDieta", ausolanProperties.dietaPk()),
                param("PkIdioma", ausolanProperties.idiomaPk()),
                param("PkMenu", ausolanProperties.menuPk()),
                param("PkServicio", "[\"" + ausolanProperties.servicioPk() + "\"]"),
                param("NombreArchivo", "menu_" + now.getYear() + "_" + now.getMonthValue()),
                param("TipoVista", "Mensual")
        );
    }

    private Map<String, String> param(String nombre, String valor) {
        return Map.of(
                "nombre", nombre,
                "valorObjeto", valor
        );
    }
}
