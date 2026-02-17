package ru.yandex.practicum.services.blog.core.domain.entityobjects.base;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

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
    private final UUID id;

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
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = createdAt;
    }

    public BaseEntityObject(
            final UUID id,
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
    public final UUID getId()
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
    public final LocalDateTime getCreatedAt()
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
    public final LocalDateTime getUpdatedAt()
    {
        return this.updatedAt;
    }

    // endregion

    // region Methods

    /**
     * <summary>
     * Обновляет значение свойства UpdatedAt текущим системным временем.
     * Должен вызываться при любом изменении внутреннего состояния сущности.
     * </summary>
     **/
    public void markUpdatedAt()
    {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * <summary>
     * Сравнивает текущий экземпляр с другим объектом на предмет равенства.
     * Реализует логику идентичности на основе уникального идентификатора и даты создания.
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

        if (obj instanceof BaseEntityObject other)
        {
            return Objects.equals(this.id, other.id) &&
                   Objects.equals(this.createdAt, other.createdAt);
        }

        return false;
    }

    /**
     * <summary>
     * Вычисляет хэш-код объекта для использования в хеш-таблицах.
     * Базируется на неизменяемых полях Id и CreatedAt для обеспечения стабильности хэша.
     * </summary>
     * @return Целочисленное значение хэш-кода.
     **/
    @Override
    public int  hashCode()
    {
        return Objects.hash(id, createdAt);
    }

    // endregion
}