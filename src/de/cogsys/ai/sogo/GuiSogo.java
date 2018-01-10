package de.cogsys.ai.sogo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.UIManager;

import de.cogsys.ai.sogo.control.SogoGameConsole;
import de.cogsys.ai.sogo.control.TimeCounter;
import de.cogsys.ai.sogo.game.SogoGame;
import de.cogsys.ai.sogo.game.SogoGame.Player;
import de.cogsys.ai.sogo.game.SogoMove;
import de.cogsys.ai.sogo.gui.MainFrame;
import de.cogsys.ai.sogo.gui.SogoGamePanel;
import de.cogsys.ai.sogo.gui.SogoGamePanelListener;
import de.cogsys.ai.sogo.gui.StatusPanel;
import de.cogsys.ai.sogo.player.AIPeterSchoener;
import de.cogsys.ai.sogo.player.GuiPlayer;
import de.cogsys.ai.sogo.player.MrNovice;
import de.cogsys.ai.sogo.player.MrRandom;
import de.cogsys.ai.sogo.player.MrSavant;
import de.cogsys.ai.sogo.player.SogoPlayer;

/**
 * @author Sebastian Otte
 */
public class GuiSogo {

	public static <T> T selectDiaglog(final String caption, final String message, final T[] options,
			final T defaultoption, final Icon icon) {
		//
		@SuppressWarnings("unchecked")
		final T result = (T) JOptionPane.showInputDialog(null, message, caption, JOptionPane.QUESTION_MESSAGE, icon,
				options, defaultoption);
		//
		return result;
	}

	public static final String[] agents = {
		"Human",
		"MrNovice (depth 2)",
		"MrNovice (depth 3)",
		"MrNovice (depth 4)",
		"MrRandom",
		"AIPeter",
		"MrSavant"
	};

	public static SogoPlayer createAgent(final String agent) {
		if (agent.equals(agents[0])) return new GuiPlayer();
		if (agent.equals(agents[1])) return new MrNovice(2);
		if (agent.equals(agents[2])) return new MrNovice(3);
		if (agent.equals(agents[3])) return new MrNovice(4);
		if (agent.equals(agents[4])) return new MrRandom();
		if (agent.equals(agents[5])) return new AIPeterSchoener();
		if (agent.equals(agents[6])) return new MrSavant(5);
		return null;
	}

	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		;

		String player1name = agents[0];
		String player2name = agents[1];

		final GuiSogo gameControl = new GuiSogo();

		do {
			// select player 1
			player1name = selectDiaglog("Select Player 1", "Select one of the following agents:", agents, player1name,
					null);
			if (player1name == null) {
				break;
			}
			// select player 2
			player2name = selectDiaglog("Select Player 2", "Select one of the following agents:", agents, player2name,
					null);
			if (player2name == null) {
				break;
			}

			gameControl.start(new SogoGame(), createAgent(player1name), createAgent(player2name));

		} while (JOptionPane.showConfirmDialog(null, "Start a new game?", gameControl.mainframe.getTitle(),
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null) == JOptionPane.YES_OPTION);

