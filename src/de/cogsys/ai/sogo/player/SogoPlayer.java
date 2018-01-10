package de.cogsys.ai.sogo.player;

import de.cogsys.ai.sogo.control.SogoGameConsole;
import de.cogsys.ai.sogo.game.SogoGame.Player;

public interface SogoPlayer {
	
	/**
	 * Lets the agent know which player it is supposed to represent. May
	 * not be necessary since the agent can always call getCurrentPlayer() 
	 * when asked for the next move
	 */
	public void initialize(Player p);
	
	public void generateNextMove(final SogoGameConsole c);

}
