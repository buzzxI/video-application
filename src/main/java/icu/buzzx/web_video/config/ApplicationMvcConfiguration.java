package icu.buzzx.web_video.config;

import icu.buzzx.web_video.property.ApplicationProperty;
import lombok.Data;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@EnableConfigurationProperties({ApplicationProperty.class})
@Configuration
public class ApplicationMvcConfiguration implements WebMvcConfigurer {
}
