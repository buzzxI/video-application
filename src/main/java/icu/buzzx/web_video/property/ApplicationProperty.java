package icu.buzzx.web_video.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ConfigurationProperties("my.application")
public class ApplicationProperty {
    private String videoPath;
}
