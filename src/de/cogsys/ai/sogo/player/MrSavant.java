package de.cogsys.ai.sogo.player;

import java.util.List;

import de.cogsys.ai.sogo.control.SogoGameConsole;
import de.cogsys.ai.sogo.game.SogoGame;
import de.cogsys.ai.sogo.game.SogoGame.Player;
import de.cogsys.ai.sogo.game.SogoMove;

/**
 * THIS IS NOT THE ASSIGNMENT SUBMISSION
 * 
 * TEST AGENT ONLY
 * 
 * @author Peter
 *
 */
public class MrSavant implements SogoPlayer
{
	// roles are determined at creation for efficiency
	Player me, opponent;
	
	private int depth;
	public MrSavant(int depth)
	{
		this.depth = depth;
	}
	
	@Override
	public void initialize(Player p)
	{
		me = p;
		opponent = me == Player.P1 ? Player.P2 : Player.P1;
	}
	
	@Override
	public void generateNextMove(SogoGameConsole c)
	{
		SogoGame game = c.getGame();
		
		List<SogoMove> moves = game.generateValidMoves();
		int bestValue = Integer.MIN_VALUE;
		
		int value;
		for(SogoMove move : moves)
		{
			value = minValue(game.performMove(move), depth);
			if(value > bestValue)
			{
				bestValue = value;
				c.updateMove(move);
			}
			
			if(Thread.currentThread().isInterrupted() || bestValue >= 10000)
				return;
		}
	}

	private int minValue(SogoGame game, int depth) { return minValue(game, Integer.MIN_VALUE, Integer.MAX_VALUE, depth); };
	private int minValue(SogoGame game, int alpha, int beta, int depth)
	{
		if(depth <= 0 || game.ends())
			return evaluate(game);
		
		List<SogoMove> moves = game.generateValidMoves();
		int bestValue = Integer.MAX_VALUE;
		
		int value;
		for(SogoMove move : moves)
		{
			value = maxValue(game.performMove(move), alpha, bestValue, depth - 1);
			if(value < bestValue)
				bestValue = value;
			
			if(bestValue < alpha)
				return bestValue;
		}
		
		return bestValue;
	}
	
	//private int maxValue(SogoGame game, int depth) { return maxValue(game, Integer.MIN_VALUE, Integer.MAX_VALUE, depth); };
	private int maxValue(SogoGame game, int alpha, int beta, int depth)
	{
		if(depth <= 0 || game.ends())
			return evaluate(game);
		
		List<SogoMove> moves = game.generateValidMoves();
		int bestValue = Integer.MIN_VALUE;
		
		int value;
		for(SogoMove move : moves)
		{
			value = minValue(game.performMove(move), bestValue, beta, depth - 1);
			if(value > bestValue)
				bestValue = value;
			
			if(bestValue > beta)
				return bestValue;
		}
		
		return bestValue;
	}
	
	private static final int win_p = 1000,
			triple_p = 20,
			double_p = 2,
			single_p = 1;
	private static int evaluate(final SogoGame g) {
		List<Player[]> lines = g.getLines();
		int res = 0;
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
}
