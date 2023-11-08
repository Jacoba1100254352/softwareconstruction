package dataAccess;

import chess.*;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Map;

class ChessBoardTypeAdapter extends TypeAdapter<ChessBoard> {

    @Override
    public void write(JsonWriter out, ChessBoard value) throws IOException {
        // Return if value is null
        if (value == null) {
            out.nullValue();
            return;
        }

        // Serialize the board's state without casting to ChessBoardImpl or ChessPieceImpl
        out.beginObject();
        for (Map.Entry<ChessPosition, ChessPiece> entry : ((ChessBoardImpl) value).getBoard().entrySet()) {
            ChessPosition pos = entry.getKey();
            ChessPiece piece = entry.getValue();
            String position = pos.getRow() + ":" + pos.getCol();
            String pieceString = piece.teamColor().name() + ":" + piece.getPieceType().name();
            out.name(position).value(pieceString);
        }
        out.endObject();
    }

    @Override
    public ChessBoard read(JsonReader in) throws IOException {
        ChessBoardImpl impl = new ChessBoardImpl();

        in.beginObject();
        while (in.hasNext()) {
            String position = in.nextName();
            String pieceString = in.nextString();
            ChessPosition pos = parsePosition(position);
            ChessPiece piece = parsePiece(pieceString);
            impl.addPiece(pos, piece);
        }
        in.endObject();

        return impl;
    }

    private ChessPosition parsePosition(String position) {
        String[] parts = position.split(":");

        int row = Integer.parseInt(parts[0]);
        int column = Integer.parseInt(parts[1]);

        return new ChessPositionImpl(row, column);
    }

    private ChessPiece parsePiece(String pieceString) {
        String[] parts = pieceString.split(":");

        ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(parts[0]);
        ChessPiece.PieceType type = ChessPiece.PieceType.valueOf(parts[1]);

        return new ChessPieceImpl(color, type);
    }
}