package ru.yandex.practicum.services.blog.infrastructure.persistence.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.PostEntityObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTitleValueObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

/**
 * <summary>
 * Маппер для преобразования строки из таблицы Posts (ResultSet)
 * в объект доменной сущности PostEntityObject.
 * </summary>
 **/
public final class PostRowMapper implements RowMapper<PostEntityObject>
{
    // region Methods

    /**
     * <summary>
     * Компонент маппинга, отвечающий за преобразование строк результирующего набора данных (ResultSet)
     * в объекты доменной сущности PostEntityObject.
     * </summary>
     */
    @Override
    public PostEntityObject mapRow(final ResultSet rs, final int rowNum) throws SQLException
    {
        return new PostEntityObject(
                rs.getLong("Id"),
                new PostTitleValueObject(rs.getString("Title")),
                new PostTextValueObject(rs.getString("Text")),
                rs.getLong("LikesCount"),
                rs.getObject("CreatedAt", OffsetDateTime.class),
                rs.getObject("UpdatedAt", OffsetDateTime.class)
        );
    }

    // endregion
}