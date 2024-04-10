package adapter;


import chess.gameplay.ChessPosition;
import chess.gameplay.ChessPositionImpl;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;


public class ChessPosAdapter implements JsonDeserializer<ChessPosition>
{
	@Override
	public ChessPosition deserialize(
			JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext
	) throws JsonParseException {
		return jsonDeserializationContext.deserialize(jsonElement, ChessPositionImpl.class);
	}
}
