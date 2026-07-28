package ru.yandex.practicum.services.blog.infrastructure.persistence.mappers;

import ru.yandex.practicum.services.blog.core.domain.entityobjects.CommentEntityObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.CommentTextValueObject;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

/**
 * <summary>
 * Маппер для преобразования строки из таблицы PostComments (ResultSet)
 * в объект доменной сущности PostCommentEntityObject.
 * </summary>
 **/
public class CommentRowMapper implements RowMapper<CommentEntityObject>
{
    // region Methods

    /**
     * <summary>
     * Компонент маппинга, отвечающий за преобразование строк результирующего набора данных (ResultSet)
     * в объекты доменной сущности PostCommentEntityObject.
     * </summary>
     */
    @Override
    public CommentEntityObject mapRow(final ResultSet rs, final int rowNum) throws SQLException
    {
        return new CommentEntityObject(
                rs.getLong("Id"),
                new CommentTextValueObject(rs.getString("Text")),
                rs.getObject("CreatedAt", OffsetDateTime.class),
                rs.getObject("UpdatedAt", OffsetDateTime.class)
        );
    }

    // endregion
}