# ServerDatabaseController

ServerDatabaseController is a core utility plugin designed to handle database connections and persistence layers for Minecraft server environments. By leveraging **Hibernate ORM**, it provides a robust and scalable way to manage data without writing boilerplate SQL.

## Features
* Centralized Database Management.
* ORM support via Hibernate.
* Shared SessionFactory for external plugins.

## Usage
To interact with the database, you can access the main controller of this plugin. The primary way to handle data is through the Hibernate SessionFactory.

You can obtain the session factory using:
`ServerDatabaseController.getSessionFactory()`

Once you have the factory, you can open a new session to perform database operations:
`openSession()`

Ensure that you manage your sessions correctly by closing them after your transactions are complete to prevent memory leaks.

## Dependency Requirement
If you are developing a plugin that depends on ServerDatabaseController, you **must** add it to your `plugin.yml` file. Failure to do so will result in `ClassNotFoundException` or initialization errors, as the server needs to load this plugin before yours.

Add the following to your `plugin.yml`:
```yaml
depend: [ServerDatabaseController]
```

## Models:

Pre-configured database models and entities are located at the following path within the source:

[src/main/java/org/prag/mc/plugins/serverDatabaseController/Models/](./src/main/java/org/prag/mc/plugins/serverDatabaseController/Models/)

Refer to these models when performing queries or extending the database schema.

## Licence
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.