package ru.yandex.practicum.services.blog.core.domain.entityobjects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.CommentTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTitleValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.TagNameObject;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * <summary>
 * Юнит-тесты для проверки доменной логики, инвариантов и управления состоянием агрегата PostEntityObject.
 * </summary>
 **/
public final class PostEntityObjectTest
{
    /**
     * <summary>
     * Проверяет успешное создание нового поста с начальным состоянием (0 лайков, пустые коллекции).
     * </summary>
     **/
    @Test
    void constructorShouldCreateNewInstanceWithDefaultState()
    {
        var title = new PostTitleValueObject("DDD в Java");

        var text = new PostTextValueObject("Разбираем основы проектирования сущностей.");

        var post = new PostEntityObject(title, text);

        Assertions.assertNotNull(post);

        Assertions.assertEquals(title, post.getTitle());

        Assertions.assertEquals(text, post.getText());

        Assertions.assertEquals(0L, post.getLikesCount());

        Assertions.assertTrue(post.getTags().isEmpty());

        Assertions.assertTrue(post.getComments().isEmpty());

        Assertions.assertEquals(0L, post.getCommentsCount());
    }

    /**
     * <summary>
     * Проверяет точное восстановление существующего поста со всеми метаданными из репозитория.
     * </summary>
     **/
    @Test
    void constructorWithAllFieldsShouldRestoreStateCorrectly()
    {
        var expectedId = 777L;

        var title = new PostTitleValueObject("Архитектура");

        var text = new PostTextValueObject("Чистый код и структура слоев.");

        var likesCount = 42L;

        var createdAt = OffsetDateTime.now().minusDays(5);

        var updatedAt = OffsetDateTime.now();

        var post = new PostEntityObject(expectedId, title, text, likesCount, createdAt, updatedAt);

        Assertions.assertEquals(expectedId, post.getId());

        Assertions.assertEquals(title, post.getTitle());

        Assertions.assertEquals(text, post.getText());

        Assertions.assertEquals(likesCount, post.getLikesCount());

        Assertions.assertEquals(createdAt, post.getCreatedAt());

        Assertions.assertEquals(updatedAt, post.getUpdatedAt());
    }

    /**
     * <summary>
     * Проверяет валидацию параметров на null в конструкторе нового поста.
     * </summary>
     **/
    @Test
    void constructorShouldThrowNullPointerExceptionWhenParametersAreNull()
    {
        var title = new PostTitleValueObject("Заголовок");

        var text = new PostTextValueObject("Текст");

        var titleException = Assertions.assertThrows(
                NullPointerException.class,
                () -> new PostEntityObject(null, text)
        );

        Assertions.assertEquals("Объект заголовка публикации не может быть null.", titleException.getMessage());

        var textException = Assertions.assertThrows(
                NullPointerException.class,
                () -> new PostEntityObject(title, null)
        );

        Assertions.assertEquals("объект сообщения публикации не может быть null.", textException.getMessage());
    }

    /**
     * <summary>
     * Проверяет успешное изменение заголовка поста при передаче нового значения.
     * </summary>
     **/
    @Test
    void changeTitleShouldUpdateValueWhenNewTitleIsDifferent()
    {
        var post = new PostEntityObject(new PostTitleValueObject("Старый заголовок"), new PostTextValueObject("Текст"));

        var newTitle = new PostTitleValueObject("Новый заголовок");

        post.changeTitle(newTitle);

        Assertions.assertEquals(newTitle, post.getTitle());
    }

    /**
     * <summary>
     * Проверяет успешное изменение содержимого текста поста при передаче нового значения.
     * </summary>
     **/
    @Test
    void changeTextShouldUpdateValueWhenNewTextIsDifferent()
    {
        var post = new PostEntityObject(new PostTitleValueObject("Заголовок"), new PostTextValueObject("Старый текст"));

        var newText = new PostTextValueObject("Новый текст публикации");

        post.changeText(newText);

        Assertions.assertEquals(newText, post.getText());
    }

