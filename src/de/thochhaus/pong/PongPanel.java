package de.thochhaus.pong;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class PongPanel extends JPanel implements Runnable {

	/*
	 * desired UPS Period in ns, FPS will be adjusted according to the speed of
	 * the running machine
	 */

	/* ---- Codes of the Players used to adress their Control setup ---- */
	public static final int PLAYER_1 = 0; // Is always at the top of the Page
	public static final int PLAYER_2 = 1; // Is always at the bottom of the Page
	public static final int NO_PLAYER = 2;

	private static final int NO_DELAYS_PER_YIELD = 15;
	private static final int MAX_FRAME_SKIPS = 5;
	private static final int TIME_COLLECT_STATS = 1000000000;
	private static final int WIDTH = 500;
	private static final int HEIGHT = 400;

	private boolean isPaused;
	private boolean isRunning;
	/* ---- Used to trigger movementevents of both players simultanously ---- */
	private boolean[] playerPushesControl;
	private boolean[] playerDirectionLeft;

	private Thread animator; // Animationthread
	private JFrame topFrame; // saved to Update included JPanels

	private Font font;
	private FontMetrics metrics;
	private DecimalFormat df;

	// calculate stats
	private long timeRunning = 0L;
	private long prevStatsTime = 0L;
	private int totalFrames = 0;
	private int totalSkips = 0;
	private long statsIntervall = 0L;
	private double averageFPS = 0.0;
	private double averageUPS = 0.0;

	private long period;

	private Graphics dbg;
	private Image dbgImage;

	private Ball ball;
	private Brick[] tokens;
	private int[][] controls; // contains Keycodes used for controlling the
								// Bricks

	public PongPanel(long period, JFrame caller) {

		topFrame = caller;
		this.period = period;

		playerPushesControl = new boolean[] { false, false };
		playerDirectionLeft = new boolean[] { false, false };

		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));

		setFocusable(true);
		addKeyListeners();

		/* ---- Compute startpositions of Bricks ---- */
		int midCanvasX = (WIDTH / 2) - (Brick.BRICK_WIDTH / 2);
		int topY = 20;
		int botY = HEIGHT - 20;

		tokens = new Brick[2];
		tokens[PLAYER_1] = new Brick(midCanvasX, topY, WIDTH);
		tokens[PLAYER_2] = new Brick(midCanvasX, botY, WIDTH);
		ball = new Ball(tokens[PLAYER_1], tokens[PLAYER_2]);

		df = new DecimalFormat("0.##");

		controls = new int[][] { { KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT },
				{ KeyEvent.VK_A, KeyEvent.VK_D } };
	}

	/* ---- Methods to control Game (start, pause...) ---- */

	public void addNotify() {

		super.addNotify();
		requestFocus();
		startGame();
	}

	private void startGame() {

		if (animator == null) {
			animator = new Thread(this);
			animator.start();
		}
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
		prevStatsTime = beforeTime;

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
					Thread.sleep(sleepTime / 1000000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			} else {
				++noDelays;
				excess -= sleepTime;
				overSleepTime = 0L;

				int skips = 0;
				while (excess > period && skips < MAX_FRAME_SKIPS) {
					excess -= period;

					gameUpdate();
					skips++;
				}
				totalSkips += skips;

				/* ---- Give Garbage Collector a Chance to run ---- */
				if (noDelays >= NO_DELAYS_PER_YIELD) {
					noDelays = 0;
					Thread.yield();
				}

			}

			beforeTime = System.nanoTime();
			calculateStats();

		}

		System.exit(0);
	}

	private void gameUpdate() {
		if (!isPaused) {
			moveTokens();
			ball.move();
		}
	}

	private void gameRender() {
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

		dbg.drawString(
				"Average FPS/UPS: " + df.format(averageFPS) + "/"
						+ df.format(averageUPS), 20, 20);

		tokens[0].draw(dbg);
		tokens[1].draw(dbg);
		ball.draw(dbg);

	}

	private void paintScreen() {

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

	public void moveTokens() {

		for (int i = 0; i < 2; i++) {
			if (playerPushesControl[i])
				if (playerDirectionLeft[i])
					tokens[i].moveLeft();
				else
					tokens[i].moveRight();
		}
	}

	public void calculateStats() {

		totalFrames++;
		statsIntervall += period;

		if (statsIntervall >= TIME_COLLECT_STATS) {
			long timeNow = System.nanoTime();
			long realElapsedTime = timeNow - prevStatsTime;
			timeRunning += realElapsedTime;

			averageFPS = ((double) totalFrames / timeRunning) * 1000000000L; // ns
																				// -->
																				// secs
			averageUPS = ((double) (totalSkips + totalFrames) / timeRunning) * 1000000000L;
			statsIntervall = 0L;
			prevStatsTime = timeNow;

		}
	}

	/* ---- Listener and Handlers for Key Events ---- */
	private void addKeyListeners() {
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				int playerCode = isControl(keyCode);
				if (playerCode == NO_PLAYER)
					handleNonControls(keyCode, e);
				else {
					playerPushesControl[playerCode] = true;
					if (controls[playerCode][0] == keyCode)
						playerDirectionLeft[playerCode] = true;
					else
						playerDirectionLeft[playerCode] = false;
				}
			}
		});

		addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				int keyCode = e.getKeyCode();
				int playerCode = isControl(keyCode);
				if (playerCode < NO_PLAYER)
					playerPushesControl[playerCode] = false;
			}
		});
	}

	private int isControl(int keyCode) {
		for (int a : controls[0])
			if (a == keyCode)
				return PLAYER_1;
		for (int a : controls[1])
			if (a == keyCode)
				return PLAYER_2;
		return NO_PLAYER;
	}

	private void handleNonControls(int keyCode, KeyEvent e) {
		if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_Q
				|| keyCode == KeyEvent.VK_END
				|| (keyCode == KeyEvent.VK_C && e.isControlDown()))
			isRunning = false;
	}
}