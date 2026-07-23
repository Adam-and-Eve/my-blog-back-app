package ru.yandex.practicum.services.blog.core.application.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.services.blog.core.application.interfaces.IPostRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.IPostService;
import ru.yandex.practicum.services.blog.core.application.interfaces.ITagRepository;
import ru.yandex.practicum.services.blog.core.application.services.PostService;

/**
 * <summary>
 * Класс конфигурации уровня приложения (Application Layer).
 * Отвечает за настройку компонентов бизнес-логики, включая сервисы и их зависимости.
 * Служит связующим звеном между доменными моделями и портами инфраструктуры,
 * обеспечивая выполнение функциональных требований приложения. Данный слой
 * остается независимым от внешних технологий, таких как базы данных или веб-фреймворки.
 * </summary>
 **/
@Configuration
public class ApplicationConfiguration
{
    // region Fields



    // endregion

    // region Constructors



    // endregion

    // region Properties

    /**
     * <summary>
     * Создает и регистрирует компонент сервиса публикаций.
     * </summary>
     * @return Реализация сервиса постов.
     **/
    @Bean
    public IPostService postService(
            final IPostRepository postRepository,
            final ITagRepository tagRepository)
    {
        return new PostService(postRepository, tagRepository);
    }

    // endregion

    // region Methods



    // endregion
}