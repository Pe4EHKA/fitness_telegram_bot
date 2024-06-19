CREATE TABLE [Users] (
	[id] int IDENTITY(1,1) NOT NULL UNIQUE,
	[name] nvarchar(max),
	[telegram_id] int NOT NULL UNIQUE,
	PRIMARY KEY ([id])
);

CREATE TABLE [Instructors] (
	[id] int IDENTITY(1,1) NOT NULL UNIQUE,
	[bio] nvarchar(max),
	[phone_number] nvarchar(max) NOT NULL UNIQUE,
	PRIMARY KEY ([id])
);

CREATE TABLE [Lessons] (
	[id] int IDENTITY(1,1) NOT NULL UNIQUE,
	[choach_id] int NOT NULL UNIQUE,
	[date_time] datetime NOT NULL,
	[places] int NOT NULL,
	[occupaid_places] int NOT NULL,
	[description] nvarchar(max) NOT NULL,
	PRIMARY KEY ([id])
);

CREATE TABLE [Lessons_registrations] (
	[id] int IDENTITY(1,1) NOT NULL UNIQUE,
	[lesson_id] int NOT NULL UNIQUE,
	[user_id] int NOT NULL UNIQUE,
	PRIMARY KEY ([id])
);

CREATE TABLE [Users_role] (
	[id] int IDENTITY(1,1) NOT NULL UNIQUE,
	[user_id] int NOT NULL UNIQUE,
	[role] varbinary(max) NOT NULL,
	PRIMARY KEY ([id])
);



ALTER TABLE [Lessons] ADD CONSTRAINT [Lessons_fk1] FOREIGN KEY ([choach_id]) REFERENCES [Instructors]([id]);
ALTER TABLE [Lessons_registrations] ADD CONSTRAINT [Lessons_registrations_fk1] FOREIGN KEY ([lesson_id]) REFERENCES [Lessons]([id]);

ALTER TABLE [Lessons_registrations] ADD CONSTRAINT [Lessons_registrations_fk2] FOREIGN KEY ([user_id]) REFERENCES [Users]([id]);
ALTER TABLE [Users_role] ADD CONSTRAINT [Users_role_fk1] FOREIGN KEY ([user_id]) REFERENCES [Users]([id]);