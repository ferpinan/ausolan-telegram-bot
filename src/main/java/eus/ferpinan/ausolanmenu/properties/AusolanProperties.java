package eus.ferpinan.ausolanmenu.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ausolan")
public record AusolanProperties(
        String baseUrl,
        String centroPk,
        String centroEnvioPk,
        String dietaPk,
        String idiomaPk,
        String menuPk,
        String servicioPk
) {}
