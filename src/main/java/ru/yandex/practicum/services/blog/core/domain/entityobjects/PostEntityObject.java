package ru.yandex.practicum.services.blog.core.domain.entityobjects;

import ru.yandex.practicum.services.blog.core.domain.entityobjects.base.BaseEntityObject;
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

    /**
     * Количество лайков публикации.
     **/
    private Long likesCount;

    // endregion

    // region Constructors

    public PostEntityObject(
            PostTitleValueObject title,
            PostTextValueObject text,
            Long likesCount
    )
    {
        super();

        this.title = Objects.requireNonNull(title);

        this.text = Objects.requireNonNull(text);

        this.likesCount = Objects.requireNonNull(likesCount);
    }

    public PostEntityObject(
            PostTitleValueObject title,
            PostTextValueObject text
    )
    {
        this(title, text, 0L);
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
    public final synchronized PostTitleValueObject getTitle()
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
    public final synchronized PostTextValueObject getText()
    {
        return text;
    }

    /**
     * <summary>
     * Возвращает количество лайков публикации.
     * </summary>
     * <return>
     * @return Количество лайков публикации.
     * </return>
     **/
    public final synchronized Long getLikesCount()
    {
        return likesCount;
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
    public final synchronized void changeTitle(final PostTitleValueObject title)
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
    public final synchronized void changeText(final PostTextValueObject text)
    {
        if (!this.text.equals(text))
        {
            this.text = text;

            super.markUpdatedAt();
        }
    }

    /**
     * <summary>
     * Увеличивает количество лайков публикации на единицу.
     * При изменении состояния обновляет метку времени последнего изменения.
     * </summary>
     **/
    public final synchronized void like()
    {
        if (this.likesCount < Long.MAX_VALUE)
        {
            this.likesCount++;

            super.markUpdatedAt();
        }
    }

    /**
     * <summary>
     * Уменьшает количество лайков публикации на единицу, если текущее значение больше нуля.
     * При изменении состояния обновляет метку времени последнего изменения.
     * </summary>
     **/
    public final synchronized void unlike()
    {
        if (this.likesCount > 0)
        {
            this.likesCount--;

            super.markUpdatedAt();
        }
    }

    // endregion
}