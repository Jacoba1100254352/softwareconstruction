package blindchess.model;


public enum GameMode
{
	HISTORY,
	NO_HISTORY;

	public static GameMode fromString(String value) {
		if (value == null) {
			throw new IllegalArgumentException("Game mode is required.");
		}
		String normalized = value.trim().replace('-', '_').replace(' ', '_').toUpperCase();
		return switch (normalized) {
			case "HISTORY" -> HISTORY;
			case "NO_HISTORY" -> NO_HISTORY;
			default -> throw new IllegalArgumentException("Unsupported game mode: " + value);
		};
	}
}
