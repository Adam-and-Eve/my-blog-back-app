# my-blog-back-app

**Бэкенд приложения-блога**

---

## 🚀 О проекте

Бэкенд-приложение для платформы блога, разработанное на **Java 21** с использованием **Spring Framework**.

Проект следует принципам **Clean Architecture** / многослойной архитектуры:
- **Presentation** → **Application** → **Domain** → **Infrastructure**

Приложение собирается в **WAR**-архив и деплоится в сервлет-контейнер (Tomcat / Jetty).

---

## 🛠 Технологический стек

- **Язык:** Java 21
- **Фреймворк:** Spring Framework (Web MVC, JDBC)
- **Сборка:** Gradle (Kotlin DSL)
- **База данных:** Microsoft SQL Server
- **Деплой:** WAR + Tomcat (Docker)
- **Тестирование:** JUnit 5, Spring Test, Mockito
- **Дополнительно:** Jackson

---

## 📂 Структура проекта

```bash
my-blog-back-app/
├── src/
│   ├── main/
│   │   ├── java/ru/yandex/practicum/services/blog/
│   │   │   ├── core/
│   │   │   │   ├── application/          # DTOs, Services, Interfaces
│   │   │   │   └── domain/               # Entity Objects, Value Objects, Exceptions
│   │   │   ├── infrastructure/persistence/
│   │   │   │   ├── repositories/
│   │   │   │   ├── mappers/
│   │   │   │   ├── projections/
│   │   │   │   └── configuration/
│   │   │   └── presentation/rest/
│   │   │       ├── controllers/
│   │   │       └── configuration/
│   │   └── resources/
│   │       ├── database/schema.sql
│   │       ├── application.properties
│   │       └── WEB-INF/web.xml
│   └── test/java/...                     # Unit + Integration тесты
├── build.gradle.kts
├── Dockerfile
├── application.properties.env
├── .gitignore
└── gradle.properties
```

---

## ⚙️ Конфигурация и запуск

### Переменные окружения (application.properties.env)

```bash
blog.datasource.driver=class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
blog.datasource.url=jdbc:sqlserver://host.docker.internal:1433;databaseName=BlogDb;encrypt=true;trustServerCertificate=true;
blog.datasource.username=login
blog.datasource.password=password
```

### Запуск через Docker

```bash
docker build -t my-blog-back-app .
```

```bash
docker run --name my-blog-back-service -p 8080:8080 --env-file application.properties.env --rm my-blog-back-app
```

---

## 🏗 Сборка проекта

**# Сборка WAR-файла**

```bash
./gradlew war
```

**# Запуск через Gretty (Jetty)**

```bash
./gradlew appRun
```

---

## 📋 Основные возможности API

PostController (/api/posts)

| Метод   | Эндпоинт                                      | Описание                          |
|---------|-----------------------------------------------|-----------------------------------|
| `GET`   | `/posts?search=...&pageNumber=...&pageSize=...` | Постраничный поиск постов        |
| `GET`   | `/posts/{id}`                                 | Получение поста                   |
| `POST`  | `/posts`                                      | Создание поста                    |
| `PUT`   | `/posts/{id}`                                 | Обновление поста                  |
| `DELETE`| `/posts/{id}`                                 | Удаление поста                    |
| `POST`  | `/posts/{id}/likes`                           | Поставить лайк                    |
| `GET`   | `/posts/{id}/image`                           | Получение изображения             |
| `PUT`   | `/posts/{id}/image`                           | Обновление изображения            |
| `GET`   | `/posts/{id}/comments`                        | Получение комментариев            |
| `POST`  | `/posts/{id}/comments`                        | Добавление комментария            |
| `PUT`   | `/posts/{postId}/comments/{commentId}`        | Обновление комментария            |
| `DELETE`| `/posts/{postId}/comments/{commentId}`        | Удаление комментария              |

---

## 🗄 Схема базы данных

Основные таблицы:
- `Posts`
- `PostImages`
- `Tags` + `PostTags` (многие-ко-многим)
- `PostComments`

Скрипт инициализации: `src/main/resources/database/schema.sql`

---

## 🧪 Тестирование

```bash
./gradlew test
```

- `Unit-тесты сервисов`
- `Integration-тесты репозиториев (*IT)`
- `Integration-тесты контроллеров`

---

## 📌 Особенности архитектуры

- **Чёткое разделение по DDD (Entity Objects, Value Objects)**
- **Использование Projection для оптимизации запросов**
- **RowMapper для работы с Spring JDBC**
- **Интерфейсы для всех важных компонентов**
- **Центральная конфигурация через RestConfiguration**
- **Поддержка multipart-запросов для изображений**

---

## 🔧 Как внести изменения

- **Создайте новую ветку: git checkout -b feature/название**
- **Внесите изменения**
- **Запустите тесты: ./gradlew test** 
- **Соберите проект: ./gradlew war**
- **Создайте Pull Request**

---

## ⚠️ Ответы на популярные вопросы

- **Не подключается к БД в Docker → используйте host.docker.internal**
- **Gretty не запускается → проверьте, свободен ли порт 8080**
- **Проблемы со сборкой → выполните ./gradlew clean war**
- **SQL Server → убедитесь, что база BlogDb существует и у пользователя есть права**
