package ru.yandex.practicum.services.blog.infrastructure.persistence.repositories;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.services.blog.core.application.interfaces.IPostRepository;
import ru.yandex.practicum.services.blog.core.application.queries.PostSearchCriteria;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;
import ru.yandex.practicum.services.blog.infrastructure.persistence.mappers.PostRowMapper;
import ru.yandex.practicum.services.blog.infrastructure.persistence.mappers.PostTagRowMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <summary>
 * Реализация репозитория публикаций с использованием Spring JDBC Template.
 * Отвечает за маппинг доменных сущностей в реляционную модель базы данных.
 * </summary>
 **/
public final class PostJdbcRepository implements IPostRepository
{
    // region Fields

    private final NamedParameterJdbcTemplate jdbcTemplate;

    // endregion

    // region Constructors

    public PostJdbcRepository(final NamedParameterJdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // endregion

    // region Properties



    // endregion

    // region Methods

    /**
     * <summary>
     * Получение постраничного списка публикаций с учетом фильтрации и пагинации на стороне БД.
     * </summary>
     * <param name="criteria">
     * Объект с критериями для поиска публикаций.
     * </param>
     * <param name="offset">
     * Количество записей, которые нужно пропустить (для пагинации).
     * </param>
     * <param name="limit">
     * Максимальное количество возвращаемых записей (размер страницы).
     * </param>
     * <return>
     * @return Отсортированный список найденных доменных сущностей публикаций.
     * </return>
     **/
    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public List<PostEntityObject> findAllPosts(
            final PostSearchCriteria criteria,
            final Long offset,
            final Long limit
    )
    {
        var sqlQuery = new StringBuilder();
        var sqlParams = new MapSqlParameterSource();

        /*
         * Формируем базовую выборку полей сущности.
         */
        sqlQuery.append("SELECT [P].[Id], ")
                .append("[P].[Title], ")
                .append("[P].[Text], ")
                .append("[P].[LikesCount], ")
                .append("[P].[CreatedAt], ")
                .append("[P].[UpdatedAt] ")
                .append("FROM [dbo].[Posts] AS [P] ");

        /*
         * Применяем динамические фильтры.
         */
        appendFilters(sqlQuery, sqlParams, criteria);

        /*
         * Добавляем сортировку постов (сначала самые свежие).
         */
        sqlQuery.append("ORDER BY [P].[CreatedAt] DESC ");

        /*
         * Добавляем пагинацию MS SQL Server.
         */
        sqlQuery.append("OFFSET :offset ROW FETCH NEXT :limit ROWS ONLY ");

        sqlParams.addValue("offset", offset);

        sqlParams.addValue("limit", limit);

        /*
         * Получаем "голые" посты (без тегов) из таблицы Posts.
         */
        final var posts = jdbcTemplate.query(
                sqlQuery.toString(),
                sqlParams,
                new PostRowMapper());

        /*
         * Обогащаем полученные посты тегами.
         */
        enrichPostsWithTags(posts);

        return posts;
    }

    /**
     * <summary>
     * Получение публикации.
     * </summary>
     * <param name="id">
     * Идентификатор публикации.
     * </param>
     * <return>
     * @return Доменная сущность публикации.
     * </return>
     **/
    public PostEntityObject findPostById(final Long id)
    {
        var sqlQuery = new StringBuilder();
        var sqlParams = new MapSqlParameterSource();

        /*
         * Формируем базовую выборку полей сущности.
         */
        sqlQuery.append("SELECT [P].[Id], ")
                .append("[P].[Title], ")
                .append("[P].[Text], ")
                .append("[P].[LikesCount], ")
                .append("[P].[CreatedAt], ")
                .append("[P].[UpdatedAt] ")
                .append("FROM [dbo].[Posts] AS [P] ")
                .append("WHERE [P].[Id] = :id ");

        /*
         * Применяем фильтр по идентификатору сущности.
         */
        sqlParams.addValue(
                "id",
                id);

        try
        {
            /*
             * Получаем "голый" пост (без тегов) из таблицы Posts.
             */
            var post = jdbcTemplate.queryForObject(
                    sqlQuery.toString(),
                    sqlParams,
                    new PostRowMapper()
            );

            /*
             * Обогащаем полученный пост тегами.
             */
            enrichPostsWithTags(List.of(post));

            return post;
        }
        catch (EmptyResultDataAccessException ex)
        {
            return null;
        }
    }

    /**
     * <summary>
     * Подсчет общего количества публикаций, удовлетворяющих заданным фильтрам.
     * Необходим для корректного расчета метаданных пагинации (общее количество страниц).
     * <param name="criteria">
     * Объект с критериями для поиска публикаций.
     * </param>
     * <return>
     * @return Общее количество найденных публикаций.
     * </return>
     **/
    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public Long countAllPosts(final PostSearchCriteria criteria)
    {
        var sqlQuery = new StringBuilder();
        var sqlParams = new MapSqlParameterSource();

        /*
         * Формируем базовую выборку полей сущности.
         */
        sqlQuery.append("SELECT COUNT(*) ")
                .append("FROM [dbo].[Posts] AS [P] ");

        /*
         * Применяем динамические фильтры.
         */
       appendFilters(sqlQuery, sqlParams, criteria);

        var result = jdbcTemplate.queryForObject(
                sqlQuery.toString(),
                sqlParams,
                Long.class);

        return result != null ? result : 0L;
    }

    /**
     * <summary>
     * Увеличивает счетчик лайков публикации на единицу.
     * </summary>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <return>
     * Обновленное количество лайков или empty, если публикация не найдена.
     * </return>
     */
    @Override
    public Optional<Long> incrementLikesCount(final Long postId)
    {
        var sqlQuery = new StringBuilder();
        var sqlParams = new MapSqlParameterSource();

        sqlQuery.append("UPDATE [dbo].[Posts] ")
                .append("SET [LikesCount] = [LikesCount] + 1, ")
                .append("[UpdatedAt] = :updatedAt ")
                .append("OUTPUT INSERTED.[LikesCount] ")
                .append("WHERE [Id] = :postId ");

        sqlParams.addValue("postId", postId)
                 .addValue("updatedAt", OffsetDateTime.now());

        try
        {
            final var result =  jdbcTemplate.queryForObject(
                    sqlQuery.toString(),
                    sqlParams,
                    Long.class);

            return Optional.ofNullable(result);
        }
        catch (EmptyResultDataAccessException ex)
        {
            return Optional.empty();
        }
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
    private void enrichPostsWithTags(final List<PostEntityObject> posts)
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
         * Распределяем восстановленные сущности тегов по соответствующим постам.
         */
        for (final var projection : projections)
        {
            final var post = postMap.get(projection.getPostId());

            if (post != null)
            {
                post.addTag(projection.getTag());
            }
        }
    }

    /**
     * <summary>
     * Создание публикации.
     * </summary>
     * <param name="post">
     * Объект публикации.
     * </param>
     * <return>
     * @return Идентификатор публикации.
     * </return>
     **/
    public Optional<Long> createPost(final PostEntityObject post)
    {
        var sqlQuery = new StringBuilder();
        var sqlParams = new MapSqlParameterSource();

        sqlQuery.append("INSERT [dbo].[Posts] ")
                .append("(Title, Text, LikesCount, CreatedAt, UpdatedAt) ")
                .append("OUTPUT inserted.Id ")
                .append("VALUES (:title, :text, :likesCount, :createdAt, :updatedAt); ");

        sqlParams.addValue("title", post.getTitle().getValue())
                .addValue("text", post.getText().getValue())
                .addValue("likesCount", post.getLikesCount())
                .addValue("createdAt", post.getCreatedAt())
                .addValue("updatedAt", post.getUpdatedAt());

        try
        {
            final var result =  jdbcTemplate.queryForObject(
                    sqlQuery.toString(),
                    sqlParams,
                    Long.class);

            return Optional.ofNullable(result);
        }
        catch (EmptyResultDataAccessException ex)
        {
            return Optional.empty();
        }
    }

    /**
     * <summary>
     * Удаление публикации.
     * </summary>
     * <param name="id">
     * Идентификатор публикации.
     * </param>
     * <return>
     * @return Статус удаления публикации.
     * </return>
     **/
    public Boolean deletePost(final Long id)
    {
        if (id == null)
        {
            return false;
        }

        var sqlQuery = new StringBuilder();
        var sqlParams = new MapSqlParameterSource();

        sqlQuery.append("DELETE [dbo].[Posts] ")
                .append("WHERE [Id] = :postId ");

        sqlParams.addValue("postId", id);

        final var result =  jdbcTemplate.update(
                sqlQuery.toString(),
                sqlParams);

        return result > 0;
    }

    /**
     * <summary>
     * Вспомогательный метод для динамической сборки условий WHERE.
     * </summary>
     * <param name="sqlQuery">
     * Конструктор строки SQL-запроса.
     * </param>
     * <param name="sqlParams">
     * Контейнер для именованных параметров.
     * </param>
     * <param name="criteria">
     * Критерии фильтрации.
     * </param>
     */
    private void appendFilters(
            final StringBuilder sqlQuery,
            final MapSqlParameterSource sqlParams,
            final PostSearchCriteria criteria
    )
    {
        sqlQuery.append(" WHERE 1 = 1 ");

        /*
         * Фильтрация по частичному совпадению в заголовке.
         */
        if (criteria.getTitleQuery() != null &&
            !criteria.getTitleQuery().isBlank())
        {
            sqlQuery.append("AND [P].[Title] LIKE :title ");

            sqlParams.addValue(
                    "title",
                    "%" + criteria.getTitleQuery().trim() + "%");
        }

        /*
         * Фильтрация по набору тегов (логика "И").
         * Для каждого тега генерируется отдельное условие EXISTS.
         */
        if (criteria.getTagFilters() != null &&
            !criteria.getTagFilters().isEmpty())
        {
            for (var i = 0; i < criteria.getTagFilters().size(); i++)
            {
                final var paramName = "tagFilter" + i;

                final var tagValue = criteria.getTagFilters().get(i);

                sqlQuery.append("AND EXISTS ( ")
                        .append("SELECT 1 FROM [dbo].[PostTags] AS [PT] ")
                        .append("JOIN [dbo].[Tags] AS [T] ON [T].[Id] = [PT].[TagId] ")
                        .append("WHERE [PT].[PostId] = [P].[Id] ")
                        .append("AND [T].[Name] = :")
                        .append(paramName)
                        .append(" ) ");

                sqlParams.addValue(paramName, tagValue);
            }
        }
    }

    // endregion
}