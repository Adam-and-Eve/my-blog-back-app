package ru.yandex.practicum.services.blog.core.domain.entityobjects;

import ru.yandex.practicum.services.blog.core.domain.entityobjects.base.BaseEntityObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.CommentTextValueObject;

import java.time.OffsetDateTime;

/**
 * <summary>
 * Сущность, представляющая комментарий.
 * </summary>
 **/
public class CommentEntityObject extends BaseEntityObject
{
    // Fields

    /**
     * Объект-значение (Value Object), представляющий значение комментария.
     **/
    private CommentTextValueObject text;

    // endregion

    // region Constructors

    public CommentEntityObject(final CommentTextValueObject text)
    {
        super();

        this.text = text;
    }

    public CommentEntityObject(
            final Long id,
            final CommentTextValueObject text,
            final OffsetDateTime createdAt,
            final OffsetDateTime updatedAt
            )
    {
        super(id, createdAt, updatedAt);

        this.text = text;
    }

    // endregion

    // region Properties

    /**
     * <summary>
     * Возвращает сообщение комментария.
     * </summary>
     * <return>
     * @return Объект-значение сообщения комментария.
     * </return>
     **/
    public final synchronized CommentTextValueObject getText()
    {
        return text;
    }

    // endregion

    // region Methods

    /**
     * <summary>
     * Изменяет значение сообщения комментария.
     * При изменении состояния обновляет метку времени последнего изменения.
     * </summary>
     * @param text Новое значение сообщения комментария.
     **/
    public final synchronized void changeText(final CommentTextValueObject text)
    {
        if (!this.text.equals(text))
        {
            this.text = text;

            super.markUpdatedAt();
        }
    }

    // endregion
}