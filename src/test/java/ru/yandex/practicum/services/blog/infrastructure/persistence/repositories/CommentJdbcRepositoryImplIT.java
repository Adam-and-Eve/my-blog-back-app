package ru.yandex.practicum.services.blog.infrastructure.persistence.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import ru.yandex.practicum.services.blog.MyBlogBackAppApplicationTests;
import ru.yandex.practicum.services.blog.core.application.interfaces.CommentRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.PostRepository;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.CommentEntityObject;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.CommentTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTitleValueObject;

import java.time.OffsetDateTime;
import java.util.ArrayList;

/**
 * Интеграционные тесты для проверки корректности работы репозитория комментариев.
 * Запускаются в кэшируемом контексте Spring Boot на базе Testcontainers.
 **/
class CommentJdbcRepositoryImplIT extends MyBlogBackAppApplicationTests
{
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    /**
     * <summary>
     * Проверяет успешное создание нового комментария к публикации и генерацию его идентификатора.
     * </summary>
     **/
    void saveCommentShouldInsertRecordAndReturnGeneratedId()
    {
        var postId = createTestPost("Пост для комментария");

        var comment = new CommentEntityObject(new CommentTextValueObject("Текст нового комментария"));

        var savedCommentOptional = commentRepository.saveComment(comment, postId);

        Assertions.assertTrue(savedCommentOptional.isPresent(), "Комментарий должен успешно сохраниться");

        var savedComment = savedCommentOptional.get();

        Assertions.assertNotNull(savedComment.getId(), "База данных должна сгенерировать Id для комментария");

        Assertions.assertEquals(comment.getText().getValue(), savedComment.getText().getValue());
    }

    /**
     * <summary>
     * Проверяет получение списка всех комментариев, привязанных к конкретной публикации, с сортировкой по убыванию даты.
     * </summary>
     **/
    @Test
    void findCommentsByPostIdShouldReturnCommentsOrderedByCreatedAtDesc()
    {
        var postId = createTestPost("Пост с несколькими комментариями");

        var firstComment = new CommentEntityObject(new CommentTextValueObject("Первый комментарий"));

        var secondComment = new CommentEntityObject(new CommentTextValueObject("Второй комментарий"));

        var futureTime = OffsetDateTime.now().plusHours(1);

        ReflectionTestUtils.setField(secondComment, "createdAt", futureTime);

        commentRepository.saveComment(firstComment, postId);

        commentRepository.saveComment(secondComment, postId);

        var comments = commentRepository.findCommentsByPostId(postId);

        Assertions.assertEquals(2, comments.size(), "Должно быть возвращено ровно 2 комментария");

        Assertions.assertEquals("Второй комментарий", comments.getFirst().getText().getValue());
    }

    /**
     * <summary>
     * Проверяет удаление комментария по его идентификатору и идентификатору публикации.
     * </summary>
     **/
    @Test
    void deleteCommentShouldRemoveRecordFromDatabase()
    {
        var postId = createTestPost("Пост для удаления комментария");

        var comment = new CommentEntityObject(new CommentTextValueObject("Комментарий под удаление"));

        var savedComment = commentRepository.saveComment(comment, postId).orElseThrow();

        var isDeleted = commentRepository.deleteComment(savedComment.getId(), postId);

        Assertions.assertTrue(isDeleted, "Метод удаления должен вернуть true");

        var dbComments = commentRepository.findCommentsByPostId(postId);

        Assertions.assertTrue(dbComments.isEmpty(), "После удаления список комментариев в БД должен быть пуст");
    }

    /**
     * <summary>
     * Проверяет пакетный метод enrichPostsWithComments: извлекает пачку комментариев
     * для списка постов за один раз и распределяет их по соответствующим сущностям постов.
     * </summary>
     **/
    @Test
    void enrichPostsWithCommentsShouldPopulateCommentsIntoPostEntities()
    {
        var firstPostId = createTestPost("Первый пост пачки");

        var secondPostId = createTestPost("Второй пост пачки");

        var commentForFirstPost = new CommentEntityObject(new CommentTextValueObject("Коммент к первому"));

        var commentForSecondPost = new CommentEntityObject(new CommentTextValueObject("Коммент ко второму"));

        commentRepository.saveComment(commentForFirstPost, firstPostId).orElseThrow();

        commentRepository.saveComment(commentForSecondPost, secondPostId).orElseThrow();

        var firstPostEntity = postRepository.findPostById(firstPostId);

        var secondPostEntity = postRepository.findPostById(secondPostId);

        var postsList = new ArrayList<PostEntityObject>();

        postsList.add(firstPostEntity);

        postsList.add(secondPostEntity);

        commentRepository.enrichPostsWithComments(postsList);

        Assertions.assertEquals(1, firstPostEntity.getComments().size());

        Assertions.assertEquals("Коммент к первому", firstPostEntity.getComments().iterator().next().getText().getValue());

        Assertions.assertEquals(1, secondPostEntity.getComments().size());

        Assertions.assertEquals("Коммент ко второму", secondPostEntity.getComments().iterator().next().getText().getValue());
    }

    /**
     * <summary>
     * Вспомогательный метод для быстрой подготовки родительской записи публикации в БД.
     * </summary>
     **/
    private Long createTestPost(final String title)
    {
        var post = new PostEntityObject(
                new PostTitleValueObject(title),
                new PostTextValueObject("Технический текст для связи внешнего ключа")
        );
        return postRepository.createPost(post).orElseThrow();
    }
}