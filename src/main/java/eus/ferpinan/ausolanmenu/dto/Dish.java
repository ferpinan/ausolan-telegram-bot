package eus.ferpinan.ausolanmenu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Dish(
        @JsonProperty("nombre") String name,
        @JsonProperty("dieta") Diet diet,
        @JsonProperty("valorNutricionalPlato") NutritionalValue nutritionalValue,
        @JsonProperty("ordenPlato") int dishOrder,
        @JsonProperty("diaSemana") int dayOfWeek,
        @JsonProperty("rotacion") int rotation,
        @JsonProperty("articuloPk") String articlePk,
        @JsonProperty("familiaTecnicaPk") String technicalFamilyPk
) {}