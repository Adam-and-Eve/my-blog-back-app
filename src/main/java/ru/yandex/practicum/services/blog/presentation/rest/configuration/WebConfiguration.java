package ru.yandex.practicum.services.blog.presentation.rest.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <summary>
 * Класс конфигурации веб-слоя приложения.
 * Обеспечивает настройку инфраструктуры Spring MVC, включая сканирование контроллеров,
 * установку глобальных префиксов API и управление политиками безопасности CORS.
 * Реализует интерфейс WebMvcConfigurer для тонкой настройки поведения веб-контекста.
 * </summary>
 **/
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"ru.yandex.practicum.services.blog.presentation.rest.controllers"})
public class WebConfiguration implements WebMvcConfigurer
{
    // region Fields



    // endregion

    // region Constructors



    // endregion

    // region Properties



    // endregion

    // region Methods

    /**
     * <summary>
     * Настраивает правила сопоставления путей (Path Matching), устанавливая глобальный
     * префикс "/api" для всех контроллеров, помеченных аннотацией @RestController.
     * Это позволяет централизованно управлять базовым URL-адресом API, исключая
     * необходимость дублирования префикса в каждом контроллере.
     * </summary>
     **/
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer)
    {
        configurer.addPathPrefix("/api",
                c -> c.isAnnotationPresent(RestController.class));
    }

    /**
     * <summary>
     * Настраивает политику Cross-Origin Resource Sharing (CORS) для приложения.
     * Разрешает перекрестные запросы для всех эндпоинтов ("/**"), позволяя
     * использовать любые HTTP-методы, заголовки и домены. Настройка также
     * активирует поддержку передачи учетных данных (cookies, авторизационные заголовки).
     * </summary>
     **/
    @Override
    public void addCorsMappings(CorsRegistry registry)
    {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    // endregion
}