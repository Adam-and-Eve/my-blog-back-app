package ru.yandex.practicum.services.blog.infrastructure.persistence.repositories;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.services.blog.core.application.interfaces.ImageRepository;

/**
 * <summary>
 * Реализация репозитория изображений с использованием Spring JDBC Template.
 * Отвечает за маппинг доменных сущностей в реляционную модель базы данных.
 * </summary>
 **/
public final class ImageJdbcRepositoryImpl implements ImageRepository
{
    // region Fields

    private final NamedParameterJdbcTemplate jdbcTemplate;

    // endregion

    // region Constructors

    public ImageJdbcRepositoryImpl(final NamedParameterJdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // endregion

    // region Properties



    // endregion

    // region Methods

    /**
     * <summary>
     * Добавляет или обновляет в базе данных изображение публикации.
     * </summary>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <param name="imageContent">
     * Изображение публикации.
     * </param>
     * <return>
     * Статус выполнения операции.
     * </return>
     **/
    public Boolean saveOrUpdatePostImage(final Long postId, final byte[] imageContent)
    {
        if (postId == null)
        {
            return false;
        }

        var updateSqlQuery = "UPDATE [dbo].[PostImages] " +
                             "SET [Content] = :content " +
                             "WHERE [PostId] = :postId;";

        var sqlParams = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("content", imageContent);

        var rowsAffected = jdbcTemplate.update(updateSqlQuery, sqlParams);

        if (rowsAffected == 0)
        {
            var insertSqlQuery = "INSERT INTO [dbo].[PostImages] ([PostId], [Content]) " +
                                 "VALUES (:postId, :content);";

            rowsAffected = jdbcTemplate.update(insertSqlQuery, sqlParams);
        }

        return rowsAffected > 0;
    }

    /**
     * <summary>
     * Получает изображение по идентификатору публикации.
     * </summary>
     * <param name="postId">
     * Идентификатор публикации.
     * </param>
     * <return>
     * Изображение публикации.
     * </return>
     **/
    public byte[] findPostImageBytesByPostId(final Long postId)
    {
        if (postId == null)
        {
            return null;
        }

        var selectSqlQuery = "SELECT [Content] FROM [dbo].[PostImages] WHERE postId = :postId;";

        var sqlParams = new MapSqlParameterSource()
                .addValue("postId", postId);

        var sqlResponse = jdbcTemplate.query(
            selectSqlQuery,
            sqlParams,
            (rs, rowNum) -> rs.getBytes("Content")
        );

        if (sqlResponse.isEmpty())
        {
            return null;
        }

        return sqlResponse.getFirst();
    }

    // endregion
}