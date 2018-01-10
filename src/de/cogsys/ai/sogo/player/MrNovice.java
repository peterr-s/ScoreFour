package de.cogsys.ai.sogo.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.cogsys.ai.sogo.control.SogoGameConsole;
import de.cogsys.ai.sogo.game.SogoGame;
import de.cogsys.ai.sogo.game.SogoGame.Player;
import de.cogsys.ai.sogo.game.SogoMove;

public class MrNovice implements SogoPlayer {

	private static final int DEFAULT_DEPTH = 4;

	private Random rnd;
	private int depth;

	public MrNovice(final long seed, final int depth) {
		this.rnd = new Random(seed);
		this.depth = depth;
	}

	public MrNovice(final int depth) {
		this(System.currentTimeMillis(), depth);
	}

	public MrNovice() {
		this(DEFAULT_DEPTH);
	}

	@Override
	public void initialize(Player p) {
	}

	private static final double win_p = 1000;
	private static final double triple_p = 20;
	private static final double double_p = 2;
	private static final double single_p = 1;

	private static double evaluateGame(final SogoGame g) {
		List<Player[]> lines = g.getLines();
		double res = 0;
		for (Player[] l : lines) {
			int self = SogoGame.countLine(l, g.getCurrentPlayer());
			int other = SogoGame.countLine(l, g.getOtherPlayer());
			if (other == 0) {
				switch (self) {
				case 4:
					return win_p;
				case 3:
					res += triple_p;
					break;
				case 2:
					res += double_p;
					break;
				case 1:
					res += single_p;
					break;
				default:
					break;
				}
			}
			if (self == 0) {
				switch (other) {
				case 4:
					return -win_p;
				case 3:
					res -= triple_p;
					break;
				case 2:
					res -= double_p;
					break;
				case 1:
					res -= single_p;
					break;
				default:
					break;
				}
			}
		}
		return res;
	}

	@Override
	public void generateNextMove(final SogoGameConsole c) {
		final SogoGame g = c.getGame();
		final List<SogoMove> moves = g.generateValidMoves();
		final List<SogoMove> bestmoves = new ArrayList<SogoMove>();

		double maxscore = Double.NEGATIVE_INFINITY;

		for (SogoMove m : moves) {
			final double score = -negamax(g.performMove(m), depth);

			if (score > maxscore) {
				bestmoves.clear();
				bestmoves.add(m);
				maxscore = score;
			} else if (score == maxscore) {
				bestmoves.add(m);
			}
		}

		c.updateMove(bestmoves.get(rnd.nextInt(bestmoves.size())));
	}
	
	/**
	 * MrNovice uses a different way of implementing MiniMax search called NegaMax
	 * which uses only one function to handle the tasks of MIN and MAX. This works
	 * only if the evaluation function is symmetric, i.e. from the other player's
	 * perspective the score is negated.
	 * 
	 * If you want to base your agent on this NegaMax agent, be very careful when
	 * implementing Alpha-Beta pruning with how you pass along the alpha and beta
	 * values.
	 */
	private double negamax(final SogoGame g, final int depth) {
		if ((depth <= 0) || g.ends()) {
			return evaluateGame(g);
		}

		final List<SogoMove> moves = g.generateValidMoves();
		double maxscore = Double.NEGATIVE_INFINITY;

		for (SogoMove m : moves) {
			final double score = -negamax(g.performMove(m), depth - 1);

			if (score > maxscore) {
				maxscore = score;
			}
		}

		return maxscore;
	}

}
