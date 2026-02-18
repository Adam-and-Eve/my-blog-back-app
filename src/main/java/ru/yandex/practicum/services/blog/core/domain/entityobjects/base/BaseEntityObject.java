package ru.yandex.practicum.services.blog.core.domain.entityobjects.base;

import ru.yandex.practicum.services.blog.core.domain.exceptions.EntityObjectIllegalStateException;

import java.time.LocalDateTime;

/**
 * <summary>
 * Базовый абстрактный класс для всех сущностей (Entity Objects) сервиса.
 * Обеспечивает наличие уникального идентификатора и меток времени жизненного цикла объекта.
 * </summary>
 **/
public abstract class BaseEntityObject
{
    // region Fields

    /**
     * Уникальный идентификатор сущности.
     **/
    private Long id;

    /**
     * Дата и время создания объекта.
     **/
    private final LocalDateTime createdAt;

    /**
     * Дата и время последнего изменения объекта.
     **/
    private LocalDateTime updatedAt;

    // endregion

    // region Constructors

    public BaseEntityObject()
    {
        this.id = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = createdAt;
    }

    public BaseEntityObject(
            final Long id,
            final LocalDateTime createdAt,
            final LocalDateTime updatedAt
    )
    {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // endregion

    // region Properties

    /**
     * <summary>
     * Возвращает уникальный идентификатор сущности.
     * </summary>
     * <return>
     * @return Уникальный идентификатор сущности.
     * </return>
     **/
    public final synchronized Long getId()
    {
        return this.id;
    }

    /**
     * <summary>
     * Возвращает дату и время создания сущности.
     * </summary>
     * <return>
     * @return Дата и время создания сущности.
     * </return>
     **/
    public final synchronized LocalDateTime getCreatedAt()
    {
        return this.createdAt;
    }

    /**
     * <summary>
     * Возвращает дату и время последнего изменения сущности.
     * </summary>
     * <return>
     * @return Дата и время последнего изменения сущности.
     * </return>
     **/
    public final synchronized LocalDateTime getUpdatedAt()
    {
        return this.updatedAt;
    }

    // endregion

    // region Methods

    /**
     * <summary>
     * Устанавливает идентификатор сущности.
     * </summary>
     * @param id Новый идентификатор сущности.
     * @throws EntityObjectIllegalStateException
     * Выбрасывается, если идентификатор уже был установлен.
     **/
    public synchronized void changeId(final Long id)
    {
        if (this.id != null)
        {
            throw new EntityObjectIllegalStateException(
                    "Идентификатор сущности уже установлен",
                    "id");
        }
        else
        {
            this.id = id;

            markUpdatedAt();
        }
    }

    /**
     * <summary>
     * Обновляет значение свойства UpdatedAt текущим системным временем.
     * Должен вызываться при любом изменении внутреннего состояния сущности.
     * </summary>
     **/
    public synchronized void markUpdatedAt()
    {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * <summary>
     * Сравнивает текущую сущность с другим объектом на предмет идентичности.
     * </summary>
     * @param obj Объект для сравнения.
     * @return true, если объекты идентичны; иначе false.
     **/
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof BaseEntityObject other))
        {
            return false;
        }

        if (this.id == null || other.id == null)
        {
            return false;
        }

        return this.id.equals(other.id);
    }

    /**
     * <summary>
     * Вычисляет хэш-код сущности.
     * </summary>
     * @return Целочисленное значение хэш-кода.
     **/
    @Override
    public int hashCode()
    {
        return (id != null)
                ? id.hashCode()
                : System.identityHashCode(this);
    }

    // endregion
}