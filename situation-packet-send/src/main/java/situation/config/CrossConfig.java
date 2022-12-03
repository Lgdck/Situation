package situation.config;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lgd
 * @date 2021/12/20 20:12
 */
@Configuration
public class CrossConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET","POST")
                .allowedHeaders("*")
                .maxAge(10000); //10s
    }

    public static Socket socket;



}
