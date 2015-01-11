package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import players.*;


public class Game {
	private final Player[] players = {new Sleeper(), new Crasher()};
	public static final int TOWNS_PER_PLAYER = 3;
	private static final int MAX_POPULATION = 100;
	private static final int KNIGHT_PRODUCING_ROUND = 3;
	private static final float DEFENSE_BONUS = 1.2f;
	private static final int ROUNDS = 100;
	private static final boolean DEBUG = false;
	private static final boolean GAME_MESSAGES = false;
	private final List<Town> towns = new ArrayList<>();
	private final Player neutralPlayer = new Neutral();
	private int round = 0;
	
	public Game() {
		for (int i = 0; i < players.length; i++) {
			players[i].setId(i);
		}
	}
	
	public static void main(String... args) {
		new Game().run();
	}
	
	public void run() {
		if (GAME_MESSAGES) System.out.println("Starting a new game...");
		initialize();
		if (GAME_MESSAGES) System.out.println("Game begins.");

		for (round = 1; round <= ROUNDS; round++) {
			if (GAME_MESSAGES) {
				System.out.println("****** ROUND " + round + " ******");
			}
			if (!makeTurns()) break; //break if only one player left
		}
		printResults();
	}
	
	private void initialize() {		
		for (int i = 0; i < players.length; i++) {
			try {
				int startingKnights[] = players[i].getStartingKnights();

				for (int j = 0; j < startingKnights.length; j++) {
					int knightCount = startingKnights[j];
					if (GAME_MESSAGES) System.out.println(knightCount + " by " + players[i].getDisplayName());

					if (knightCount >= 0 && knightCount <= MAX_POPULATION) {
						towns.add(new Town(i * 3 + j, players[i], knightCount, MAX_POPULATION - knightCount));
					}
				}
				continue; //otherwise, towns belongs to neutral player
			} catch (Exception e) {
				if (DEBUG) {
					System.out.println("Exception in initialize() by " + players[i].getDisplayName());
					e.printStackTrace();
				}
			}
			//exception occured or too many/few knights
			for (int j = 0; j < 3; j++) {
				towns.add(new Town(i * 3 + j, neutralPlayer, 50, 50));
			}
		}
		
		Collections.shuffle(towns);
	}	
	
	private boolean makeTurns() {
		reduceDefenseCooldowns();
		produceKnights();
		
		for (Town town : towns) {
			Player owner = town.getOwner();
			try {
				String response = town.getCommand(round + ";" + owner.getId() + ";" + town.getId() + generateArgs());
				switch (response.charAt(0)) {
					case 'A': executeAttack(new Command(response, town)); break;
					case 'S': executeSupport(new Command(response, town)); break;
				}
			} catch (Exception e) {
				if (DEBUG) {
					System.out.println("Exception in makeTurns() by " + owner.getDisplayName());
					e.printStackTrace();
				}
			}
			if (onePlayerLeft()) return false;
		}
		tryRebellion();
		return true;
	}
	
	private boolean onePlayerLeft() {
		Player player = null;
		for (Town town : towns) {
			if (player == null) {
				player = town.getOwner();
			} else if (!player.equals(town.getOwner())) {
				return false;
			}
		}
		return true;
	}
	
	private void printResults() {
		int townCount = 0;
		int knightCount = 0;
		int lowbornCount = 0;
		
		List<Score> scores = new ArrayList<>();
		System.out.println("********** FINISH **********");
		for (Player player : players) {
			townCount = 0;
			knightCount = 0;
			lowbornCount = 0;
			for (Town town : towns) {
				if (player.equals(town.getOwner())) {
					townCount++;
					knightCount += town.getKnights();
					lowbornCount += town.getLowborns();
				}
			}
			scores.add(new Score(player, townCount, knightCount, lowbornCount));
		}
		
		//neutral player
		townCount = 0;
		knightCount = 0;
		lowbornCount = 0;
		for (Town town : towns) {
			if (neutralPlayer.equals(town.getOwner())) {
				townCount++;
				knightCount += town.getKnights();
				lowbornCount += town.getLowborns();
			}
		}
		scores.add(new Score(neutralPlayer, townCount, knightCount, lowbornCount));
	
		//sort descending
		Collections.sort(scores, Collections.reverseOrder());
		
		for (int i = 0; i < scores.size(); i++) {
			Score score = scores.get(i);
			System.out.println(i+1 + ". " + score.print());
		}
	}
	
