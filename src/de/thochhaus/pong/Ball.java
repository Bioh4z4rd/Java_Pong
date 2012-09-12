package de.thochhaus.pong;

import java.awt.Point;

public class Ball {

	private static final int BALLSIZE = 20;
	private static final double POSY_UNINITIALIZED = -1.0;
	
	private Point moveDirection;
	private Point location;
	private Brick[] tokens;
	private double[] posY;
	
	
	public Ball (Brick a, Brick b) {
	
		tokens = new Brick[2];
		tokens[0] = a;
		tokens[1] = b;
		
		posY = new double[]{POSY_UNINITIALIZED, POSY_UNINITIALIZED};
		
		location = new Point(250 ,200);
		moveDirection = new Point(0, 1);
		
	}
	
	public void move() {
		Point pos1, pos2;
		if ( posY[0] == POSY_UNINITIALIZED ){
			pos1 = tokens[PongPanel.PLAYER_1].getPos();
			pos2 = tokens[PongPanel.PLAYER_1].getPos();
			posY[PongPanel.PLAYER_1] = pos1.getX();
			posY[PongPanel.PLAYER_2] = pos2.getX();
		}
		
		int[] size1 = new int[2];
		int[] size2 = new int[2];
		size1 = tokens[PongPanel.PLAYER_1].getSize();
		size2 = tokens[PongPanel.PLAYER_1].getSize();
		
		
	}
	
	private boolean touchesBrick() {
		
	}
	
}
