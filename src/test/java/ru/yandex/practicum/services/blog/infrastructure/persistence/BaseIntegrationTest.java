package ru.yandex.practicum.services.blog.infrastructure.persistence;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.services.blog.infrastructure.persistence.configuration.PersistenceConfiguration;
import ru.yandex.practicum.services.blog.infrastructure.persistence.configuration.TestPersistenceConfiguration;

/**
 * <summary>
 * Базовый класс для всех интеграционных тестов инфраструктурного уровня.
 * Поднимает контекст Spring на основе реального PersistenceConfiguration,
 * автоматически подтягивая настройки из classpath (main/test/resources/application.properties).
 * Обеспечивает кэширование контекста между всеми тестами-наследниками и автоматический откат
 * транзакций после каждого тестового метода.
 * </summary>
 **/
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestPersistenceConfiguration.class})
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest
{
    // region Methods



    // endregion
}