package ru.yandex.practicum.services.blog.infrastructure.persistence.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.services.blog.core.application.interfaces.ImageRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.PostRepository;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTitleValueObject;
import ru.yandex.practicum.services.blog.infrastructure.persistence.BaseIntegrationTest;

/**
 * <summary>
 * Интеграционные тесты для проверки корректности работы репозитория изображений ImageJdbcRepository.
 * Проверяют механизмы сохранения, обновления (upsert) и чтения бинарных данных изображений публикаций.
 * </summary>
 **/
public final class ImageJdbcRepositoryImplIT extends BaseIntegrationTest
{
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * <summary>
     * Проверяет, что при отсутствии записи в таблице [PostImages],
     * метод saveOrUpdatePostImage выполняет успешную вставку (INSERT) нового изображения.
     * </summary>
     **/
    @Test
    void saveOrUpdatePostImageShouldInsertNewRecordWhenImageDoesNotExist()
    {
        var post = new PostEntityObject(
                new PostTitleValueObject("Пост для нового изображения"),
                new PostTextValueObject("Текст поста")
        );

        var postId = postRepository.createPost(post).orElseThrow();

        var imageBytes = new byte[] { 1, 2, 3, 4, 5 };

        var result = imageRepository.saveOrUpdatePostImage(postId, imageBytes);

        Assertions.assertTrue(
            result,
            "Новое изображение должно успешно сохраниться");

        var checkSqlQuery = "SELECT COUNT(*) FROM [dbo].[PostImages] WHERE [PostId] = :postId";

        var count = jdbcTemplate.queryForObject(
                checkSqlQuery,
                new MapSqlParameterSource("postId", postId),
                Integer.class);

        Assertions.assertEquals(
            1,
            count,
            "В базе данных должна появиться ровно одна запись");
    }

    /**
     * <summary>
     * Проверяет, что если запись для публикации уже существует в таблице [PostImages],
     * метод saveOrUpdatePostImage обновляет (UPDATE) существующий контент без дублирования.
     * </summary>
     **/
    @Test
    void saveOrUpdatePostImageShouldUpdateExistingRecordWhenImageAlreadyExists()
    {
        var post = new PostEntityObject(
                new PostTitleValueObject("Пост для обновления изображения"),
                new PostTextValueObject("Текст поста")
        );

        var postId = postRepository.createPost(post).orElseThrow();

        var initialBytes = new byte[] { 10, 20, 30 };

        imageRepository.saveOrUpdatePostImage(postId, initialBytes);

        var updatedBytes = new byte[] { 40, 50, 60, 70 };

        var result = imageRepository.saveOrUpdatePostImage(postId, updatedBytes);

        Assertions.assertTrue(
            result,
            "Обновление существующего изображения должно пройти успешно");

        var dbBytes = imageRepository.findPostImageBytesByPostId(postId);

        Assertions.assertArrayEquals(
                updatedBytes,
                dbBytes,
                "Контент изображения в базе должен обновиться");
    }

    /**
     * <summary>
     * Проверяет, что метод findPostImageBytesByPostId возвращает корректный массив байт
     * для существующего изображения, и null — если изображения для публикации нет.
     * </summary>
     **/
    @Test
    void findPostImageBytesByPostIdShouldReturnCorrectBytesOrNull()
    {
        var post = new PostEntityObject(
                new PostTitleValueObject("Пост для проверки чтения"),
                new PostTextValueObject("Текст поста")
        );

        var postId = postRepository.createPost(post).orElseThrow();

        var nonExistentImage = imageRepository.findPostImageBytesByPostId(postId);

        Assertions.assertNull(
                nonExistentImage,
                "Если изображение не загружалось, метод должен вернуть null");

        var expectedBytes = new byte[] { 12, 34, 56, 78 };

        imageRepository.saveOrUpdatePostImage(postId, expectedBytes);

        var actualBytes = imageRepository.findPostImageBytesByPostId(postId);

        Assertions.assertNotNull(
            actualBytes,
            "Метод должен найти сохраненное изображение");

        Assertions.assertArrayEquals(
                expectedBytes,
                actualBytes,
                "Считанный массив байт должен полностью совпадать с сохраненным");
    }
}