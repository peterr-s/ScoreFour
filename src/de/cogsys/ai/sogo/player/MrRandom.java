package de.cogsys.ai.sogo.player;

import java.util.List;
import java.util.Random;

import de.cogsys.ai.sogo.control.SogoGameConsole;
import de.cogsys.ai.sogo.game.SogoGame.Player;
import de.cogsys.ai.sogo.game.SogoMove;

public class MrRandom implements SogoPlayer {

	Random r;
	
	public MrRandom(long seed) {
		r = new Random(seed);
	}
	
	public MrRandom() {
		r = new Random();
	}
	
	@Override
	public void initialize(Player p) {}

	@Override
	public void generateNextMove(SogoGameConsole c) {
		List<SogoMove> moves = c.getGame().generateValidMoves();
		c.updateMove(moves.get(r.nextInt(moves.size())));
	}

}
