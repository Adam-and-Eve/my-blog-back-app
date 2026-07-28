IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'dbo.Posts') AND type in (N'U'))
BEGIN
    CREATE TABLE dbo.Posts
    (
        Id BIGINT PRIMARY KEY IDENTITY(1,1),
        Title NVARCHAR(512) NOT NULL,
        Text NVARCHAR(MAX) NOT NULL,
        LikesCount BIGINT NOT NULL DEFAULT 0,
        CreatedAt DATETIMEOFFSET NOT NULL,
        UpdatedAt DATETIMEOFFSET NOT NULL
    )
END;

IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'dbo.Tags') AND type in (N'U'))
BEGIN
    CREATE TABLE dbo.Tags
    (
        Id BIGINT PRIMARY KEY IDENTITY(1,1),
        Name NVARCHAR(512) NOT NULL
    )
END;

IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'dbo.PostTags') AND type in (N'U'))
BEGIN
    CREATE TABLE dbo.PostTags
    (
        PostId BIGINT NOT NULL,
        TagId BIGINT NOT NULL,
        PRIMARY KEY (PostId, TagId),
        FOREIGN KEY (PostId) REFERENCES dbo.Posts(Id) ON DELETE CASCADE,
        FOREIGN KEY (TagId) REFERENCES dbo.Tags(Id) ON DELETE CASCADE
    )
END;

IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'dbo.PostImages') AND type in (N'U'))
BEGIN
    CREATE TABLE dbo.PostImages
    (
        PostId BIGINT PRIMARY KEY NOT NULL,
        Content VARBINARY(MAX) NOT NULL,

        FOREIGN KEY (PostId) REFERENCES dbo.Posts (Id) ON DELETE CASCADE
    )
END;

IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'dbo.PostComments') AND type in (N'U'))
BEGIN
    CREATE TABLE dbo.PostComments
    (
        Id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL ,
        PostId BIGINT FOREIGN KEY (PostId) REFERENCES dbo.Posts(Id) ON DELETE CASCADE NOT NULL,
        Text NVARCHAR(MAX) NOT NULL,
        CreatedAt DATETIMEOFFSET NOT NULL,
        UpdatedAt DATETIMEOFFSET NOT NULL
    )
END;

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = N'IX_PostComments_PostId' AND object_id = OBJECT_ID(N'dbo.PostComments'))
BEGIN
    CREATE NONCLUSTERED INDEX IX_PostComments_PostId ON dbo.PostComments (PostId)
END;

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = N'UQ_Tags_Name' AND object_id = OBJECT_ID(N'dbo.Tags'))
BEGIN
    CREATE UNIQUE NONCLUSTERED INDEX UQ_Tags_Name ON dbo.Tags (Name)
END;