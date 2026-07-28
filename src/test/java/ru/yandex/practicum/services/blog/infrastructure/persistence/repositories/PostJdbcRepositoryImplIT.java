package ru.yandex.practicum.services.blog.infrastructure.persistence.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.services.blog.core.application.interfaces.PostRepository;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTitleValueObject;
import ru.yandex.practicum.services.blog.infrastructure.persistence.BaseIntegrationTest;

public class PostJdbcRepositoryImplIT extends BaseIntegrationTest
{
    @Autowired
    private PostRepository postRepository;

    /**
     * <summary>
     * Проверяет, что поиск по несуществующему идентификатору возвращает null.
     * </summary>
     **/
    @Test
    void findByIdShouldReturnEmptyWhenPostDoesNotExist()
    {
        var postExists = postRepository.findPostById(-999L);

        Assertions.assertNull(postExists);
    }

    /**
     * <summary>
     * Проверяет успешную вставку новой публикации в базу данных и генерацию её идентификатора.
     * </summary>
     **/
    @Test
    void saveShouldInsertPostAndReturnGeneratedId()
    {
        var newPost = new PostEntityObject(
            new PostTitleValueObject("Тестовый пост для интеграции"),
            new PostTextValueObject("Текст интеграционного поста")
        );

        var generatedIdOpt = postRepository.createPost(newPost);

        Assertions.assertTrue(
            generatedIdOpt.isPresent(),
            "Репозиторий должен вернуть сгенерированный ID");

        var generatedId = generatedIdOpt.get();

        Assertions.assertNotNull(generatedId);

        var postExists = postRepository.findPostById(generatedId);

        Assertions.assertNotNull(
            postExists,
            "Репозиторий должен вернуть созданный пост");

        Assertions.assertEquals(
            "Тестовый пост для интеграции",
            postExists.getTitle().getValue());
    }

    /**
     * <summary>
     * Проверяет обновление текстовых данных и заголовка у существующей в базе данных публикации.
     * </summary>
     **/
    @Test
    void updatePostShouldModifyExistingPostData()
    {
        var newPost = new PostEntityObject(
            new PostTitleValueObject("Оригинальный заголовок"),
            new PostTextValueObject("Оригинальный текст")
        );

        var postIdOpt = postRepository.createPost(newPost).orElseThrow();

        var postExists = postRepository.findPostById(postIdOpt);

        postExists.changeTitle(new PostTitleValueObject("Обновленный заголовок"));

        postExists.changeText(new PostTextValueObject("Обновленный текст"));

        var operationStatus = postRepository.updatePostById(postExists);

        Assertions.assertTrue(operationStatus, "Репозиторий должен вернуть успешный статус обновления");

        postExists = postRepository.findPostById(postIdOpt);

        Assertions.assertEquals(
            "Обновленный заголовок",
            postExists.getTitle().getValue());

        Assertions.assertEquals(
            "Обновленный текст",
            postExists.getText().getValue());
    }

    /**
     * <summary>
     * Проверяет успешное удаление публикации из базы данных по её идентификатору.
     * </summary>
     **/
    @Test
    void deletePostShouldRemovePostFromDatabase()
    {
        var newPost = new PostEntityObject(
            new PostTitleValueObject("Пост для удаления"),
            new PostTextValueObject("Этот пост будет удален")
        );

        var postIdOpt = postRepository.createPost(newPost).orElseThrow();

        var operationStatus = postRepository.deletePostById(postIdOpt);

        Assertions.assertTrue(operationStatus, "Репозиторий должен вернуть успешный статус удаления");

        var postExists = postRepository.findPostById(postIdOpt);

        Assertions.assertNull(postExists, "Удаленный пост не должен находиться в базе данных");
    }
}