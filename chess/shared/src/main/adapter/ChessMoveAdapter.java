package adapter;

import chess.ChessMove;
import chess.ChessMoveImpl;
import chess.ChessPosition;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ChessMoveAdapter implements JsonDeserializer<ChessMove> {
    public ChessMove deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        var builder = new GsonBuilder();
        builder.registerTypeAdapter(ChessPosition.class, new ChessPosAdapter());

        //return builder.create().fromJson(jsonElement, BoardImpl.class);
        return builder.create().fromJson(jsonElement, ChessMoveImpl.class);
    }
}
