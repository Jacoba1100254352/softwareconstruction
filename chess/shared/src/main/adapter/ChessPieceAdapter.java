package adapter;

import chess.*;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ChessPieceAdapter implements JsonDeserializer<ChessPiece> {
    public ChessPiece deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        // get pieceType and teamColor from jsonElement
        JsonObject obj = jsonElement.getAsJsonObject();
        String pieceType = obj.get("pieceType").getAsString();
        String teamColor = obj.get("teamColor").getAsString();

        switch (pieceType) {
            case "PAWN" -> {
                if (teamColor.equals("WHITE")) {
                    return new PawnPiece(ChessGame.TeamColor.WHITE);
                } else {
                    return new PawnPiece(ChessGame.TeamColor.BLACK);
                }
            }
            case "ROOK" -> {
                if (teamColor.equals("WHITE")) {
                    return new RookPiece(ChessGame.TeamColor.WHITE);
                } else {
                    return new RookPiece(ChessGame.TeamColor.BLACK);
                }
            }
            case "KNIGHT" -> {
                if (teamColor.equals("WHITE")) {
                    return new KnightPiece(ChessGame.TeamColor.WHITE);
                } else {
                    return new KnightPiece(ChessGame.TeamColor.BLACK);
                }
            }
            case "BISHOP" -> {
                if (teamColor.equals("WHITE")) {
                    return new BishopPiece(ChessGame.TeamColor.WHITE);
                } else {
                    return new BishopPiece(ChessGame.TeamColor.BLACK);
                }
            }
            case "QUEEN" -> {
                if (teamColor.equals("WHITE")) {
                    return new QueenPiece(ChessGame.TeamColor.WHITE);
                } else {
                    return new QueenPiece(ChessGame.TeamColor.BLACK);
                }
            }
            case "KING" -> {
                if (teamColor.equals("WHITE")) {
                    return new KingPiece(ChessGame.TeamColor.WHITE);
                } else {
                    return new KingPiece(ChessGame.TeamColor.BLACK);
                }
            }
        }
        return null;
    }
}

