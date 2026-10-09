package eus.ferpinan.ausolanmenu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object (DTO) representing an individual food item or dish within a menu.
 * This record maps specific dish details and nutritional metadata from the Ausolan API.
 *
 * @param name               The descriptive name of the dish (e.g., "nombre").
 * @param diet               The {@link Diet} classification associated with this dish.
 * @param nutritionalValue   The {@link NutritionalValue} details for a single serving of the dish.
 * @param dishOrder          The sequence or order in which the dish appears in a meal (e.g., "ordenPlato").
 * @param dayOfWeek          The numeric representation of the day of the week (e.g., "diaSemana").
 * @param rotation           The rotation cycle number this dish belongs to.
 * @param articlePk          The unique primary key or identifier for the specific food article.
 * @param technicalFamilyPk  The unique identifier for the technical category or family the dish belongs to.
 */
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