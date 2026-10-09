package eus.ferpinan.ausolanmenu.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gasca")
public record GascaProperties(
        String baseUrl,
        String appSecretToken
) {}
