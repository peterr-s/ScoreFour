package de.cogsys.ai.sogo.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import de.cogsys.ai.sogo.game.SogoGame;
import de.cogsys.ai.sogo.game.SogoGame.Player;
import de.cogsys.ai.sogo.game.SogoMove;

/**
 * @author Sebastian Otte
 */
public class SogoGamePanel extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;

	// factor for lenghtening due to isometric projection
	public static final double ISO_LONG = Math.sqrt(1.5);
	public static final double ISO_SHORT = Math.sqrt(0.5);

	public static final double BEADSIZE = 50;
	public static final double PEGWIDTH = 10;
	public static final double PEGHEIGHT = 4 * BEADSIZE + 10;

	public static final double SPACING = 100;
	public static final double BORDER = 50;

	public static final int CELLMARGIN = 4;
	public static final int CELLWIDTH = 80;
	public static final int CELLHEIGHT = 80;

	public static final int PANELMARGIN = 20;

	public static final int WIDTH = 640;
	public static final int HEIGHT = 600;

	public static final BasicStroke STROKE_GAMEAREA = new BasicStroke(2);

	public static final Font FONT_BOARDLABEL = new Font("Verdana", Font.BOLD, 16);
	public static final Color COLOR_BOARDLABEL = new Color(180, 180, 200);

	public static final Color COLOR_BACKGROUND = Color.BLACK;
	public static final Color COLOR_BOARD = Color.DARK_GRAY;
	public static final Color COLOR_PEG = new Color(0x46, 0x1F, 0x00);
	public static final Color COLOR_BEAD_P1 = new Color(0x23, 0x10, 0x10);
	public static final Color COLOR_BEAD_P2 = new Color(0xE6, 0xBF, 0x83);
	public static final Color COLOR_BEAD_PREVIEW = Color.GRAY;
	public static final Color COLOR_LAST_MOVE_MIX = new Color(100, 80, 50);

	private static Color mix(final double mix, final Color c1, final Color c2) {
		return new Color((int) ((1.0 - mix) * c1.getRed() + mix * c2.getRed()),
				(int) ((1.0 - mix) * c1.getGreen() + mix * c2.getGreen()),
				(int) ((1.0 - mix) * c1.getBlue() + mix * c2.getBlue()));
	}

	private SogoGame game;

	private double viewAngle = -0.5;

	private List<SogoGamePanelListener> listener = new ArrayList<SogoGamePanelListener>();

	private int previewI = -1;
	private int previewJ = -1;

	private int lastMoveI = -1;
	private int lastMoveJ = -1;
	private int lastMoveK = -1;

	public void addListener(final SogoGamePanelListener listener) {
		this.listener.add(listener);
	}

	public void clearListener() {
		this.listener.clear();
	}

	public SogoGamePanel() {
		final Dimension dimension = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(dimension);
		setMinimumSize(dimension);
		setMaximumSize(dimension);
		setSize(dimension);

		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void update(final SogoGame game) {
		this.update(game, null);
	}

	public void update(final SogoGame game, final SogoMove lastmove) {
		this.game = game;

		if (lastmove != null) {
			lastMoveI = lastmove.i;
			lastMoveJ = lastmove.j;
			int k;
			for (k = 0; k < 4; k++) {
				if (game.board[lastmove.i][lastmove.j][k] == Player.NONE) {
					break;
				}
			}
			assert (k != 0);
			lastMoveK = k - 1;
		}

		this.repaint();
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		// draw background.
		g2d.setColor(COLOR_BACKGROUND);
		g2d.fillRect(0, 0, WIDTH, HEIGHT);

		// draw playing area
		g2d.setColor(COLOR_BOARD);
		g2d.fillRect(PANELMARGIN, PANELMARGIN, WIDTH - 2 * PANELMARGIN, HEIGHT - 2 * PANELMARGIN);

		// draw pegs and beads, making sure to draw from back to front
		int init_i, i_inc, init_j, j_inc;
		if (viewAngle > Math.PI / 4 || viewAngle < -3 * Math.PI / 4) {
			init_i = 3;
			i_inc = -1;
		} else {
			init_i = 0;
			i_inc = 1;
		}
		if (viewAngle < -Math.PI / 4 || viewAngle > 3 * Math.PI / 4) {
			init_j = 3;
			j_inc = -1;
		} else {
			init_j = 0;
			j_inc = 1;
		}
		for (int i = init_i; 0 <= i && i < 4; i += i_inc) {
			for (int j = init_j; 0 <= j && j < 4; j += j_inc) {
				drawPeg(g2d, i, j);
			}
		}

	}

	private void drawPeg(final Graphics2D g, int i, int j) {
		double x = (i - 1.5) * SPACING;
		double y = (j - 1.5) * SPACING;
		Point2D p = project(x, y);

		boolean previewDrawn = false;
		int k;
		peg: for (k = 0; k < 4; k++) {
			switch (game.board[i][j][k]) {
			case P1:
				g.setColor(COLOR_BEAD_P1);
				break;
			case P2:
				g.setColor(COLOR_BEAD_P2);
				break;
			case NONE:
				if (i == previewI && j == previewJ && !previewDrawn) {
					g.setColor(COLOR_BEAD_PREVIEW);
					previewDrawn = true;
				} else {
					break peg;
				}
			}
			if (i == lastMoveI && j == lastMoveJ && k == lastMoveK) {
				g.setColor(mix(0.3, g.getColor(), COLOR_LAST_MOVE_MIX));
			}
			g.fill(new Ellipse2D.Double(p.getX() - ISO_LONG * BEADSIZE / 2,
					p.getY() - k * BEADSIZE - ISO_LONG * BEADSIZE, ISO_LONG * BEADSIZE, ISO_LONG * BEADSIZE));
		}

		g.setColor(COLOR_PEG);
		g.fill(new Rectangle2D.Double(p.getX() - ISO_LONG * PEGWIDTH / 2, p.getY() - PEGHEIGHT, ISO_LONG * PEGWIDTH,
				PEGHEIGHT - k * BEADSIZE));
		g.fill(new Ellipse2D.Double(p.getX() - ISO_LONG * PEGWIDTH / 2, p.getY() - PEGHEIGHT - ISO_LONG * PEGWIDTH / 2,
				ISO_LONG * PEGWIDTH, ISO_LONG * PEGWIDTH));
		g.fill(new Ellipse2D.Double(p.getX() - ISO_LONG * PEGWIDTH / 2,
				p.getY() - k * BEADSIZE - ISO_SHORT * PEGWIDTH / 2, ISO_LONG * PEGWIDTH, ISO_SHORT * PEGWIDTH));

	}

	private Point2D project(double x, double y) {
		double xpy = (x + y) / 2.0;
		double xmy = (x - y) / 2.0;
		double cPhi = Math.cos(viewAngle);
		double sPhi = Math.sin(viewAngle);
		return new Point2D.Double(Math.sqrt(3) * (cPhi * xmy + sPhi * xpy) + WIDTH / 2,
				cPhi * xpy - sPhi * xmy + 2 * HEIGHT / 3);
	}

	private void clickedCell(final int i, final int j) {
		for (SogoGamePanelListener listener : this.listener) {
			listener.clickedCell(i, j);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1 && previewI != -1 && previewJ != -1) {
			clickedCell(previewI, previewJ);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// draw pegs and beads, making sure to draw from back to front
		int init_i, i_inc, init_j, j_inc;
		if (viewAngle > Math.PI / 4 || viewAngle < -3 * Math.PI / 4) {
			init_i = 0;
			i_inc = 1;
		} else {
			init_i = 3;
			i_inc = -1;
		}
		if (viewAngle < -Math.PI / 4 || viewAngle > 3 * Math.PI / 4) {
			init_j = 0;
			j_inc = 1;
		} else {
			init_j = 3;
			j_inc = -1;
		}
		for (int i = init_i; 0 <= i && i < 4; i += i_inc) {
			for (int j = init_j; 0 <= j && j < 4; j += j_inc) {
				double x = (i - 1.5) * SPACING;
				double y = (j - 1.5) * SPACING;
				Point2D p = project(x, y);
				if (Math.abs(e.getX() - p.getX()) < 20 && e.getY() < p.getY()
						&& e.getY() > (p.getY() - PEGHEIGHT - 10)) {
					previewI = i;
					previewJ = j;
					this.repaint();
					return;
				}
			}
		}
		previewI = -1;
		previewJ = -1;
		this.repaint();
	}

	private double mouseDragLastX;

	@Override
	public void mouseDragged(MouseEvent e) {
		int mask = MouseEvent.BUTTON3_DOWN_MASK;
		if ((e.getModifiersEx() & mask) == mask) {
			viewAngle = (viewAngle + (e.getX() - mouseDragLastX) / 100.0) % (2 * Math.PI);
			if (viewAngle > Math.PI) {
				viewAngle -= 2 * Math.PI;
			} else if (viewAngle < -Math.PI) {
				viewAngle += 2 * Math.PI;
			}
			mouseDragLastX = e.getX();
			this.repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			mouseDragLastX = e.getX();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}