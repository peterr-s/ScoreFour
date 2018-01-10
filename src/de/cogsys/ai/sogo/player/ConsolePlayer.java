package de.cogsys.ai.sogo.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;

import de.cogsys.ai.sogo.control.SogoGameConsole;
import de.cogsys.ai.sogo.game.SogoGame;
import de.cogsys.ai.sogo.game.SogoMove;
import de.cogsys.ai.sogo.game.SogoGame.Player;

public class ConsolePlayer implements SogoPlayer {
	
	private SogoGame.Player self;
	private BufferedReader br;
	
	public ConsolePlayer(BufferedReader br) {
		this.br = br;
	}

	@Override
	public void initialize(SogoGame.Player p) {
		self = p;
	}

	@Override
	public void generateNextMove(final SogoGameConsole c) {
		final SogoGame g = c.getGame();
		int i = -1;
		int j = -1;
		do {
			System.out.print("Enter move for Player " + (self==Player.P1 ? "1 (X):" : "2 (O): "));
			try {
				String line = br.readLine();
				Scanner s = new Scanner(line);
				Scanner ss = s.useDelimiter("\\s*,\\s*");
				i = s.nextInt();
				j = s.nextInt();
				ss.close();
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		} while (!g.isValidMove(i, j));

		c.updateMove(new SogoMove(i,j));
	}

}
