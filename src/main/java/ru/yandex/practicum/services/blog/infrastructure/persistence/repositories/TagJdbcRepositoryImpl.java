package ru.yandex.practicum.services.blog.infrastructure.persistence.repositories;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.services.blog.core.application.interfaces.TagRepository;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;
import ru.yandex.practicum.services.blog.infrastructure.persistence.mappers.PostTagRowMapper;
import ru.yandex.practicum.services.blog.infrastructure.persistence.projections.PostTagProjection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <summary>
 * Реализация репозитория тегов с использованием Spring JDBC Template.
 * Отвечает за маппинг доменных сущностей в реляционную модель базы данных.
 * </summary>
 **/
public final class TagJdbcRepositoryImpl implements TagRepository
{
    // region Fields

    private final NamedParameterJdbcTemplate jdbcTemplate;

    // endregion

    // region Constructors

    public TagJdbcRepositoryImpl(final NamedParameterJdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // endregion

    // region Properties



    // endregion

    // region Methods

    /**
     * <summary>
     * Ищет идентификатор тега по его имени.
     * </summary>
     **/
    private Long findTagIdByName(final String name)
    {
        var selectSqlQuery = "SELECT [Id] FROM [dbo].[Tags] WHERE [Name] = :name;";

        var ids = jdbcTemplate.query(
                selectSqlQuery,
                new MapSqlParameterSource("name", name),
                (rs, rowNum) -> rs.getLong("Id")
        );

        return ids.isEmpty() ? null : ids.getFirst();
    }

    /**
     * <summary>
     * Обогащает переданный список публикаций связанными с ними тегами.
     * Выполняет пакетный запрос к базе данных для извлечения
     * всех тегов, привязанных к указанным постам, и распределяет их по
     * соответствующим объектам доменных сущностей.
     * </summary>
     * <param name="posts">
     * Список доменных сущностей публикаций, которые необходимо обогатить тегами.
     * Если список пуст или равен null, выполнение метода прерывается без обращения к БД.
     * </param>
     **/
    public void enrichPostsWithTags(final List<PostEntityObject> posts)
    {
        var sqlQuery = new StringBuilder();
        var sqlParams = new MapSqlParameterSource();

        if (posts == null || posts.isEmpty())
        {
            return;
        }

        /*
         * Индексируем посты по их идентификатору для обеспечения доступа за O(1).
         */
        final var postMap = posts.stream()
                .collect(Collectors.toMap(
                        PostEntityObject::getId,
                        Function.identity()
                ));

        sqlParams = new MapSqlParameterSource(
                "postIds",
                postMap.keySet()
        );

        /*
         * Формируем базовую выборку полей сущности.
         */
        sqlQuery.append("SELECT [PT].[PostId], ")
                .append("[T].[Id] AS [TagId], ")
                .append("[T].[Name] ")
                .append("FROM [dbo].[PostTags] AS [PT] ")
                .append("JOIN [dbo].[Tags] AS [T] ")
                .append("ON [T].[Id] = [PT].[TagId] ")
                .append("WHERE [PT].[PostId] IN(:postIds)");

        /*
         * Получаем плоский список проекций (PostId + TagEntityObject).
         */
        final var projections = jdbcTemplate.query(
                sqlQuery.toString(),
                sqlParams,
                new PostTagRowMapper()
        );

        /*
         * Группируем теги по Id постов, чтобы избежать затирания при вызове initializeTags.
         */
        final var tagsByPostId = projections.stream()
                .collect(Collectors.groupingBy(
                        PostTagProjection::getPostId,
                        Collectors.mapping(PostTagProjection::getTag, Collectors.toList())
                ));

        /*
         * Чистая гидратация: накатываем списки тегов на сущности.
         */
        for (final var entry : tagsByPostId.entrySet())
        {
            final var post = postMap.get(entry.getKey());

            if (post != null)
            {
                post.initializeTags(entry.getValue());
            }
        }
    }

    /**
     * <summary>
     * Добавляет в базу данных теги и возвращает список их идентификаторов.
     * </summary>
     * <param name="tags">
     * Список тегов.
     * </param>
     **/
    public List<Long> getOrCreateTags(final List<String> tags)
    {
        if (tags == null ||
            tags.isEmpty())
        {
            return List.of();
        }

        var result = new ArrayList<Long>();

        for (var tag : tags)
        {
            if (tag == null)
            {
                continue;
            }

            var trimmedName = tag.trim().toLowerCase();

            if (trimmedName.isEmpty())
            {
                continue;
            }

            var existingId = findTagIdByName(trimmedName);

            if (existingId != null)
            {
                result.add(existingId);

                continue;
            }

            try
            {
                var insertSqlQuery = "INSERT INTO [dbo].[Tags] ([Name]) VALUES (:name);";

                var keyHolder = new GeneratedKeyHolder();

                jdbcTemplate.update(
                        insertSqlQuery,
                        new MapSqlParameterSource("name", trimmedName),
                        keyHolder,
                        new String[]{"Id"}
                );

                var key = keyHolder.getKey();

                if (key != null)
                {
                    result.add(key.longValue());
                }
            }
            catch (Exception ex)
            {
                var concurrentId = findTagIdByName(trimmedName);

                if (concurrentId != null)
                {
                    result.add(concurrentId);
                }
                else
                {
                    throw ex;
                }
            }
        }

        return result;
    }

    /**
     * <summary>
     * Обновляет теги публикации.
     * </summary>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <param name="tagIds">
     * Cписок идентификаторов тегов..
     * </param>
     **/
    public void updatePostTags(final Long postId, final List<Long> tagIds)
    {
        if (postId == null)
        {
            return;
        }

        var deleteSqlQuery = "DELETE FROM [dbo].[PostTags] WHERE [PostId] = :postId;";

        jdbcTemplate.update(
            deleteSqlQuery,
            new MapSqlParameterSource("postId", postId));

        if (tagIds == null ||
            tagIds.isEmpty())
        {
            return;
        }

        var insertSqlQuery = "INSERT INTO [dbo].[PostTags] ([PostId],[TagId]) VALUES (:postId, :tagId)";

        var batchParams = new SqlParameterSource[tagIds.size()];

        for (var i = 0; i < tagIds.size(); i++)
        {
            batchParams[i] =  new MapSqlParameterSource()
                    .addValue("postId", postId)
                    .addValue("tagId", tagIds.get(i));
        }

        jdbcTemplate.batchUpdate(
                insertSqlQuery,
                batchParams
        );
    }

    /**
     * <summary>
     * Удаляет из базы данных все теги, которые не привязаны ни к одной публикации.
     * </summary>
     **/
    public void clearOrphanedTags()
    {
        var deleteSqlQuery = "DELETE FROM [dbo].[Tags] " +
                             "WHERE [Id] NOT IN (SELECT DISTINCT [TagId] FROM [dbo].[PostTags])";

        jdbcTemplate.getJdbcTemplate().update(deleteSqlQuery);
    }

    // endregion
}