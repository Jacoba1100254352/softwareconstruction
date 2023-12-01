package handlers;

import chess.InvalidMoveException;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import models.Game;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {
    private final Map<Session, String> userSessions = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("New WebSocket connection: " + session.getRemoteAddress().getAddress());
        userSessions.put(session, null);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("WebSocket closed: " + statusCode + ", Reason: " + reason);
        userSessions.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("Message received: " + message);
        processMessage(session, message);
    }

    private void processMessage(Session session, String message) {
        try {
            switch (gson.fromJson(message, UserGameCommand.class).getCommandType()) {
                case JOIN_PLAYER -> handleJoinPlayer(session, gson.fromJson(message, JoinPlayerCommand.class));
                case JOIN_OBSERVER -> handleJoinObserver(session, gson.fromJson(message, JoinObserverCommand.class));
                case MAKE_MOVE -> handleMakeMove(session, gson.fromJson(message, MakeMoveCommand.class));
                case LEAVE -> handleLeave(session, gson.fromJson(message, LeaveCommand.class));
                case RESIGN -> handleResign(session, gson.fromJson(message, ResignCommand.class));
                default -> sendErrorMessage(session, "Unknown command type");
            }
        } catch (Exception e) {
            sendErrorMessage(session, "Invalid message format");
        }
    }


    private void handleJoinPlayer(Session session, JoinPlayerCommand command) {
        try {
            GameDAO gameDAO = new GameDAO();
            Game game = gameDAO.findGameById(command.getGameID());
            if (game != null) {
                gameDAO.claimSpot(command.getGameID(), userSessions.get(session), command.getPlayerColor());
                sendMessage(session, new NotificationMessage("Player joined: " + command.getPlayerColor()));
            } else {
                sendErrorMessage(session, "Game not found");
            }
        } catch (DataAccessException e) {
            sendErrorMessage(session, "Database error: " + e.getMessage());
        }
    }


    private void handleJoinObserver(Session session, JoinObserverCommand command) {
        try {
            GameDAO gameDAO = new GameDAO();
            Game game = gameDAO.findGameById(command.getGameID());
            if (game != null) {
                // FIXME: Assuming there's a method in Game to add an observer
                // game.getGame().addObserver(session);
                sendMessage(session, new NotificationMessage("Observer joined game ID: " + command.getGameID()));
            } else {
                sendErrorMessage(session, "Game not found");
            }
        } catch (DataAccessException e) {
            sendErrorMessage(session, "Database error: " + e.getMessage());
        }
    }

    private void handleMakeMove(Session session, MakeMoveCommand command) {
        try {
            GameDAO gameDAO = new GameDAO();
            Game game = gameDAO.findGameById(command.getGameID());
            if (game != null) {
                try {
                    game.getGame().makeMove(command.getMove());
                    broadcastToAllClients(new LoadGameMessage("Updated game state for game ID: " + command.getGameID()));
                } catch (InvalidMoveException e) {
                    sendErrorMessage(session, "Invalid move: " + e.getMessage());
                }
            } else {
                sendErrorMessage(session, "Game not found");
            }
        } catch (DataAccessException e) {
            sendErrorMessage(session, "Error processing move: " + e.getMessage());
        }
    }

    private void handleLeave(Session session, LeaveCommand command) {
        try {
            GameDAO gameDAO = new GameDAO();
            Game game = gameDAO.findGameById(command.getGameID());
            if (game != null) {
                // FIXME: Assuming there's a method in Game to remove a player
                //game.getGame().removePlayer(session);
                sendMessage(session, new NotificationMessage("Player left game ID: " + command.getGameID()));
            } else {
                sendErrorMessage(session, "Game not found");
            }
        } catch (DataAccessException e) {
            sendErrorMessage(session, "Database error: " + e.getMessage());
        }
    }


    private void handleResign(Session session, ResignCommand command) {
        // TODO: Add your game logic for a player resigning from a game

        // Psuedocode:
        // if (removePlayerFromGame(command.getGameID(), command.getPlayerColor())) {
        //     sendMessage(session, new NotificationMessage("Player resigned: " + command.getPlayerColor()));
        // } else {
        //     sendErrorMessage(session, "Could not remove player from game");
        // }
    }

    private void sendErrorMessage(Session session, String errorMessage) {
        try {
            ErrorMessage errorMsg = new ErrorMessage(errorMessage);
            String jsonErrorMsg = gson.toJson(errorMsg);
            session.getRemote().sendString(jsonErrorMsg);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    private void sendMessage(Session session, ServerMessage message) {
        try {
            String jsonMsg = gson.toJson(message);
            session.getRemote().sendString(jsonMsg);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    private void broadcastToAllClients(ServerMessage message) {
        String jsonMsg = gson.toJson(message);
        userSessions.keySet().forEach(session -> {
            try {
                session.getRemote().sendString(jsonMsg);
            } catch (IOException e) {
                System.err.println("Error broadcasting message: " + e.getMessage());
            }
        });
    }
}
