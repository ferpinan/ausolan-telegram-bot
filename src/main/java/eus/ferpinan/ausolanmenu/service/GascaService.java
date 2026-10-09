package eus.ferpinan.ausolanmenu.service;

import java.time.YearMonth;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eus.ferpinan.ausolanmenu.properties.GascaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GascaService {

    private static final String ORIGIN = "https://menus.grupogasca.com";
    private static final String DEVICE_UUID = "8185cd76-565f-4b00-8ed8-5c1ad23b4dd4";
    private static final String DEVICE_INFO_UUID =
            "f8136f4d5a351d6ad96df6b2027e954bb5f12015c9eb3cbe97162dfc14ff2641a5007d887ec9c7b22a04c2bdd804e3bbf910f5c1bd4ffb823bbd5d2359ba117f";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final GascaProperties gascaProperties;

    public byte[] downloadMonthlyPdf() {
        return downloadMonthlyPdf(YearMonth.now());
    }

    public byte[] downloadMonthlyPdf(YearMonth month) {
        if (gascaProperties.appSecretToken() == null || gascaProperties.appSecretToken().isBlank()) {
            throw new IllegalStateException("GASCA_APP_SECRET_TOKEN must be configured");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setOrigin(ORIGIN);
        headers.set(HttpHeaders.REFERER, ORIGIN + "/");

        ResponseEntity<String> response = restTemplate.exchange(
                gascaProperties.baseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(serializeRequest(month), headers),
                String.class
        );

        GascaFileResponse file = parseResponse(response.getBody());
        if (file == null || file.data() == null) {
            throw new IllegalStateException("Gasca PDF response is missing file data");
        }
        if (!"application/pdf".equalsIgnoreCase(file.mimeType())
                || !"pdf".equalsIgnoreCase(file.fileExtension())) {
            throw new IllegalStateException("Gasca response is not a PDF");
        }

        return Base64.getDecoder().decode(file.data());
    }

    private String serializeRequest(YearMonth month) {
        Map<String, Object> requestInfo = new LinkedHashMap<>();
        requestInfo.put("serializableName", "RequestInfo");
        requestInfo.put("requestSource", "FRONTEND");
        requestInfo.put("deviceUUID", DEVICE_UUID);
        requestInfo.put("deviceInfoUUID", DEVICE_INFO_UUID);
        requestInfo.put("ipAddress", "ip-address");
        requestInfo.put("appSecretToken", gascaProperties.appSecretToken());
        requestInfo.put("sessionToken", "");
        requestInfo.put("sessionLocale", "es-ES");
        requestInfo.put("projectCode", "Menus");
        requestInfo.put("appVersion", "130");
        requestInfo.put("time", Long.toString(System.currentTimeMillis()));
        requestInfo.put("requestInfoData", Map.of("serviceID", "02B"));

        Map<String, Object> request = new LinkedHashMap<>();
        request.put("serializableName", "RequestDataDTO");
        request.put("requestType", "FUNCTION");
        request.put("requestFunction", "DOWNLOAD_CENTER_MENU_CALENDAR");
        request.put("data", List.of(
                month.getYear() + "_" + (month.getMonthValue() - 1),
                "eu-ES", 201845, 2061, 2, 3, 1
        ));
        request.put("requestInfo", requestInfo);
        request.put("requestUUID", UUID.randomUUID().toString());

        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize Gasca PDF request", e);
        }
    }

    private GascaFileResponse parseResponse(String body) {
        if (body == null) {
            return null;
        }
        try {
            return objectMapper.readValue(body, GascaFileResponse.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not parse Gasca PDF response", e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GascaFileResponse(
            String fileName,
            String fileExtension,
            String mimeType,
            String data
    ) {}
}
