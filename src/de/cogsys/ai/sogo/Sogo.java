package de.cogsys.ai.sogo;

import de.cogsys.ai.sogo.control.SogoGameConsole;
import de.cogsys.ai.sogo.control.TimeCounter;
import de.cogsys.ai.sogo.game.SogoGame;
import de.cogsys.ai.sogo.game.SogoGame.Player;
import de.cogsys.ai.sogo.game.SogoMove;
import de.cogsys.ai.sogo.player.MrNovice;
import de.cogsys.ai.sogo.player.MrRandom;
import de.cogsys.ai.sogo.player.SogoPlayer;

public class Sogo {

	public static final long PLAYER_TIMEOUT = 10000;
	public static final long TIMEOUT_CULANCE = 1000;

	public static void main(String[] args) {

		// BufferedReader br = new BufferedReader(new
		// InputStreamReader(System.in));

		SogoGame g = new SogoGame();
		final SogoPlayer p1 = new MrNovice(); // new ConsolePlayer(br);
		final SogoPlayer p2 = new MrRandom(); // new ConsolePlayer(br);
		p1.initialize(Player.P1);
		p2.initialize(Player.P2);

		// game loop
		boolean playing = true;
		int turn = 0;
		final TimeCounter tc = new TimeCounter();
		tc.reset();
		while (playing) {
			turn++;
			System.out.println("Turn " + turn + ":");
			System.out.print(g);

			SogoPlayer player;

			if (g.getCurrentPlayer() == Player.P1) {
				player = p1;
			} else {
				player = p2;
			}

			final SogoPlayer currentplayer = player;
			final SogoMove[] m = new SogoMove[1];
			SogoMove selectedMove = null;

			tc.reset();

			final SogoGame sg = new SogoGame(g);

			final Thread thread = new Thread(() -> {
				currentplayer.generateNextMove(new SogoGameConsole() {
					@Override
					public SogoGame getGame() {
						return new SogoGame(sg);
					}

					@Override
					public long getTimeLeft() {
						return Math.max(0, PLAYER_TIMEOUT - tc.valueMilli());
					}

					@Override
					public void updateMove(final SogoMove move) {
						m[0] = move;
					}
				});
			});

			thread.start();
			try {
				thread.join(PLAYER_TIMEOUT + TIMEOUT_CULANCE);
				selectedMove = m[0];
				thread.interrupt();
			} catch (InterruptedException e) {
			}

			if (!g.isValidMove(selectedMove)) {
				System.out.println(
						"Player " + g.getCurrentPlayer() + "has selected an invalid move and forfeits the game");
				System.exit(1);
			}
			g = g.performMove(selectedMove);
			if (g.ends()) {
				playing = false;
			}
			System.out.println();
		}

		switch (g.result()) {
		case P1:
			System.out.println("Player 1 (X) wins");
			break;
		case P2:
			System.out.println("Player 2 (O) wins");
			break;
		case NONE:
			System.out.println("Draw!");
			break;
		}
		System.out.print(g);
	}

}
