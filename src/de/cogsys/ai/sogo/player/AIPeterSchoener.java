package de.cogsys.ai.sogo.player;

import java.util.List;

import de.cogsys.ai.sogo.GuiSogo;
import de.cogsys.ai.sogo.control.SogoGameConsole;
import de.cogsys.ai.sogo.game.SogoGame;
import de.cogsys.ai.sogo.game.SogoGame.Player;
import de.cogsys.ai.sogo.game.SogoMove;

public class AIPeterSchoener implements SogoPlayer
{
	private Player me, opponent; // roles are determined at creation for efficiency
	
	private static int depth = 4;
	private static final int MAX_BRANCH_FACTOR = 20; // max branching is actually 16 of course, but turns out the pruning is so inconsistent it can jam badly on that value
	
	public AIPeterSchoener()
	{
	}
	
	@Override
	public void initialize(Player p)
	{
		me = p;
		opponent = me == Player.P1 ? Player.P2 : Player.P1;
	}
	
	/**
	 * Generates a move.
	 * 
	 * This is a standard minmax approach with alpha beta pruning and a
	 * few tweaks. It will automatically play a move rather than
	 * continuing to search if it finds one that is really good, and
	 * will try to adjust search depth based on how much time it needed
	 * in the previous turn.
	 * 
	 * Considered converting to an iterative rather than recursive
	 * implementation, but that was just a mess and not worth sorting out
	 * for the performance gain, even in this case.
	 * 
	 * The heuristic is explained below.
	 * 
	 * @param c - the console containing the game
	 */
	@Override
	public void generateNextMove(SogoGameConsole c)
	{
		SogoGame game = c.getGame();
		
		List<SogoMove> moves = game.generateValidMoves();
		if(!moves.isEmpty()) // at least prepare a move if possible to ensure that the first traversal doesn't already time out
			c.updateMove(moves.get(0));
		int bestValue = Integer.MIN_VALUE;
		
		int value;
		for(SogoMove move : moves)
		{
			value = minValue(game.performMove(move), Integer.MIN_VALUE, Integer.MAX_VALUE, depth);
			if(value > bestValue)
			{
				bestValue = value;
				c.updateMove(move);
			}
			
			if(Thread.currentThread().isInterrupted())
			{
				depth --; // reduce depth if it ran out of time this round
				
				return;
			}
			if(bestValue >= 10000) // don't do anything to depth here because this is just a matter of lucky order
				return;
		}
		
		if((GuiSogo.PLAYER_TIMEOUT / (GuiSogo.PLAYER_TIMEOUT - c.getTimeLeft())) > MAX_BRANCH_FACTOR) // increase depth if it had lots of extra time this round
			depth ++;
	}

	/**
	 * Minimizes the evaluation function.
	 * 
	 * @param game - the game state being considered
	 * @param alpha - best maximizer result between here and root
	 * @param beta - best minimizer result between here and root
	 * @param depth - maximum remaining search depth
	 * @return the best value found in this branch
	 */
	//private int minValue(SogoGame game, int depth) { return minValue(game, Integer.MIN_VALUE, Integer.MAX_VALUE, depth); };
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
	
	/**
	 * Maximizes the evaluation function.
	 * 
	 * @param game - the game state being considered
	 * @param alpha - best maximizer result between here and root
	 * @param beta - best minimizer result between here and root
	 * @param depth - maximum remaining search depth
	 * @return the best value found in this branch
	 */
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
	
	/**
	 * Evaluates a game state.
	 * 
	 * This is the key part of my solution. I evaluate a game state as
	 * a sum of values of all possible lines. A line is worth something
	 * if it isn't blocked by the opponent, so it is only offensively
	 * valuable to one player once it has been "claimed". Lines also
	 * have a worse than exponentially decreasing chance of staying open
	 * as they approach completion because the incentive to block them
	 * grows. I approximated this with just a power of three since it's
	 * close enough and faster, and this seems to beat the Mr. Novice
	 * approach.
	 * 
	 * I tried approaches where the values of the lines were multiplied
	 * with each other to promote forked strategies, but this didn't
	 * work for whatever reason.
	 * 
	 * This formula also incentivizes interfering with the opponent even
	 * before their win is imminent.
	 * 
	 * @param game - the game state being evaluated
	 * @return an integer representing how desirable the game state is for this agent
	 */
	private int evaluate(SogoGame game)
	{
		List<Player[]> lines = game.getLines();
		
		int total = 0;
		
		int lineValue;
		for(Player[] line : lines)
		{
			lineValue = evaluateLine(line, me, opponent);
			
			if(lineValue == 0)
				lineValue = - evaluateLine(line, opponent, me);
			
			if(lineValue <= -4 || lineValue >= 4)
				return 10000 * lineValue;
			
			total += lineValue * lineValue * lineValue;
		}
		
		return total;
	}
	
	/**
	 * Evaluates a line along which a win could be made.
	 * 
	 * Checks how many of the four slots (everything was hard coded in
	 * the example agents so I just did the same here; might as well
	 * save those nanoseconds) are occupied. A route is automatically
	 * worthless if blocked by the opponent.
	 * 
	 * @param line
	 * @param p
	 * @param a
	 * @return a number between 0 and 4, indicating how close the player is to controlling the line
	 */
	private int evaluateLine(Player[] line, Player p, Player a)
	{
		int value = 0;
		for(int i = 0; i < 4; i ++)
		{
			if(line[i] == a) // put this first since it's more than likely to be hit at least once
				return 0;
			else if(line[i] == p)
				value ++;
		}
		return value;
	}
}
