package eus.ferpinan.ausolanmenu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object (DTO) representing the nutritional breakdown of a dish.
 * This record captures key dietary metrics used for labeling and health tracking.
 *
 * @param energyKcal    The energy content measured in kilocalories (kcal).
 * @param carbohydrates The total amount of carbohydrates, typically in grams.
 * @param saturatedFats The subset of total fats consisting of saturated fatty acids.
 * @param fats          The total fat content.
 * @param proteins      The total protein content.
 * @param sugars        The total sugar content, usually as a subset of carbohydrates.
 * @param salt          The total salt (sodium chloride) content.
 */
public record NutritionalValue(
        @JsonProperty("valorEnergeticoKcal") double energyKcal,
        @JsonProperty("hidratosCarbono") double carbohydrates,
        @JsonProperty("grasasSaturadas") double saturatedFats,
        @JsonProperty("grasas") double fats,
        @JsonProperty("proteinas") double proteins,
        @JsonProperty("azucares") double sugars,
        @JsonProperty("sal") double salt
) {}