    /**
     * <summary>
     * Проверяет добавление уникального тега в публикацию.
     * </summary>
     **/
    @Test
    void addTagShouldAddUniqueTagToPost()
    {
        var post = new PostEntityObject(new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"));

        var tag = new TagEntityObject(new TagNameObject("java"));

        post.addTag(tag);

        Assertions.assertEquals(1, post.getTags().size());

        Assertions.assertTrue(post.getTags().contains(tag));
    }

    /**
     * <summary>
     * Проверяет, что дублирующийся тег не добавляется в коллекцию повторно.
     * </summary>
     **/
    @Test
    void addTagShouldIgnoreDuplicateTags()
    {
        var post = new PostEntityObject(new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"));

        var tag = new TagEntityObject(1L, new TagNameObject("spring"));

        post.addTag(tag);

        post.addTag(tag);

        Assertions.assertEquals(1, post.getTags().size());
    }

    /**
     * <summary>
     * Проверяет удаление тега из публикации.
     * </summary>
     **/
    @Test
    void removeTagShouldDeleteExistingTagFromPost()
    {
        var post = new PostEntityObject(new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"));

        var tag = new TagEntityObject(new TagNameObject("docker"));

        post.addTag(tag);

        post.removeTag(tag);

        Assertions.assertTrue(post.getTags().isEmpty());
    }

    /**
     * <summary>
     * Проверяет чистую гидратацию (инициализацию) коллекции тегов из репозитория без вызова побочных эффектов.
     * </summary>
     **/
    @Test
    void initializeTagsShouldPopulateCollectionWithoutSideEffects()
    {
        var post = new PostEntityObject(new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"));

        var firstTag = new TagEntityObject(1L, new TagNameObject("postgres"));

        var secondTag = new TagEntityObject(2L, new TagNameObject("mssql"));

        post.initializeTags(List.of(firstTag, secondTag));

        Assertions.assertEquals(2, post.getTags().size());

        Assertions.assertTrue(post.getTags().containsAll(List.of(firstTag, secondTag)));
    }

    /**
     * <summary>
     * Проверяет успешное добавление комментария и корректность подсчета их количества.
     * </summary>
     **/
    @Test
    void addCommentShouldInsertCommentAndIncrementCount()
    {
        var post = new PostEntityObject(new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"));

        var comment = new CommentEntityObject(new CommentTextValueObject("Крутая фича!"));

        post.addComment(comment);

        Assertions.assertEquals(1L, post.getCommentsCount());

        Assertions.assertTrue(post.getComments().contains(comment));
    }

    /**
     * <summary>
     * Проверяет инициализацию комментариев списком из базы данных при сборке агрегата.
     * </summary>
     **/
    @Test
    void initializeCommentsShouldOverwriteInternalCollection()
    {
        var post = new PostEntityObject(new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"));

        var comment = new CommentEntityObject(10L, new CommentTextValueObject("Текст"), OffsetDateTime.now(), OffsetDateTime.now());

        post.initializeComments(List.of(comment));

        Assertions.assertEquals(1L, post.getCommentsCount());

        Assertions.assertTrue(post.getComments().contains(comment));
    }

    /**
     * <summary>
     * Проверяет работу механизма лайков, включая инкремент и защиту от переполнения Long.MAX_VALUE.
     * </summary>
     **/
    @Test
    void likeShouldIncrementCountAndRespectMaxBoundary()
    {
        var post = new PostEntityObject(new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"));

        post.like();

        Assertions.assertEquals(1L, post.getLikesCount());

        var maxLikesPost = new PostEntityObject(1L, new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"), Long.MAX_VALUE, OffsetDateTime.now(), OffsetDateTime.now());

        maxLikesPost.like();

        Assertions.assertEquals(Long.MAX_VALUE, maxLikesPost.getLikesCount());
    }

    /**
     * <summary>
     * Проверяет работу механизма отзыва лайка, включая декремент и защиту от ухода в отрицательный диапазон.
     * </summary>
     **/
    @Test
    void unlikeShouldDecrementCountAndRespectZeroBoundary()
    {
        var post = new PostEntityObject(new PostTitleValueObject("Заголовок"), new PostTextValueObject("Текст"));

        post.unlike();

        Assertions.assertEquals(0L, post.getLikesCount());

        post.like();

        post.like();

        post.unlike();

        Assertions.assertEquals(1L, post.getLikesCount());
    }
}