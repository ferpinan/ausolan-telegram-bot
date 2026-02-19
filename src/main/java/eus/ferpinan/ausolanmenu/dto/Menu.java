package eus.ferpinan.ausolanmenu.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Menu(
        @JsonProperty("menuPk") String menuPk,
        @JsonProperty("fechaInicioMenu") String menuStartDate,
        @JsonProperty("fechaFinMenu") String menuEndDate,
        @JsonProperty("rotacionActiva") int activeRotation,
        @JsonProperty("maxRotacion") int maxRotation,
        @JsonProperty("platos") List<Dish> dishes
) {}