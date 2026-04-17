# Blind Chess Bot

This is a separate copy of the original `chess` project adapted into a text-only single-player blind chess game.

## What changed

- No board or graphical interface.
- One human player vs. a built-in local bot.
- The player enters moves in chess notation such as `e4`, `Nf3`, `exd5`, `O-O`, or `e8=Q`.
- Two play modes are supported:
  - `history`: the full move list is shown.
  - `no-history`: only the most recent notation is shown.
- The server owns the game state and immediately replies with the bot's move in notation.

The bot is local and does not require a Stockfish or external API key.

## Run

Start the server in one terminal:

```bash
cd blind-chess-bot
./mvnw -q -DskipTests exec:java -Dexec.mainClass=blindchess.BlindChessServer
```

Start the client in another terminal:

```bash
cd blind-chess-bot
./mvnw -q -DskipTests exec:java -Dexec.mainClass=blindchess.BlindChessClient
```

## Test

```bash
cd blind-chess-bot
./mvnw test
```

The first wrapper run downloads a local Maven distribution automatically, so a separate `mvn` install is not required.
