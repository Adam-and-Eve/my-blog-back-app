package ru.yandex.practicum.services.blog.infrastructure.persistence.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.services.blog.MyBlogBackAppApplicationTests;
import ru.yandex.practicum.services.blog.core.application.interfaces.PostRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.TagRepository;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTitleValueObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Интеграционные тесты для проверки корректности работы репозитория публикаций TagJdbcRepository.
 * Выполняются в общем изолированном контексте Spring Boot с использованием Testcontainers.
 **/
class TagJdbcRepositoryImplIT extends MyBlogBackAppApplicationTests
{
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * <summary>
     * Проверяет, что метод getOrCreateTags создает новые теги и возвращает их ID,
     * а при повторном вызове переиспользует существующие записи без дублирования.
     * </summary>
     **/
    @Test
    void getOrCreateTagsShouldCreateNewOrReturnExistingIds()
    {
        var tagNames = List.of(" Java ", "Spring "); // Проверим также обрезку пробелов

        var firstCallIds = tagRepository.getOrCreateTags(tagNames);

        Assertions.assertEquals(2, firstCallIds.size());

        Assertions.assertNotNull(firstCallIds.get(0));

        Assertions.assertNotNull(firstCallIds.get(1));

        var secondCallIds = tagRepository.getOrCreateTags(List.of("java", "spring"));

        Assertions.assertEquals(
            firstCallIds,
            secondCallIds,
            "Повторный вызов должен вернуть те же ID");
    }

    /**
     * <summary>
     * Проверяет корректность перезаписи связей между публикацией и тегами, включая пакетную вставку.
     * </summary>
     **/
    @Test
    void updatePostTagsShouldModifyConnectionsInDatabase()
    {
        var post = new PostEntityObject(
            new PostTitleValueObject("Заголовок"),
            new PostTextValueObject("Текст")
        );

        var postId = postRepository.createPost(post).orElseThrow();

        var tagIds = tagRepository.getOrCreateTags(List.of("java", "backend"));

        tagRepository.updatePostTags(postId, tagIds);

        var checkSqlQuery = "SELECT COUNT(*) FROM [dbo].[PostTags] WHERE [PostId] = :postId";

        var count = jdbcTemplate.queryForObject(
            checkSqlQuery,
            new MapSqlParameterSource("postId", postId),
            Integer.class);

        Assertions.assertEquals(2, count);
    }

    /**
     * <summary>
     * Проверяет, что метод обогащения заполняет внутреннюю коллекцию тегов у объектов публикаций.
     * </summary>
     **/
    @Test
    void enrichPostsWithTagsShouldPopulateTagsInPostObjects()
    {
        var post = new PostEntityObject(
            new PostTitleValueObject("Пост с тегами"),
            new PostTextValueObject("Текст"));

        var postId = postRepository.createPost(post).orElseThrow();

        var tagIds = tagRepository.getOrCreateTags(List.of("testing"));

        tagRepository.updatePostTags(postId, tagIds);

        var freshPost = postRepository.findPostById(postId);

        var postsToEnrich = new ArrayList<PostEntityObject>();

        postsToEnrich.add(freshPost);

        tagRepository.enrichPostsWithTags(postsToEnrich);

        Assertions.assertFalse(
            freshPost.getTags().isEmpty(),
            "Коллекция тегов у поста должна быть заполнена");

        Assertions.assertEquals(
            "testing",
            freshPost.getTags().iterator().next().getName().getValue());
    }

    /**
     * <summary>
     * Проверяет успешное удаление из таблицы тегов, которые не связаны ни с одной публикацией.
     * </summary>
     **/
    @Test
    void clearOrphanedTagsShouldRemoveUnusedTags()
    {
        var tagIds = tagRepository.getOrCreateTags(List.of("orphan-tag"));

        tagRepository.clearOrphanedTags();

        var checkSqlQuery = "SELECT COUNT(*) FROM [dbo].[Tags] WHERE [Id] IN (:ids)";

        var count = jdbcTemplate.queryForObject(
            checkSqlQuery,
            new MapSqlParameterSource("ids", tagIds),
            Integer.class);

        Assertions.assertEquals(
            0,
            count,
            "Тег без связей должен быть удален из базы данных");
    }
}