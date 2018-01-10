package de.cogsys.ai.sogo.player;

import de.cogsys.ai.sogo.control.SogoGameConsole;
import de.cogsys.ai.sogo.game.SogoGame;
import de.cogsys.ai.sogo.game.SogoGame.Player;
import de.cogsys.ai.sogo.game.SogoMove;
import de.cogsys.ai.sogo.gui.SogoGamePanelListener;

public class GuiPlayer implements SogoPlayer, SogoGamePanelListener {
	
	private boolean active = false;
	private int i;
	private int j;

    @Override
    public void clickedCell(final int i, final int j) {
        if (!this.active) return;
        
        synchronized (this) {
            
            this.i = i;
            this.j = j;
            
            this.notify();
        };
    }

	@Override
	public void initialize(Player p) {}

	@Override
	public void generateNextMove(SogoGameConsole c) {
        final SogoGame game = c.getGame();
        SogoMove move = null;
        
        synchronized (this) {
        	
        	active = true;

            try {
            	while (move == null) {
                    synchronized (this) {
                        wait();
                    }
                    if (game.isValidMove(new SogoMove(i, j))) {
                    	move = new SogoMove(i, j);
                    	active = false;
                    }
            	}
            } catch (InterruptedException e) {}
        }
        c.updateMove(move);
	}

}
