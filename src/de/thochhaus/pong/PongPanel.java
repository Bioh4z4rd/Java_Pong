package de.thochhaus.pong;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PongPanel extends JFrame implements Runnable, WindowListener {

	/*
	 * desired UPS Period in ns, FPS will be adjusted according to the speed of
	 * the running machine
	 */
	private static long period;


	private static final int MAX_FRAME_SKIPS = 5;
	private static final int NUM_FPS = 10;
	private static final int TIME_COLLECT_STATS = 1000;
	private static final int WIDTH = 500;
	private static final int HEIGHT = 400;

	private volatile boolean isPaused;
	private volatile boolean isRunning;

	private Thread animator; // Animationthread

	private Font font;
	private FontMetrics metrics;

	// calculate stats
	private int statsIntervall = 0;
	private int storeCount = 0;
	private double[] fpsStore;
	private double averageFPS = 0.0;
	private double[] upsStore;
	private double averageUPS = 0.0;

	private Graphics dbg;
	private Image dbgImage;
	
	private Brick token1, token2;

	public PongPanel() {

		/* ---- Initialize Store Buffers */
		fpsStore = new double[10];
		upsStore = new double[10];
		for (int i = 0; i < 10; i++) {
			fpsStore[i] = 0.0;
			upsStore[i] = 0.0;
		}

		createGUI();
		
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));

		setFocusable(true);
		requestFocus();
		addKeyListeners();

		/*
		 * Paint components the first time
		 */

		
	}
	
	private void createGUI() {
		
		Container c = getContentPane();
		
		JPanel jp = new JPanel();
		jp.setBackground(Color.WHITE);
		jp.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		font = new Font("SansSerif", Font.BOLD, 24);
		metrics = this.getFontMetrics(font);

		c.add(jp, "Center");
		
		JPanel stats = new JPanel();
		stats.setLayout(new BoxLayout(stats, BoxLayout.X_AXIS));
		
		JTextField sbox = new JTextField();
		sbox.setText("SPIELSTAND");
		sbox.setEditable(false);
		
		stats.add(sbox, "Center");
		c.add(stats, "North");
		
	}

	public void pauseGame() {
		isPaused = true;
	}

	public void resumeGame() {
		isPaused = false;
	}

	public void stopGame() {
		isRunning = false;
	}

	public void run() {

		// FPS measuring / sleepDelay
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;

		beforeTime = System.nanoTime();

		isRunning = true;

		while (isRunning) {

			gameUpdate();
			gameRender();
			paintScreen();

			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = (period - timeDiff) - overSleepTime;

			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime / 1000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			} else {
				excess -= sleepTime;
				overSleepTime = 0L;

				beforeTime = System.nanoTime();

				int skips = 0;
				while (excess > period && skips < MAX_FRAME_SKIPS) {
					excess -= period;

					gameUpdate();
					skips++;
				}
			}
		}
	}

	public void gameUpdate() {
		if (!isPaused) {

		}
	}

	public void gameRender() {
		if (dbgImage == null) {
			dbgImage = createImage(WIDTH, HEIGHT);
			if (dbgImage == null) {
				System.out.println("Image kann nicht erzeugt werden!");
				return;
			} else {
				dbg = dbgImage.getGraphics();
			}
		}

		dbg.setColor(Color.WHITE);
		dbg.fillRect(0, 0, WIDTH, HEIGHT);

		dbg.setColor(Color.RED);
		dbg.setFont(font);

		dbg.drawString("Average FPS/UPS: " + averageFPS + "/" + averageUPS, 20,
				20);

	}

	public void paintScreen() {

		Graphics g;

		try {
			g = this.getGraphics();
			if (g != null && dbgImage != null) {
				g.drawImage(dbgImage, 0, 0, null);
				g.dispose();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void calculateStats() {

		statsIntervall += period;

		if (statsIntervall >= TIME_COLLECT_STATS) {
			storeCount++;

		}
	}

	public void addNotify() {
		super.addNotify();
		startGame();
	}

	private void startGame() {

		if (animator == null) {
			animator = new Thread(this);
			animator.start();
		}
	}

	/* ---- Listeners for Key and Window Events ---- */
	private void addKeyListeners() {
		addKeyListener(new KeyAdapter() {
			// listen for esc, q, end, ctrl-c on the canvas to
			// allow a convenient exit from the full screen configuration
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				switch (keyCode) {

				case KeyEvent.VK_LEFT:
					token1.moveLeft();
					break;
				case KeyEvent.VK_RIGHT:
					token1.moveRight();
					break;
				case KeyEvent.VK_ESCAPE:
				case KeyEvent.VK_Q:
				case KeyEvent.VK_END:
					isRunning = false;
					break;
				case KeyEvent.VK_C:
					if (e.isControlDown())
						isRunning = false;
					break;
				}
			}
		});
	}

	public void windowActivated(WindowEvent arg0) {
		resumeGame();
	}

	public void windowClosed(WindowEvent arg0) {
	}

	public void windowClosing(WindowEvent arg0) {
		stopGame();
	}

	public void windowDeactivated(WindowEvent arg0) {
		pauseGame();
	}

	public void windowDeiconified(WindowEvent arg0) {
		resumeGame();
	}

	public void windowIconified(WindowEvent arg0) {
		pauseGame();
	}

	public void windowOpened(WindowEvent arg0) {
	}

	public static void main() {
		
		period = 1000L/NUM_FPS;

		PongPanel pp = new PongPanel();
	}
	
}
