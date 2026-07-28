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
     * Список объектов-сущностей (Entity Object), представляющих комментарии публикации.
     **/
    private final Set<CommentEntityObject> comments;

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

        this.title = Objects.requireNonNull(title, "Объект заголовка публикации не может быть null.");

        this.text = Objects.requireNonNull(text, "объект сообщения публикации не может быть null.");

        this.tags = new HashSet<>();

        this.comments = new HashSet<>();

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

        this.title = Objects.requireNonNull(title, "Объект заголовка публикации не может быть null.");

        this.text = Objects.requireNonNull(text, "Объект сообщения публикации не может быть null.");

        this.tags = new HashSet<>();

        this.comments = new HashSet<>();

        this.likesCount = Objects.requireNonNull(likesCount, "Значением количества лайков публикации не может быть null.");
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
     * Возвращает список комментариев публикации.
     * </summary>
     * <return>
     * @return Список объектов-сущностей (Entity Object) комментариев публикации.
     * </return>
     **/
    public final synchronized Set<CommentEntityObject> getComments()
    {
        return Set.copyOf(comments);
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

    /**
     * <summary>
     * Возвращает количество комментариев публикации.
     * </summary>
     * <return>
     * @return Количество комментариев публикации.
     * </return>
     **/
    public final synchronized Long getCommentsCount()
    {

        return (long)comments.size();
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
        Objects.requireNonNull(title, "Объект заголовка публикации не может быть null.");

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
        Objects.requireNonNull(text, "Объект сообщения публикации не может быть null.");

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
        Objects.requireNonNull(tag, "Объект тега публикации не может быть null.");

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
        Objects.requireNonNull(tag, "Объект тега публикации не может быть null.");

        if (tags.remove(tag))
        {
            super.markUpdatedAt();
        }
    }

    /**
     * <summary>
     * Инициализирует список тегов при загрузке из репозитория.
     * Чистая гидратация без изменения метки времени сущности.
     * </summary>
     * @param tags Теги из базы данных сервиса.
     **/
    public synchronized void initializeTags(final Collection<TagEntityObject> tags)
    {
        Objects.requireNonNull(tags, "Перечисление объектов тегов публикации не может быть null.");

        this.tags.clear();

        this.tags.addAll(tags);
    }

    /**
     * <summary>
     * Добавляет комментарий к публикации.
     * При изменении состояния обновляет метку времени последнего изменения.
     * </summary>
     * @param comment Новый комментарий для добавления.
     **/
    public final synchronized void addComment(final CommentEntityObject comment)
    {
        Objects.requireNonNull(comment, "Объект комментария публикации не может быть null.");

        if (comments.add(comment))
        {
            super.markUpdatedAt();
        }
    }

    /**
     * <summary>
     * Инициализирует список комментариев при загрузке из репозитория.
     * </summary>
     * @param comments Комментарии из базы данных сервиса.
     **/
    public final synchronized void initializeComments(final Collection<CommentEntityObject> comments)
    {
        Objects.requireNonNull(comments, "Перечисление объектов комментариев публикации не может быть null.");

        this.comments.clear();

        this.comments.addAll(comments);
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