package responses;


import models.Game;

import java.util.Collection;


/**
 * Represents the response containing a list of all games.
 *
 * @param games   A list of games.
 * @param message A message providing details or an error description.
 * @param success The game listing was successful
 */
public record ListGamesResponse(Collection<Game> games, String message, boolean success) implements Response
{
}
