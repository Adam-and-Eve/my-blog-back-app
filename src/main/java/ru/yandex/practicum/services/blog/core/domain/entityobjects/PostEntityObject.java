package ru.yandex.practicum.services.blog.core.domain.entityobjects;

import ru.yandex.practicum.services.blog.core.domain.entityobjects.base.BaseEntityObject;
import ru.yandex.practicum.services.blog.core.domain.exceptions.ValueObjectIsInvalidException;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTitleValueObject;

import java.util.Objects;

/**
 * <summary>
 * Сущность, представляющая пост блога.
 * Содержит контент публикации, метаданные и статистику взаимодействия.
 * </summary>
 **/
public final class PostEntityObject extends BaseEntityObject
{
    // region Fields

    /**
     * Объект-значение (Value Object), представляющий заголовок публикации.
     **/
    private PostTitleValueObject title;

    /**
     * Объект-значение (Value Object), представляющий заголовок публикации.
     **/
    private PostTextValueObject text;

    // endregion

    // region Constructors

    public PostEntityObject(
            PostTitleValueObject title,
            PostTextValueObject text
    )
    {
        super();

        this.title = Objects.requireNonNull(title);

        this.text = Objects.requireNonNull(text);
    }

    // endregion

    // region Properties

    /**
     * <summary>
     * Возвращает заголовок публикации.
     * </summary>
     * <return>
     * @return Объект-значение заголовка.
     * </return>
     **/
    public final PostTitleValueObject getTitle()
    {
        return title;
    }

    /**
     * <summary>
     * Возвращает сообщение публикации.
     * </summary>
     * <return>
     * @return Объект-значение сообщения.
     * </return>
     **/
    public final PostTextValueObject getText()
    {
        return text;
    }

    // endregion

    // region Methods

    /**
     * <summary>
     * Изменяет заголовок публикации.
     * При изменении состояния обновляет метку времени последнего изменения.
     * </summary>
     * @param title Новый заголовок публикации.
     **/
    public final void changeTitle(final PostTitleValueObject title)
    {
        if (!this.title.equals(title))
        {
            this.title = title;

            super.markUpdatedAt();
        }
    }

    /**
     * <summary>
     * Изменяет сообщение публикации.
     * При изменении состояния обновляет метку времени последнего изменения.
     * </summary>
     * @param text Новое сообщение публикации.
     **/
    public final void changeText(final PostTextValueObject text)
    {
        if (!this.text.equals(text))
        {
            this.text = text;

            super.markUpdatedAt();
        }
    }

    // endregion
}