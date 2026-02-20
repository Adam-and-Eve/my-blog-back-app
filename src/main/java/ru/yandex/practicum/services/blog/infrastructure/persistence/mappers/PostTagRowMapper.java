package ru.yandex.practicum.services.blog.infrastructure.persistence.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.TagEntityObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTagNameObject;
import ru.yandex.practicum.services.blog.infrastructure.persistence.projections.PostTagProjection;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <summary>
 * Маппер для извлечения проекции связи поста и тега из ResultSet.
 * </summary>
 **/
public final class PostTagRowMapper implements RowMapper<PostTagProjection>
{
    /**
     * <summary>
     * Компонент маппинга, отвечающий за логику сопоставления колонок
     * результирующего набора данных полям проекции.
     * </summary>
     */
    @Override
    public PostTagProjection mapRow(final ResultSet rs, final int rowNum) throws SQLException
    {
        return new PostTagProjection(
                rs.getLong("PostId"),
                new TagEntityObject(
                        rs.getLong("TagId"),
                        new PostTagNameObject(rs.getString("Name"))
                )
        );
    }
}