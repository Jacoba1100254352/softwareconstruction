package adapter;


import chess.gameplay.ChessPosition;
import chess.gameplay.ChessPositionImpl;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;


public class ChessPosAdapter implements JsonDeserializer<ChessPosition>
{
	@Override
	public ChessPosition deserialize(
			JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext
	) throws JsonParseException {
		JsonObject position = jsonElement.getAsJsonObject();
		if (position.has("row") && position.has("column")) {
			return new ChessPositionImpl(position.get("row").getAsInt(), position.get("column").getAsInt());
		}
		return jsonDeserializationContext.deserialize(jsonElement, ChessPositionImpl.class);
	}
}
