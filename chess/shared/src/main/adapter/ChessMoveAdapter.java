package adapter;


import chess.gameplay.ChessMove;
import chess.gameplay.ChessMoveImpl;
import chess.gameplay.ChessPosition;
import com.google.gson.*;

import java.lang.reflect.Type;


public class ChessMoveAdapter implements JsonDeserializer<ChessMove>
{
	@Override
	public ChessMove deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
		var builder = new GsonBuilder();
		builder.registerTypeAdapter(ChessPosition.class, new ChessPosAdapter());
		
		return builder.create().fromJson(jsonElement, ChessMoveImpl.class);
	}
}
