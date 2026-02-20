package eus.ferpinan.ausolanmenu.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public record Diet(
        @JsonProperty("dietaPk") String dietPk,
        @JsonProperty("nombre") String name
) {}
