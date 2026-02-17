package ru.yandex.practicum.services.blog.core.domain.exceptions.base;

import java.io.Serial;

/**
 * <summary>
 * Базовое исключение предметной области.
 * Расширяет возможности стандартного исключения IllegalArgumentException
 * за счет хранения имени некорректного параметра.
 * </summary>
 **/
public abstract class BaseDomainException extends IllegalArgumentException
{
    // region Fields

    /**
     * Уникальный идентификатор версии сериализованного класса.
     **/
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Имя некорректного параметра.
     **/
    private final String paramName;

    // endregion

    // region Constructors

    public BaseDomainException()
    {
        super();

        this.paramName = null;
    }

    public BaseDomainException(String message)
    {
        super(message);

        this.paramName = null;
    }

    public BaseDomainException(String message, String paramName)
    {
        super(message);

        this.paramName = paramName;
    }

    public BaseDomainException(String message, Throwable cause)
    {
        super(message, cause);

        this.paramName = null;
    }

    public BaseDomainException(String message, String paramName, Throwable cause)
    {
        super(message, cause);

        this.paramName = paramName;
    }

    // endregion

    // region Properties

    /**
     * <summary>
     * Возвращает имя параметра, вызвавшего исключение.
     * Позволяет идентифицировать конкретный входной аргумент, который
     * не прошел валидацию в бизнес-логике.
     * </summary>
     * <returns>
     * Имя некорректного параметра или null, если имя не было указано.
     * </returns>
     **/
    public String getParamName()
    {
        return this.paramName;
    }

    // endregion

    // region Methods



    // endregion
}