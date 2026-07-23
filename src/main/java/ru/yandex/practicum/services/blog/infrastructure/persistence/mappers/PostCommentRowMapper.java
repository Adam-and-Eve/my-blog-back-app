package ru.yandex.practicum.services.blog.infrastructure.persistence.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.services.blog.core.domain.entityobjects.CommentEntityObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.CommentTextValueObject;
import ru.yandex.practicum.services.blog.infrastructure.persistence.projections.PostCommentProjection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

/**
 * <summary>
 * Маппер для извлечения проекции связи поста и комментария из ResultSet.
 * </summary>
 **/
public final class PostCommentRowMapper implements RowMapper<PostCommentProjection>
{
    // region Methods

    /**
     * <summary>
     * Компонент маппинга, отвечающий за логику сопоставления колонок
     * результирующего набора данных полям проекции.
     * </summary>
     */
    public PostCommentProjection mapRow(ResultSet rs, int rowNum) throws SQLException
    {
        return new PostCommentProjection(
                rs.getLong("PostId"),
                new CommentEntityObject(
                        rs.getLong("Id"),
                        new CommentTextValueObject(rs.getString("Text")),
                        rs.getObject("CreatedAt", OffsetDateTime.class),
                        rs.getObject("UpdatedAt", OffsetDateTime.class)
                )
        );
    }

    // endregion
}