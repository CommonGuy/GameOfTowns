package players;

import controller.Player;

public class Neutral extends Player {

	public Neutral() {
		setId(-1);
	}

	@Override
	public String getCmd() {
		return "";
	}
}
