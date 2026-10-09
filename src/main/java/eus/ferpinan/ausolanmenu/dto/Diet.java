package eus.ferpinan.ausolanmenu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object (DTO) representing a specific diet classification.
 * This record is used to categorize dishes based on dietary requirements
 * or meal types (e.g., "Normal", "Vegetarian", "Gluten-Free").
 *
 * @param dietPk The unique identifier or primary key for the diet type (e.g., "dietaPk").
 * @param name   The descriptive name of the diet (e.g., "nombre").
 */
public record Diet(
        @JsonProperty("dietaPk") String dietPk,
        @JsonProperty("nombre") String name
) {}