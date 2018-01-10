package de.cogsys.ai.sogo.control;

import de.cogsys.ai.sogo.game.SogoGame;
import de.cogsys.ai.sogo.game.SogoMove;

/**
 * @author Sebastian Otte
 */
public interface SogoGameConsole {
	
    /**
     * Returns the current game situation.
     */
	public SogoGame getGame();
	
    /**
     * Returns the remaining time for making a move in milliseconds. 
     */
	public long getTimeLeft();
	
    /**
     * Updates the current move that will be performed after timeout
     * or leaving the generateNextMove method of a player. This method
     * can be called multiple times during one turn and only the call
     * will be considered. 
     */
	public void updateMove(final SogoMove move);
}