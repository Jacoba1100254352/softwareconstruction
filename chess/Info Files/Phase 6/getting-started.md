# Getting Started

📁 [Starter code](../../6-gameplay/starter-code)

The Starter Code should have 2 folders, `serverTests` and `webSocketMessages`. Do the following:

1. Copy the `serverTests` folder into your project’s `src/test/passoffTests` folder. The `serverTests` folder already exists in your project, but this will add a new class named WebSocketTests to it. This class contains the pass off test cases that verify your server’s web socket interactions with clients.
1. Copy the `webSocketMessages` folder into your project’s `shared/src/starter` folder. Note that this folder is in your `shared` module. This folder contains the `UserGameCommand` and `ServerMessage` superclasses for the web socket message classes you will create.

## Dependencies

Add the following dependencies to the `client` module:

- **org.glassfish.tyrus.bundles:tyrus-standalone-client:1.15**
  - Scope: Compile
