package controller;

public class Command {
	private final Town source;
	private final int townId;
	private int knightCount;
	
	public Command(String string, Town source) throws Exception {
		String[] splitted = string.split(" ");
		townId = Integer.parseInt(splitted[1]);
		knightCount = Integer.parseInt(splitted[2]);
		this.source = source;
		if (knightCount > source.getKnights() || knightCount < 1) {
			throw new IllegalArgumentException();
		}
	}
	
	public int getKnightCount() {
		return knightCount;
	}
	
	public void addKnights(int knights) {
		knightCount += knights;
	}
	
	public int getDestinationId() {
		return townId;
	}
	
	public Town getSource() {
		return source;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + knightCount;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + townId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Command other = (Command) obj;
		if (knightCount != other.knightCount)
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (townId != other.townId)
			return false;
		return true;
	}	
}
