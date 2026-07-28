package ru.yandex.practicum.services.blog.core.application.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.services.blog.core.application.interfaces.*;
import ru.yandex.practicum.services.blog.core.application.services.PostCommentServiceImpl;
import ru.yandex.practicum.services.blog.core.application.services.PostImageServiceImpl;
import ru.yandex.practicum.services.blog.core.application.services.PostServiceImpl;

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
    public PostService postService(
            final PostRepository postRepository,
            final TagRepository tagRepository,
            final CommentRepository commentRepository)
    {
        return new PostServiceImpl(postRepository, tagRepository,  commentRepository);
    }

    /**
     * <summary>
     * Создает и регистрирует компонент сервиса комментариев публикаций.
     * </summary>
     * @return Реализация сервиса комментариев постов.
     **/
    @Bean
    public PostCommentService postCommentService(
            final PostRepository postRepository,
            final CommentRepository commentRepository)
    {
        return new PostCommentServiceImpl(postRepository, commentRepository);
    }

    /**
     * <summary>
     * Создает и регистрирует компонент сервиса изображений публикаций.
     * </summary>
     * @return Реализация сервиса изображений публикаци.
     **/
    @Bean
    public PostImageService postImageService(
            final PostRepository postRepository,
            final ImageRepository imageRepository)
    {
        return new PostImageServiceImpl(postRepository, imageRepository);
    }

    // endregion

    // region Methods



    // endregion
}