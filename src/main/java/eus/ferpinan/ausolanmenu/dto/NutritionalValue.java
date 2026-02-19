package eus.ferpinan.ausolanmenu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NutritionalValue(
        @JsonProperty("valorEnergeticoKcal") double energyKcal,
        @JsonProperty("hidratosCarbono") double carbohydrates,
        @JsonProperty("grasasSaturadas") double saturatedFats,
        @JsonProperty("grasas") double fats,
        @JsonProperty("proteinas") double proteins,
        @JsonProperty("azucares") double sugars,
        @JsonProperty("sal") double salt
) {}

