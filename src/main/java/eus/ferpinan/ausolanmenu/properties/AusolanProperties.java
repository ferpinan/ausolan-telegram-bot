package eus.ferpinan.ausolanmenu.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "ausolan")
public class AusolanProperties {

    private String centroPk;
    private String centroEnvioPk;
    private String dietaPk;
    private String idiomaPk;
}
