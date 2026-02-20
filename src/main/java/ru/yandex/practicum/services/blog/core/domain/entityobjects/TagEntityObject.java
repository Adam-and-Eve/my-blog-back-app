package ru.yandex.practicum.services.blog.core.domain.entityobjects;

import ru.yandex.practicum.services.blog.core.domain.entityobjects.base.BaseEntityObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTagNameObject;

import java.util.Objects;

/**
 * <summary>
 * Сущность, представляющая тег.
 * </summary>
 **/
public final class TagEntityObject extends BaseEntityObject
{
    // region Fields

    /**
     * Объект-значение (Value Object), представляющий значение тега.
     **/
    private PostTagNameObject name;

    // endregion

    // region Constructors

    public TagEntityObject(final PostTagNameObject name)
    {
        super();

        this.name = Objects.requireNonNull(name);
    }

    public TagEntityObject(
            final Long id,
            final PostTagNameObject name)
    {
        super(id, null, null);

        this.name = Objects.requireNonNull(name);
    }

    // endregion

    // region Properties

    /**
     * <summary>
     * Возвращает тег.
     * </summary>
     * <return>
     * @return Объект-значение тега.
     * </return>
     **/
    public final synchronized PostTagNameObject getName()
    {
        return name;
    }

    // endregion

    // region Methods

    /**
     * <summary>
     * Изменяет значение тега.
     * При изменении состояния обновляет метку времени последнего изменения.
     * </summary>
     * @param name Новое значение тега.
     **/
    public final synchronized void changeName(final PostTagNameObject name)
    {
        if (!this.name.equals(name))
        {
            this.name = name;

            super.markUpdatedAt();
        }
    }

    // endregion
}