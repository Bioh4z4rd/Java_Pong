package de.thochhaus.pong;

import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.crypto.dsig.keyinfo.PGPData;

public class PongGame extends JFrame implements WindowListener{
	
	private static final int WIDTH  = 500;
	private static final int HEIGHT = 400;
	
	private static PongPanel pp;
	private static long period;

	private static final int UPS = 60;
	
	public static void main (String[] args) {

		period = 1000L/UPS;
		
		PongGame pg = new PongGame();
	
	}

	public PongGame () {
		
		super("Ponggame");
		
		createGUI();
		addWindowListener(this);
		pack();
		
		setResizable(false);
		setVisible(true);
		
		pp.addNotify();
		
	}
	
	private void createGUI() {
		
		Container c = getContentPane();
		
		pp = new PongPanel(period*1000L);
		
		c.add(pp, "Center");
		
		JPanel stats = new JPanel();
		stats.setLayout(new BoxLayout(stats, BoxLayout.X_AXIS));
		
		JTextField sbox = new JTextField();
		sbox.setText("SPIELSTAND");
		sbox.setEditable(false);
		
		stats.add(sbox, "Center");
		c.add(stats, "North");
		
	}

	public void windowActivated(WindowEvent arg0) {
		pp.resumeGame();
	}

	public void windowClosed(WindowEvent arg0) {}

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
	
}