		System.exit(0);
	}

	public static final long PLAYER_TIMEOUT = 10000;
	public static final long TIMEOUT_CULANCE = 500;

	private MainFrame mainframe;
	private SogoGamePanel gamepanel;
	private StatusPanel statuspanel;
	private SogoPlayer player1;
	private SogoPlayer player2;
	private SogoGame game;
	private SogoMove lastmove;
	private boolean running;

	public MainFrame getMainFrame() {
		return this.mainframe;
	}

	public SogoGamePanel getGamePanel() {
		return this.gamepanel;
	}

	public StatusPanel getStatusPanel() {
		return this.statuspanel;
	}

	public SogoPlayer getPlayer1() {
		return this.player1;
	}

	public SogoPlayer getPlayer2() {
		return this.player2;
	}

	public SogoGame getGame() {
		return this.game;
	}

	public boolean isRunning() {
		synchronized (this) {
			return this.running;
		}
	}

	public GuiSogo() {
		final MainFrame frame = new MainFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mainframe = frame;
		this.gamepanel = frame.getGamePanel();
		this.statuspanel = frame.getStatusPanel();
	}

	public void update() {
		this.gamepanel.update(this.game, this.lastmove);
		if (this.game.ends()) {
			switch (this.game.result()) {
			case P1:
				this.statuspanel.status("Player 1 wins!");
				break;
			case P2:
				this.statuspanel.status("Player 2 wins!");
				break;
			case NONE:
				this.statuspanel.status("It's a draw!");
				break;
			}
		} else {
			if (this.game.getCurrentPlayer() == Player.P1) {
				this.statuspanel.status("Player 1's turn...");
			} else {
				this.statuspanel.status("Player 2's turn...");
			}
		}
	}

	private static String encodeMove(SogoMove m) {
		return "" + (char) ('A' + m.j) + (char) ('1' + m.i);
	}

	public Player start(final SogoGame game, final SogoPlayer player1, final SogoPlayer player2) {
		synchronized (this) {
			this.running = true;
		}
		this.game = game;
		this.lastmove = null;
		this.player1 = player1;
		this.player2 = player2;

		this.mainframe.setVisible(true);

		this.gamepanel.clearListener();

		this.player1.initialize(Player.P1);
		if (this.player1 instanceof SogoGamePanelListener) {
			this.gamepanel.addListener((SogoGamePanelListener) this.player1);
		}
		this.player2.initialize(Player.P2);
		if (this.player2 instanceof SogoGamePanelListener) {
			this.gamepanel.addListener((SogoGamePanelListener) this.player2);
		}

		final TimeCounter tc = new TimeCounter();
		tc.reset();
		Timer timer = null;

		timer = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiSogo.this.statuspanel.updateTimeLeft(Math.max(0, PLAYER_TIMEOUT - tc.valueMilli()) / 1000.0);
			}
		});

		while (!this.game.ends()) {
			this.update();

			SogoPlayer player;

			if (this.game.getCurrentPlayer() == Player.P1) {
				player = this.player1;
			} else {
				player = this.player2;
			}

			final SogoPlayer currentplayer = player;
			final SogoMove[] playermove = new SogoMove[1];
			SogoMove selectedMove = null;

			tc.reset();
			timer.start();

			final Thread thread = new Thread(() -> {
				currentplayer.generateNextMove(new SogoGameConsole() {
					@Override
					public SogoGame getGame() {
						return new SogoGame(GuiSogo.this.game);
					}

					@Override
					public long getTimeLeft() {
						return Math.max(0, PLAYER_TIMEOUT - tc.valueMilli());
					}

					@Override
					public void updateMove(final SogoMove move) {
						playermove[0] = move;
					}
				});
			});

			thread.start();

			try {
				thread.join(PLAYER_TIMEOUT + TIMEOUT_CULANCE);
				selectedMove = playermove[0];
				thread.interrupt();
			} catch (InterruptedException e) {
			}

			timer.stop();

			if (selectedMove == null) {
				if (this.game.getCurrentPlayer() == Player.P1) {
					this.statuspanel.msg("Player 1 gives up.");
					this.statuspanel.status("Player 2 wins!");
					synchronized (this) {
						this.running = false;
					}
					return Player.P2;
				} else {
					this.statuspanel.msg("Player 2 gives up.");
					this.statuspanel.status("Player 1 wins!");
					synchronized (this) {
						this.running = false;
					}
					return Player.P1;
				}
			} else {
				if (this.game.getCurrentPlayer() == Player.P1) {
					System.out.print("Player 1");
				} else {
					System.out.print("Player 2");
				}

				System.out.println(" performs move : " + encodeMove(selectedMove));
				System.out.println("");

				this.game = this.game.performMove(selectedMove);
				this.lastmove = selectedMove;
			}
		}

		this.update();

		System.out.println();
		System.out.println(this.game);
		System.out.println();

		if (this.game.wins(Player.P1)) {
			synchronized (this) {
				this.running = false;
			}
			return Player.P1;
		}
		if (this.game.wins(Player.P2)) {
			synchronized (this) {
				this.running = false;
			}
			return Player.P2;
		}
		synchronized (this) {
			this.running = false;
		}
		return Player.NONE;

	}

}