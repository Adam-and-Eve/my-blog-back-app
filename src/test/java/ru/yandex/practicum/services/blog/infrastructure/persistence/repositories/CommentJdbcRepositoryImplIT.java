package ru.yandex.practicum.services.blog.infrastructure.persistence.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import ru.yandex.practicum.services.blog.core.application.interfaces.CommentRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.PostRepository;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.CommentEntityObject;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.CommentTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTitleValueObject;
import ru.yandex.practicum.services.blog.infrastructure.persistence.BaseIntegrationTest;

import java.time.OffsetDateTime;
import java.util.ArrayList;

/**
 * <summary>
 * Интеграционные тесты для проверки корректности работы репозитория комментариев CommentJdbcRepository.
 * Проверяют CRUD-операции над комментариями, а также логику пакетного обогащения постов.
 * </summary>
 **/
public final class CommentJdbcRepositoryImplIT extends BaseIntegrationTest
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
    @Test
    void saveCommentShouldInsertRecordAndReturnGeneratedId()
    {
        var postId = createTestPost("Пост для комментария");

        var comment = new CommentEntityObject(
                new CommentTextValueObject("Текст нового комментария"));

        var savedCommentOptional = commentRepository.saveComment(comment, postId);

        Assertions.assertTrue(
            savedCommentOptional.isPresent(),
            "Комментарий должен успешно сохраниться");

        var savedComment = savedCommentOptional.get();

        Assertions.assertNotNull(
            savedComment.getId(),
            "База данных должна сгенерировать Id для комментария");

        Assertions.assertEquals(
            comment.getText().getValue(),
            savedComment.getText().getValue());
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

        var firstComment = new CommentEntityObject(
                new CommentTextValueObject("Первый комментарий"));

        var secondComment = new CommentEntityObject(
                new CommentTextValueObject("Второй комментарий"));

        /*
         * Сдвигаем CreatedAt у второго комментария на час вперед через ReflectionTestUtils.
         * Это гарантирует, что даже после округления в БД второй комментарий останется более свежим.
         */
        var futureTime = OffsetDateTime.now().plusHours(1);

        ReflectionTestUtils.setField(secondComment, "createdAt", futureTime);

        commentRepository.saveComment(firstComment, postId);

        commentRepository.saveComment(secondComment, postId);

        var comments = commentRepository.findCommentsByPostId(postId);

        Assertions.assertEquals(
            2,
            comments.size(),
            "Должно быть возвращено ровно 2 комментария");

        Assertions.assertEquals(
            "Второй комментарий",
            comments.getFirst().getText().getValue(),
            "Первым должен идти более свежий комментарий (ORDER BY CreatedAt DESC)");
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

        var comment = new CommentEntityObject(
                new CommentTextValueObject("Комментарий под удаление"));

        var savedComment = commentRepository.saveComment(comment, postId).orElseThrow();

        var isDeleted = commentRepository.deleteComment(savedComment.getId(), postId);

        Assertions.assertTrue(
            isDeleted,
            "Метод удаления должен вернуть true");

        var dbComments = commentRepository.findCommentsByPostId(postId);

        Assertions.assertTrue(
            dbComments.isEmpty(),
            "После удаления список комментариев в БД должен быть пуст");
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

        var commentForFirstPost = new CommentEntityObject(
                new CommentTextValueObject("Коммент к первому"));

        var commentForSecondPost = new CommentEntityObject(
                new CommentTextValueObject("Коммент ко второму"));

        commentRepository.saveComment(commentForFirstPost, firstPostId).orElseThrow();

        commentRepository.saveComment(commentForSecondPost, secondPostId).orElseThrow();

        var firstPostEntity = postRepository.findPostById(firstPostId);

        var secondPostEntity = postRepository.findPostById(secondPostId);

        var postsList = new ArrayList<PostEntityObject>();

        postsList.add(firstPostEntity);

        postsList.add(secondPostEntity);

        commentRepository.enrichPostsWithComments(postsList);

        Assertions.assertEquals(
            1,
            firstPostEntity.getComments().size(),
            "Первый пост должен быть обогащен одним комментарием");

        var firstComment = firstPostEntity.getComments().iterator().next();

        Assertions.assertEquals(
            "Коммент к первому",
            firstComment.getText().getValue());

        Assertions.assertEquals(
            1,
            secondPostEntity.getComments().size(),
            "Второй пост должен быть обогащен одним комментарием");

        var secondComment = secondPostEntity.getComments().iterator().next();

        Assertions.assertEquals(
            "Коммент ко второму",
            secondComment.getText().getValue());
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