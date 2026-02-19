package eus.ferpinan.ausolanmenu.service;

import eus.ferpinan.ausolanmenu.dto.MenuRoot;
import eus.ferpinan.ausolanmenu.dto.Plato;
import eus.ferpinan.ausolanmenu.properties.AusolanProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class AusolanService {

    private final RestTemplate restTemplate;
    private final AusolanProperties ausolanProperties;

    public Map<String, String> getMonthlyMenu() {
        String date = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).toString();

        String url = String.format(
                "https://apimenuo.ausolan.com/plapi/Centros/GetMenu?centroPk=%s&centroEnvioPk=%s&menuDate=%s&dietaPk=%s&idiomaPk=%s",
                ausolanProperties.getCentroPk(),
                ausolanProperties.getCentroEnvioPk(),
                date,
                ausolanProperties.getDietaPk(),
                ausolanProperties.getIdiomaPk()
        );
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<MenuRoot> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {}
        );
        return generarMenusPorFecha(response.getBody());
    }

    private Map<String, String> generarMenusPorFecha(MenuRoot menuRoot) {
        Map<String, String> resultado = new LinkedHashMap<>();

        LocalDate inicio = LocalDate.parse(menuRoot.getFechaInicioMenu().substring(0, 10)); // yyyy-MM-dd
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Agrupar platos por rotación y día de la semana
        Map<Integer, Map<Integer, List<Plato>>> agrupado = menuRoot.getPlatos().stream()
                .collect(Collectors.groupingBy(
                        Plato::getRotacion,
                        Collectors.groupingBy(Plato::getDiaSemana)
                ));

        // Iterar por cada rotación y día
        for (Map.Entry<Integer, Map<Integer, List<Plato>>> rotacionEntry : agrupado.entrySet()) {
            int rotacion = rotacionEntry.getKey();
            LocalDate inicioRotacion = inicio.plusWeeks(rotacion - 1);

            for (Map.Entry<Integer, List<Plato>> diaEntry : rotacionEntry.getValue().entrySet()) {
                int diaSemana = diaEntry.getKey();
                LocalDate fecha = inicioRotacion.plusDays(diaSemana - 1);

                // Ordenar por ordenPlato
                List<Plato> platosOrdenados = diaEntry.getValue().stream()
                        .sorted(Comparator.comparingInt(Plato::getOrdenPlato))
                        .collect(Collectors.toList());

                // Construir texto formateado
                StringBuilder sb = new StringBuilder();
                for (Plato p : platosOrdenados) {
                    sb.append(p.getNombre()).append("\n");
                }

                String fechaFormateada = fecha.format(formatter);
                resultado.put(fechaFormateada, sb.toString().trim());
            }
        }

        return resultado;
    }
}
