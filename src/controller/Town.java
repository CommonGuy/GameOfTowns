package controller;

import java.util.Scanner;

public class Town {
	private final static int DEFENSE_COOLDOWN = 3;
	private final int id;
	private Player owner;
	private int knights;
	private final int lowborns;
	private int defenseBonusCooldown = 0;
	
	public Town(int id, Player owner, int knights, int lowborns) {
		this.id = id;
		this.owner = owner;
		this.knights = knights;
		this.lowborns = lowborns;
	}
	
	public void setOwner(Player newOwner) {
		owner = newOwner;
	}
	
	public Player getOwner() {
		return owner;
	}
	
	public int getKnights() {
		return knights;
	}

	public void addKnights(int delta) {
		this.knights += delta;
	}
	
	public void setKnights(int knights) {
		this.knights = knights;
	}

	public int getLowborns() {
		return lowborns;
	}

	public int getId() {
		return id;
	}
	
	public boolean hasDefenseBonus() {
		return defenseBonusCooldown == 0;
	}
	
	public void setDefenseCooldown() {
		defenseBonusCooldown = DEFENSE_COOLDOWN;
	}
	
	public void reduceDefenseCooldown() {
		if (defenseBonusCooldown > 0) {
			defenseBonusCooldown--;
		}
	}
	
	public String getCommand(String args) throws Exception {
		//neutral player
		if ("".equals(owner.getCmd())) {
			return "W";
		}
		Process proc = null;
		Scanner stdin = null;
		try {
			proc = Runtime.getRuntime().exec(owner.getCmd() + " " + args);
			stdin = new Scanner(proc.getInputStream());
			StringBuilder response = new StringBuilder();
			while (stdin.hasNext()) {
				response.append(stdin.next()).append(' ');
			}
			return response.toString();	
		} finally {
			if (stdin != null) stdin.close();
			if (proc != null) proc.destroy();
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + knights;
		result = prime * result + lowborns;
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other != null && other instanceof Town) {
			return getId() == ((Town) other).getId();
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Id: " + id + " Owner: " + owner.getDisplayName() + " Knights: " + knights + " Lowborns: " + lowborns;
	}
}
