package adapter;

import chess.ChessBoard;
import chess.ChessBoardImpl;
import chess.ChessPiece;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ChessBoardAdapter implements JsonDeserializer<ChessBoard> {
    public ChessBoard deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        var builder = new GsonBuilder();
        builder.registerTypeAdapter(ChessPiece.class, new ChessPieceAdapter());

        //return builder.create().fromJson(jsonElement, BoardImpl.class);
        return ctx.deserialize(jsonElement, ChessBoardImpl.class);
    }
}