	private void reduceDefenseCooldowns() {
		for (Town town : towns) {
			town.reduceDefenseCooldown();
		}
	}
	
	private void produceKnights() {
		if (round % KNIGHT_PRODUCING_ROUND == 0) {
			for (Town town : towns) {
				town.addKnights(town.getLowborns() / 2);
			}
			if (GAME_MESSAGES) {
				System.out.println("Knights produced.");
			}
		}
	}
	
	private String generateArgs() {
		StringBuilder builder = new StringBuilder();
		//PlayerId TownId knights lowborns
		for (Town town : towns) {
			builder.append(';');
			builder.append(town.getOwner().getId()).append('_');
			builder.append(town.getId()).append('_');
			builder.append(town.getKnights()).append('_');
			builder.append(town.getLowborns());
		}
		return builder.toString();
	}
	
	private void executeSupport(Command support) {
		Town source = support.getSource();
		source.addKnights(-support.getKnightCount());
		for (Town town : towns) {
			if (town.getId() == support.getDestinationId()) {
				if (GAME_MESSAGES) {
					System.out.println(source.getOwner().getDisplayName() + 
							" (" + support.getKnightCount() + ") supports " + 
							town.getOwner().getDisplayName() + " (" + 
							town.getKnights() + ")");
				}
				town.addKnights(support.getKnightCount());
				return;
			}
		}
	}
	
	private void executeAttack(Command attack) {
		
		attack.getSource().addKnights(-attack.getKnightCount());
		
		//execute attacks
		for (Town town : towns) {
			if (town.getId() == attack.getDestinationId()) {				
				//add knights for defense bonus
				Player oldOwner = town.getOwner();
				int oldKnights = town.getKnights();
				if (town.hasDefenseBonus()) {
					town.setKnights((int) (oldKnights * DEFENSE_BONUS));;
				}
			
				Town source = attack.getSource();
				int attackingKnights = attack.getKnightCount();
				if (GAME_MESSAGES) {
					System.out.println(source.getOwner().getDisplayName() + 
							" (" + attackingKnights + ") attacks " + 
							town.getOwner().getDisplayName() + " (" + 
							town.getKnights() + ")" + 
							(town.hasDefenseBonus() ? " *defense bonus" : ""));
				}
				//capture
				if (attackingKnights > town.getKnights()) {
					if (GAME_MESSAGES) {
						System.out.println(source.getOwner().getDisplayName() + 
								" captured a town from " + 
								town.getOwner().getDisplayName());
					}
					attackingKnights -= town.getKnights();
					town.setOwner(source.getOwner());
					town.setKnights(attackingKnights);
					town.setDefenseCooldown();
				} else {					
					town.addKnights(-attackingKnights);
				}
				
				//remove knights (if the ones from defense bonus weren't killed)
				if (town.getOwner().equals(oldOwner) && oldKnights < town.getKnights()) {
					town.setKnights(oldKnights);
				}
				
				return;
			}
		}
	}
	
	private void tryRebellion() {
		for (Town town : towns) {
			//don't kill knights if neutral player holds the town
			if (neutralPlayer.equals(town.getOwner())) {
				continue;
			}
			if (town.getKnights() < Math.round(((float)town.getLowborns()) / 2)) {
				if (GAME_MESSAGES) {
					System.out.println(town.getOwner().getDisplayName() + " lost a town due rebellion");
				}
				town.setOwner(neutralPlayer);
				town.setKnights(0);
			}
		}
	}
}
