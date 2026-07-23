package ru.yandex.practicum.services.blog.infrastructure.persistence.repositories;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.services.blog.core.application.interfaces.ICommentRepository;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.CommentEntityObject;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;
import ru.yandex.practicum.services.blog.infrastructure.persistence.mappers.CommentRowMapper;
import ru.yandex.practicum.services.blog.infrastructure.persistence.mappers.PostCommentRowMapper;
import ru.yandex.practicum.services.blog.infrastructure.persistence.projections.PostCommentProjection;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <summary>
 * Реализация репозитория комментариев с использованием Spring JDBC Template.
 * Отвечает за маппинг доменных сущностей в реляционную модель базы данных.
 * </summary>
 **/
public final class CommentJdbcRepository implements ICommentRepository
{
    // region Fields

    private final NamedParameterJdbcTemplate jdbcTemplate;

    // endregion

    // region Constructors

    public CommentJdbcRepository(final NamedParameterJdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // endregion

    // region Properties



    // endregion

    // region Methods

    /**
     * <summary>
     * Обогащает переданный список публикаций связанными с ними комментариями.
     * Выполняет пакетный запрос к базе данных для извлечения
     * всех комментариев, привязанных к указанным постам, и распределяет их по
     * соответствующим объектам доменных сущностей.
     * </summary>
     * <param name="posts">
     * Список доменных сущностей публикаций, которые необходимо обогатить комментариями.
     * Если список пуст или равен null, выполнение метода прерывается без обращения к БД.
     * </param>
     **/
    public void enrichPostsWithComments(final List<PostEntityObject> posts)
    {
        if (posts == null || posts.isEmpty())
        {
            return;
        }

        /*
         * Индексируем посты по их идентификатору.
         */
        final var postMap = posts.stream()
                .collect(Collectors.toMap(
                        PostEntityObject::getId,
                        Function.identity()
                ));

        final var sqlParams = new MapSqlParameterSource(
                "postIds",
                postMap.keySet()
        );

        final var sqlQuery = new StringBuilder()
                .append("SELECT [Id], ")
                .append("[PostId], ")
                .append("[Text], ")
                .append("[CreatedAt], ")
                .append("[UpdatedAt] ")
                .append("FROM [dbo].[PostComments] ")
                .append("WHERE [PostId] IN(:postIds) ")
                .append("ORDER BY [CreatedAt] DESC");

        final var projections = jdbcTemplate.query(
                sqlQuery.toString(),
                sqlParams,
                new PostCommentRowMapper()
        );

        final var commentsByPostId = projections.stream()
                .collect(Collectors.groupingBy(
                        PostCommentProjection::getPostId,
                        Collectors.mapping(PostCommentProjection::getComment, Collectors.toList())
                ));

        for (final var post : posts)
        {
            final var postComments = commentsByPostId.getOrDefault(post.getId(), List.of());

            post.initializeComments(postComments);
        }
    }

    /**
     * <summary>
     * Получает комментарии по идентификатору публикации.
     * </summary>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <return>
     * Комментарии публикации.
     * </return>
     **/
    public List<CommentEntityObject> findCommentsByPostId(final Long postId)
    {
        if (postId == null)
        {
            return List.of();
        }

        var selectSqlQuery = """
                    SELECT Id, PostId, Text, CreatedAt, UpdatedAt 
                      FROM dbo.PostComments 
                     WHERE PostId = :postId 
                     ORDER
                        BY CreatedAt DESC
                """;

        final var sqlParams = new MapSqlParameterSource()
                .addValue("postId", postId);

        return jdbcTemplate.query(
            selectSqlQuery,
            sqlParams,
            new CommentRowMapper());
    }

    /**
     * <summary>
     * Добавляет комментарий к публикации.
     * </summary>
     * <param name="comment">
     * Новый комментарий.
     * </param>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <return>
     * @return Сохраненный комментарий к публикации из базы данных сервиса.
     * </return>
     **/
    public Optional<CommentEntityObject> saveComment(final CommentEntityObject comment, final Long postId)
    {
        final var insertSqlQuery = """
            INSERT INTO dbo.PostComments (PostId, Text, CreatedAt, UpdatedAt)
            VALUES (:postId, :text, :createdAt, :updatedAt)
        """;

        final var sqlParams = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("text", comment.getText().getValue())
                .addValue("createdAt", comment.getCreatedAt())
                .addValue("updatedAt", comment.getUpdatedAt());

        final var keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();

        int rowsAffected = jdbcTemplate.update(
                insertSqlQuery,
                sqlParams,
                keyHolder,
                new String[]{"Id"});

        if (rowsAffected == 0)
        {
            return Optional.empty();
        }

        final Number key = keyHolder.getKey();

        if (key == null)
        {
            return Optional.empty();
        }

        final Long generatedId = key.longValue();

        return Optional.of(
                new CommentEntityObject(
                        generatedId,
                        comment.getText(),
                        comment.getCreatedAt(),
                        comment.getUpdatedAt())
        );
    }

    /**
     * <summary>
     * Обновляет комментарий публикации.
     * </summary>
     * <param name="comment">
     * Обновленный комментарий.
     * </param>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <return>
     * @return Обновленный комментарий к публикации из базы данных сервиса.
     * </return>
     **/
    public Optional<CommentEntityObject> updateComment(final CommentEntityObject comment, final Long postId)
    {
        final var updateSqlQuery = """
            UPDATE dbo.PostComments
            SET Text = :text, UpdatedAt = :updatedAt
            WHERE Id = :id AND PostId = :postId
        """;

        final var params = new MapSqlParameterSource()
                .addValue("id", comment.getId())
                .addValue("postId", postId)
                .addValue("text", comment.getText().getValue())
                .addValue("updatedAt", comment.getUpdatedAt());

        var rowsAffected = jdbcTemplate.update(updateSqlQuery, params);

        if (rowsAffected == 0)
        {
            return Optional.empty();
        }

        return Optional.of(
                new CommentEntityObject(
                        comment.getId(),
                        comment.getText(),
                        comment.getCreatedAt(),
                        comment.getUpdatedAt()));
    }

    /**
     * <summary>
     * Удаляет комментарий публикации.
     * </summary>
     * <param name="commentId">
     * Идентификатор комментария
     * </param>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <return>
     * @return Статус выполнения операции.
     * </return>
     **/
    @Override
    public Boolean deleteComment(final Long commentId, final Long postId)
    {
        final var deleteSqlQuery = """
            DELETE FROM dbo.PostComments
            WHERE Id = :id AND PostId = :postId
        """;

        final var params = new MapSqlParameterSource()
                .addValue("id", commentId)
                .addValue("postId", postId);

        var rowsAffected = jdbcTemplate.update(deleteSqlQuery, params);

        return rowsAffected > 0;
    }

    // endregion
}