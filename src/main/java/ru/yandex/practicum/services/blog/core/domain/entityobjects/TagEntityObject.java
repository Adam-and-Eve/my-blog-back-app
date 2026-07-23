package ru.yandex.practicum.services.blog.core.domain.entityobjects;

import ru.yandex.practicum.services.blog.core.domain.entityobjects.base.BaseEntityObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.TagNameObject;

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
    private TagNameObject name;

    // endregion

    // region Constructors

    public TagEntityObject(final TagNameObject name)
    {
        super();

        this.name = Objects.requireNonNull(name);
    }

    public TagEntityObject(
            final Long id,
            final TagNameObject name)
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
    public final synchronized TagNameObject getName()
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
    public final synchronized void changeName(final TagNameObject name)
    {
        if (!this.name.equals(name))
        {
            this.name = name;

            super.markUpdatedAt();
        }
    }

    // endregion
}