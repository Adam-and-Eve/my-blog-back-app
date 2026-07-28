package ru.yandex.practicum.services.blog.presentation.rest.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ru.yandex.practicum.services.blog.core.application.configuration.ApplicationConfiguration;
import ru.yandex.practicum.services.blog.infrastructure.persistence.configuration.PersistenceConfiguration;

/**
 * <summary>
 * Главный конфигурационный класс REST-приложения.
 * Выступает в роли агрегатора, объединяя конфигурации всех уровней системы:
 * бизнес-логику (Application), доступ к данным (Persistence) и api-интерфейс (Rest).
 * Является входной точкой для формирования целостного контекста Spring, обеспечивая
 * корректное взаимодействие между слоями Core, Infrastructure и Presentation.
 * </summary>
 **/
@Configuration
@Import({
        ApplicationConfiguration.class,
        PersistenceConfiguration.class,
        WebConfiguration.class,
        ExceptionConfiguration.class
})
public class RestConfiguration
{
    // region Fields



    // endregion

    // region Constructors



    // endregion

    // region Properties



    // endregion

    // region Methods



    // endregion
}