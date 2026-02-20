package ru.yandex.practicum.services.blog.core.domain.entityobjects;

import ru.yandex.practicum.services.blog.core.domain.entityobjects.base.BaseEntityObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTextValueObject;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.PostTitleValueObject;

import java.time.OffsetDateTime;
import java.util.*;

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
     * Объект-значение (Value Object), представляющий сообщение публикации.
     **/
    private PostTextValueObject text;

    /**
     * Список объектов-сущностей (Entity Object), представляющих теги публикации.
     **/
    private final Set<TagEntityObject> tags;

    /**
     * Количество лайков публикации.
     **/
    private Long likesCount;

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

        this.tags = new HashSet<>();

        this.likesCount = 0L;
    }

    public PostEntityObject(
            Long id,
            PostTitleValueObject title,
            PostTextValueObject text,
            Long likesCount,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    )
    {
        super(id, createdAt, updatedAt);

        this.title = Objects.requireNonNull(title);

        this.text = Objects.requireNonNull(text);

        this.tags = new HashSet<>();

        this.likesCount = Objects.requireNonNull(likesCount);
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
     * Возвращает список тегов публикации.
     * </summary>
     * <return>
     * @return Список объектов-сущностей (Entity Object) тегов публикации.
     * </return>
     **/
    public final synchronized Set<TagEntityObject> getTags()
    {
        return Set.copyOf(tags);
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
     * Добавляет тег к публикации, если его там еще нет.
     * При изменении состояния обновляет метку времени последнего изменения.
     * </summary>
     * @param tag Новый тег для добавления.
     **/
    public final synchronized void addTag(final TagEntityObject tag)
    {
        Objects.requireNonNull(tag);

        if (tags.add(tag))
        {
            super.markUpdatedAt();
        }
    }

    /**
     * <summary>
     * Удаляет тег из публикации, если он там уже есть.
     * При изменении состояния обновляет метку времени последнего изменения.
     * </summary>
     * @param tag Тег для удаления.
     **/
    public final synchronized void removeTag(final TagEntityObject tag)
    {
        Objects.requireNonNull(tag);

        if (tags.remove(tag))
        {
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