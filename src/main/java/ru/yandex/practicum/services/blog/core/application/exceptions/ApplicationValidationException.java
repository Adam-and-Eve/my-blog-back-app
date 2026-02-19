package ru.yandex.practicum.services.blog.core.application.exceptions;

import java.io.Serial;

/**
 * <summary>
 * Исключение уровня application-слоя, возникающее при некорректной
 * валидации входных параметров use case.
 * Расширяет возможности стандартного исключения IllegalArgumentException
 * за счет хранения имени аргумента, не прошедшего проверку.
 * Используется для сигнализации об ошибках входных данных,
 * не связанных с нарушением инвариантов предметной области.
 * </summary>
 **/
public final class ApplicationValidationException extends IllegalArgumentException
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

    public ApplicationValidationException()
    {
        super();

        this.paramName = null;
    }

    public ApplicationValidationException(String message)
    {
        super(message);

        this.paramName = null;
    }

    public ApplicationValidationException(String message, String paramName)
    {
        super(message);

        this.paramName = paramName;
    }

    public ApplicationValidationException(String message, Throwable cause)
    {
        super(message, cause);

        this.paramName = null;
    }

    public ApplicationValidationException(String message, String paramName, Throwable cause)
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
     * не прошел валидацию в входных параметров use case.
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