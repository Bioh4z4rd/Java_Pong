package de.thochhaus.pong;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PongGame extends JFrame implements WindowListener {

    public static final int NUM_FPS = 60;
	
    private long period;
    
    protected JTextField sbox;
    
	private PongPanel pp;
	
	public PongGame(long p) {

		period = p;
		createGameGUI();
		addWindowListener(this);
		pack();
		
		setResizable(false);
		setVisible(true);
		
	}
	
	private void createGameGUI() {
		
		Container c = getContentPane();
	
		sbox = new JTextField();
		sbox.setText("SPIELSTAND");
		sbox.setEditable(false);
		
		JPanel stats = new JPanel();
		stats.setLayout(new BoxLayout(stats, BoxLayout.X_AXIS));
		stats.add(sbox, "Center");
		c.add(stats, "North");
		
		pp = new PongPanel(period, this);
		c.add(pp, "Center");
	}
	
	/* ---- Window Listeners ---- */
	public void windowActivated(WindowEvent arg0) {
		pp.resumeGame();
	}

	public void windowClosed(WindowEvent arg0) {
	}

	public void windowClosing(WindowEvent arg0) {
		pp.stopGame();
	}

	public void windowDeactivated(WindowEvent arg0) {
		pp.pauseGame();
	}

	public void windowDeiconified(WindowEvent arg0) {
		pp.resumeGame();
	}

	public void windowIconified(WindowEvent arg0) {
		pp.pauseGame();
	}

	public void windowOpened(WindowEvent arg0) {}

	
	
	public static void main(String[] args) {
		
		long p = 1000L / NUM_FPS;
		
		new PongGame(p * 1000000L); // ms --> ns
		
		
	}
	
}
