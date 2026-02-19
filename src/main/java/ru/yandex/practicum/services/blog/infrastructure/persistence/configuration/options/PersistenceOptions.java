package ru.yandex.practicum.services.blog.infrastructure.persistence.configuration.options;

/**
 * <summary>
 * Класс, содержащий параметры подключения к базе данных.
 * </summary>
 **/
public final class PersistenceOptions
{
    // region Fields

    private final String driverClassName;
    private final String url;
    private final String username;
    private final String password;

    // endregion

    // region Constructors

    public PersistenceOptions(
            final String driverClassName,
            final String url,
            final String username,
            final String password
    )
    {
        this.driverClassName = driverClassName;

        this.url = url;

        this.username = username;

        this.password = password;
    }

    // endregion

    // region Properties

    public final String getDriverClassName()
    {
        return driverClassName;
    }

    public final String getUrl()
    {
        return url;
    }

    public final String getUsername()
    {
        return username;
    }

    public final String getPassword()
    {
        return password;
    }

    // endregion
}