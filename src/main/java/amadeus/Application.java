package amadeus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

@SpringBootApplication
@ComponentScan
public class Application extends SpringBootServletInitializer {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    /**
     * Remove CORS fencing object.
     * @return a none-CORS config
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            public void configurePathMatch(PathMatchConfigurer configurer) {
                UrlPathHelper helper = new UrlPathHelper();
                helper.setUrlDecode(false);
                configurer.setUrlPathHelper(helper);
            }

            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedHeaders("*")
                        .allowedMethods("*")
                        .allowedOrigins("*");
            }
        };
    }

    /**
     * Entrance of the service
     * @param args start argvs
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(Application.class).run(args);
        logger.info("SpringBoot Start Success");
    }

}

