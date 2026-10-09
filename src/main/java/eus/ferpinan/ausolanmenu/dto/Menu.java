package eus.ferpinan.ausolanmenu.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing a dietary menu structure.
 * This record maps the JSON response from the Ausolan service to a structured Java object.
 *
 * <p>Unknown JSON properties are ignored during deserialization to maintain
 * compatibility with API updates.</p>
 *
 * @param menuPk           The unique identifier (primary key) for the menu.
 * @param menuStartDate    The start date of the menu's validity period (e.g., "fechaInicioMenu").
 * @param menuEndDate      The end date of the menu's validity period (e.g., "fechaFinMenu").
 * @param activeRotation   The index or identifier of the currently active rotation.
 * @param maxRotation      The total number of rotations available for this menu cycle.
 * @param dishes           A list of {@link Dish} objects included in this menu.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Menu(
        @JsonProperty("menuPk") String menuPk,
        @JsonProperty("fechaInicioMenu") String menuStartDate,
        @JsonProperty("fechaFinMenu") String menuEndDate,
        @JsonProperty("rotacionActiva") int activeRotation,
        @JsonProperty("maxRotation") int maxRotation,
        @JsonProperty("platos") List<Dish> dishes
) {}