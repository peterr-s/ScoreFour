package de.cogsys.ai.sogo.gui;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

/**
 * @author Sebastian Otte
 */
public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private SogoGamePanel gamepanel;
	private StatusPanel    statuspanel;
	
	public SogoGamePanel getGamePanel() {
	    return this.gamepanel;
	}
	
	public StatusPanel getStatusPanel() {
	    return this.statuspanel;
	}
	
	public MainFrame() {
		super("Sogo - 3D Four-in-a-Row - Cogsys AI Competition 2017/2018");
		//
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		//
		this.gamepanel = new SogoGamePanel();
		this.statuspanel = new StatusPanel();
		//
		this.add(this.gamepanel);
		this.add(this.statuspanel);
		this.pack();
		//
		this.setResizable(false);
	}
	
	
}