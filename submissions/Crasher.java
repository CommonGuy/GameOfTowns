import java.util.ArrayList;
import java.util.List;

public class Crasher {
	
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("80 60 80");
			return;
		}
			new Crasher().decide(args[0].split(";"));
	}
	
	private void decide(String[] args) {
		int myId = Integer.parseInt(args[1]);
		int thisTownId = Integer.parseInt(args[2]);
		List<Town> towns = new ArrayList<>();
		
		for (int i = 3; i < args.length; i++) {
			String infos[] = args[i].split("_");
			int owner = Integer.parseInt(infos[0]);
			int id = Integer.parseInt(infos[1]);
			int knights = Integer.parseInt(infos[2]);
			int lowborns = Integer.parseInt(infos[3]);
			towns.add(new Town(owner, id, knights, lowborns));
		}
		int freeKnights = 0;
		int fewestKnights = Integer.MAX_VALUE;
		int attackId = -1;
		for (Town town : towns) {
			if (town.getOwner() != myId && town.knightCount() < fewestKnights) {
				attackId = town.getId();
				fewestKnights = town.knightCount();
			} else if (town.getId() == thisTownId) {
				freeKnights = getFreeKnights(town);
			}
		}
		if (attackId != -1 && fewestKnights < freeKnights) {
			System.out.println("A " + attackId + " " + freeKnights);
		} else {
			System.out.println("W");
		}
			
	}
	
	//knights to spare without loosing the town
	private int getFreeKnights(Town town) {
		return town.knightCount() - town.lowbornCount() / 2 - 1;
	}
	
	class Town {
		private final int ownerId;
		private final int id;
		private final int knights;
		private final int lowborns;
		
		public Town(int ownerId, int id, int knights, int lowborns) {
			this.ownerId = ownerId;
			this.id = id;
			this.knights = knights;
			this.lowborns = lowborns;
		}	
		
		public int getId() {
			return id;
		}
		
		public int getOwner() {
			return ownerId;
		}
		
		public int knightCount() {
			return knights;
		}
		
		public int lowbornCount() {
			return lowborns;
		}
		
	}	
}
