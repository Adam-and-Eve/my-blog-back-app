package ru.yandex.practicum.services.blog.infrastructure.persistence.repositories;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.services.blog.core.application.interfaces.IPostRepository;
import ru.yandex.practicum.services.blog.core.application.queries.PostSearchCriteria;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;
import ru.yandex.practicum.services.blog.infrastructure.persistence.mappers.PostRowMapper;
import ru.yandex.practicum.services.blog.infrastructure.persistence.mappers.PostTagRowMapper;

import java.util.List;
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
                        .append("JOIN [dbo].[Tags] AS [T] ON [T].[Id] = [PT].[TagId] ") // Проверь Id или TagId!
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