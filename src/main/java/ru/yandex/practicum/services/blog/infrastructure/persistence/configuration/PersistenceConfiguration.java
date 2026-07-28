package ru.yandex.practicum.services.blog.infrastructure.persistence.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.yandex.practicum.services.blog.core.application.interfaces.CommentRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.ImageRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.PostRepository;
import ru.yandex.practicum.services.blog.core.application.interfaces.TagRepository;
import ru.yandex.practicum.services.blog.infrastructure.persistence.configuration.options.PersistenceOptions;
import ru.yandex.practicum.services.blog.infrastructure.persistence.repositories.CommentJdbcRepositoryImpl;
import ru.yandex.practicum.services.blog.infrastructure.persistence.repositories.ImageJdbcRepositoryImpl;
import ru.yandex.practicum.services.blog.infrastructure.persistence.repositories.PostJdbcRepositoryImpl;
import ru.yandex.practicum.services.blog.infrastructure.persistence.repositories.TagJdbcRepositoryImpl;

import javax.sql.DataSource;

/**
 * <summary>
 * Класс конфигурации уровня инфраструктуры, отвечающий за доступ к данным (Persistence).
 * Обеспечивает настройку механизмов взаимодействия с базой данных, включая создание
 * источника данных (DataSource) и инструментов выполнения SQL-запросов (JDBC).
 * Реализует логику хранения и извлечения сущностей доменной области, изолируя
 * детали реализации реляционного хранилища от бизнес-логики приложения.
 * </summary>
 **/
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
@Profile("!test")
public class PersistenceConfiguration
{
    // region Beans

    /**
     * <summary>
     * Создает и настраивает объект параметров подключения к БД.
     * Значения автоматически внедряются из application.properties.
     * </summary>
     * <return>
     * @return Заполненный объект PersistenceOptions.
     * </return>
     **/
    @Bean
    public PersistenceOptions persistenceOptions(
            @Value("${blog.datasource.driver-class-name}") final String driverClassName,
            @Value("${blog.datasource.url}") final String url,
            @Value("${blog.datasource.username}") final String username,
            @Value("${blog.datasource.password}") final String password
    )
    {
        return new PersistenceOptions(
                driverClassName,
                url,
                username,
                password
        );
    }

    /**
     * <summary>
     * Создает источник данных (DataSource) на основе PersistenceOptions.
     * </summary>
     * <param name="options">
     * Настройки подключения.
     * </param>
     * <return>
     * @return Настроенный DataSource.
     * </return>
     **/
    @Bean
    public DataSource dataSource(final PersistenceOptions options)
    {
        var dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(options.getDriverClassName());
        dataSource.setUrl(options.getUrl());
        dataSource.setUsername(options.getUsername());
        dataSource.setPassword(options.getPassword());

        return dataSource;
    }

    /**
     * <summary>
     * Инициализатор базы данных, который автоматически выполняет скрипт schema.sql
     * при запуске приложения и поднятии контекста.
     * </summary>
     **/
    @Bean
    public DataSourceInitializer dataSourceInitializer(final DataSource dataSource)
    {
        var populator = new ResourceDatabasePopulator();

        populator.addScript(new ClassPathResource("database/schema.sql"));

        populator.setContinueOnError(true);

        var initializer = new DataSourceInitializer();

        initializer.setDataSource(dataSource);

        initializer.setDatabasePopulator(populator);

        return initializer;
    }

    /**
     * <summary>
     * Создает NamedParameterJdbcTemplate на основе источника данных (DataSource).
     * </summary>
     * <param name="dataSource">
     * Источник данных.
     * </param>
     * <return>
     * @return Готовый к работе NamedParameterJdbcTemplate.
     * </return>
     **/
    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(final DataSource dataSource)
    {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * <summary>
     * Менеджер транзакций для управления фиксацией и откатом операций в БД.
     * </summary>
     **/
    @Bean
    public PlatformTransactionManager transactionManager(final DataSource dataSource)
    {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * <summary>
     * Создает бин репозитория публикаций и внедряет в него NamedParameterJdbcTemplate.
     * </summary>
     * <param name="namedParameterJdbcTemplate">
     * Настроенный шаблон JDBC.
     * </param>
     * <return>
     * @return Реализация IPostRepository.
     * </return>
     **/
    @Bean
    public PostRepository postRepository(final NamedParameterJdbcTemplate namedParameterJdbcTemplate)
    {
        return new PostJdbcRepositoryImpl(namedParameterJdbcTemplate);
    }

    /**
     * <summary>
     * Создает бин репозитория тегов и внедряет в него NamedParameterJdbcTemplate.
     * </summary>
     * <param name="namedParameterJdbcTemplate">
     * Настроенный шаблон JDBC.
     * </param>
     * <return>
     * @return Реализация ITagRepository.
     * </return>
     **/
    @Bean
    public TagRepository tagRepository(final NamedParameterJdbcTemplate namedParameterJdbcTemplate)
    {
        return new TagJdbcRepositoryImpl(namedParameterJdbcTemplate);
    }

    /**
     * <summary>
     * Создает бин репозитория тегов и внедряет в него NamedParameterJdbcTemplate.
     * </summary>
     * <param name="namedParameterJdbcTemplate">
     * Настроенный шаблон JDBC.
     * </param>
     * <return>
     * @return Реализация ITagRepository.
     * </return>
     **/
    @Bean
    public ImageRepository imageRepository(final NamedParameterJdbcTemplate namedParameterJdbcTemplate)
    {
        return new ImageJdbcRepositoryImpl(namedParameterJdbcTemplate);
    }

    /**
     * <summary>
     * Создает бин репозитория комментариев и внедряет в него NamedParameterJdbcTemplate.
     * </summary>
     * <param name="namedParameterJdbcTemplate">
     * Настроенный шаблон JDBC.
     * </param>
     * <return>
     * @return ICommentRepository ITagRepository.
     * </return>
     **/
    @Bean
    public CommentRepository commentRepository(final NamedParameterJdbcTemplate namedParameterJdbcTemplate)
    {
        return new CommentJdbcRepositoryImpl(namedParameterJdbcTemplate);
    }

    // endregion
}