package de.cogsys.ai.sogo.game;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

public class SogoGame {

	public enum Player {
		NONE, P1, // Black, X
		P2 // White, O
	}

	public static int size = 4;

	private Player currentPlayer;
	public Player[][][] board;

	public SogoGame() {
		this.currentPlayer = Player.P1;
		this.board = new Player[size][size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				for (int k = 0; k < size; k++) {
					this.board[i][j][k] = Player.NONE;
				}
			}
		}
	}

	public SogoGame(SogoGame g) {
		this.currentPlayer = g.currentPlayer;
		this.board = new Player[size][size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				for (int k = 0; k < size; k++) {
					this.board[i][j][k] = g.board[i][j][k];
				}
			}
		}
	}

	public List<Player[]> getLines() {
		List<Player[]> res = new LinkedList<Player[]>();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				Player[] tempX = new Player[size];
				Player[] tempY = new Player[size];
				Player[] tempZ = new Player[size];
				for (int k = 0; k < size; k++) {
					tempX[k] = board[k][i][j];
					tempY[k] = board[j][k][i];
					tempZ[k] = board[i][j][k];
				}
				res.add(tempX);
				res.add(tempY);
				res.add(tempZ);
			}
		}

		for (int i = 0; i < size; i++) {
			Player[] tempXd = new Player[size];
			Player[] tempXa = new Player[size];
			Player[] tempYd = new Player[size];
			Player[] tempYa = new Player[size];
			Player[] tempZd = new Player[size];
			Player[] tempZa = new Player[size];
			for (int j = 0; j < size; j++) {
				tempXd[j] = board[i][j][j];
				tempYd[j] = board[j][i][j];
				tempZd[j] = board[j][j][i];
				tempXa[j] = board[i][j][size - j - 1];
				tempYa[j] = board[size - j - 1][i][j];
				tempZa[j] = board[j][size - j - 1][i];
			}
			res.add(tempXd);
			res.add(tempYd);
			res.add(tempZd);
			res.add(tempXa);
			res.add(tempYa);
			res.add(tempZa);
		}

		Player[] tempA = new Player[size];
		Player[] tempB = new Player[size];
		Player[] tempC = new Player[size];
		Player[] tempD = new Player[size];
		for (int i = 0; i < size; i++) {
			tempA[i] = board[i][i][i];
			tempB[i] = board[i][size - i - 1][i];
			tempC[i] = board[size - i - 1][i][i];
			tempD[i] = board[size - i - 1][size - i - 1][i];
		}
		res.add(tempA);
		res.add(tempB);
		res.add(tempC);
		res.add(tempD);

		return res;
	}

	public static int countLine(Player[] line, Player player) {
		int c = 0;
		for (int i = 0; i < size; i++) {
			if (line[i] == player) {
				c++;
			}
		}
		return c;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public Player getOtherPlayer() {
		return currentPlayer == Player.P1 ? Player.P2 : Player.P1;
	}

	public boolean wins(Player pl) {
		for (Player[] line : getLines()) {
			if (countLine(line, pl) == size) {
				return true;
			}
		}
		return false;
	}

	public Player result() {
		if (wins(Player.P1)) {
			return Player.P1;
		} else if (wins(Player.P2)) {
			return Player.P2;
		} else {
			return Player.NONE;
		}
	}

	public boolean ends() {
		return wins(Player.P1) || wins(Player.P2) || generateValidMoves().isEmpty();
	}

	public boolean isValidMove(SogoMove m) {
		return isValidMove(m.i, m.j);
	}

	public boolean isValidMove(int i, int j) {
		if (i < 0 || i > 3 || j < 0 || j > 3) {
			return false;
		}
		return board[i][j][size - 1] == Player.NONE;
	}

	public List<SogoMove> generateValidMoves() {
		List<SogoMove> res = new ArrayList<SogoMove>();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (board[i][j][size - 1] == Player.NONE) {
					res.add(new SogoMove(i, j));
				}
			}
		}
		return res;
	}

	public SogoGame performMove(SogoMove m) {
		if (!isValidMove(m)) {
			throw new RuntimeException("Invalid Move!");
		}
		SogoGame res = new SogoGame(this);
		for (int k = 0; k < size; k++) {
			if (res.board[m.i][m.j][k] == Player.NONE) {
				res.board[m.i][m.j][k] = currentPlayer;
				break;
			}
		}
		res.currentPlayer = getOtherPlayer();
		return res;
	}

	private char spaceToChar(int i, int j, int k) {
		switch (board[i][j][k]) {
		case NONE:
			return '|';
		case P1:
			return 'X';
		case P2:
			return 'O';
		default:
			return ' ';
		}
	}

	@Override
	public String toString() {
		// 14 x 12
		String res = "";
		res += "  " + spaceToChar(0, 0, 3) + "    " + spaceToChar(0, 1, 3) + "    " + spaceToChar(0, 2, 3) + "    "
				+ spaceToChar(0, 3, 3) + "\n";
		res += "  " + spaceToChar(0, 0, 2) + "    " + spaceToChar(0, 1, 2) + "    " + spaceToChar(0, 2, 2) + "    "
				+ spaceToChar(0, 3, 2) + "\n";
		res += "  " + spaceToChar(0, 0, 1) + " " + spaceToChar(1, 0, 3) + "  " + spaceToChar(0, 1, 1) + " "
				+ spaceToChar(1, 1, 3) + "  " + spaceToChar(0, 2, 1) + " " + spaceToChar(1, 2, 3) + "  "
				+ spaceToChar(0, 3, 1) + " " + spaceToChar(1, 3, 3) + "\n";
		res += "0 " + spaceToChar(0, 0, 0) + " " + spaceToChar(1, 0, 2) + "  " + spaceToChar(0, 1, 0) + " "
				+ spaceToChar(1, 1, 2) + "  " + spaceToChar(0, 2, 0) + " " + spaceToChar(1, 2, 2) + "  "
				+ spaceToChar(0, 3, 0) + " " + spaceToChar(1, 3, 2) + "\n";
		res += "    " + spaceToChar(1, 0, 1) + " " + spaceToChar(2, 0, 3) + "  " + spaceToChar(1, 1, 1) + " "
				+ spaceToChar(2, 1, 3) + "  " + spaceToChar(1, 2, 1) + " " + spaceToChar(2, 2, 3) + "  "
				+ spaceToChar(1, 3, 1) + " " + spaceToChar(2, 3, 3) + "\n";
		res += "  1 " + spaceToChar(1, 0, 0) + " " + spaceToChar(2, 0, 2) + "  " + spaceToChar(1, 1, 0) + " "
				+ spaceToChar(2, 1, 2) + "  " + spaceToChar(1, 2, 0) + " " + spaceToChar(2, 2, 2) + "  "
				+ spaceToChar(1, 3, 0) + " " + spaceToChar(2, 3, 2) + "\n";
		res += "      " + spaceToChar(2, 0, 1) + " " + spaceToChar(3, 0, 3) + "  " + spaceToChar(2, 1, 1) + " "
				+ spaceToChar(3, 1, 3) + "  " + spaceToChar(2, 2, 1) + " " + spaceToChar(3, 2, 3) + "  "
				+ spaceToChar(2, 3, 1) + " " + spaceToChar(3, 3, 3) + "\n";
		res += "    2 " + spaceToChar(2, 0, 0) + " " + spaceToChar(3, 0, 2) + "  " + spaceToChar(2, 1, 0) + " "
				+ spaceToChar(3, 1, 2) + "  " + spaceToChar(2, 2, 0) + " " + spaceToChar(3, 2, 2) + "  "
				+ spaceToChar(2, 3, 0) + " " + spaceToChar(3, 3, 2) + "\n";
		res += "        " + spaceToChar(3, 0, 1) + "    " + spaceToChar(3, 1, 1) + "    " + spaceToChar(3, 2, 1)
				+ "    " + spaceToChar(3, 3, 1) + "\n";
		res += "      3 " + spaceToChar(3, 0, 0) + "    " + spaceToChar(3, 1, 0) + "    " + spaceToChar(3, 2, 0)
				+ "    " + spaceToChar(3, 3, 0) + "\n";
		res += "        0    1    2    3\n";
		return res;
	}

}
