package de.thochhaus.pong;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Ball {

	private static final int BALLSIZE = 20;
	
	private Point moveDirection;
	private Point location;
	private Brick[] tokens;
	private double[] posY;
	private int[][] brickSize;
	
	
	public Ball (Brick a, Brick b) {
	
		tokens = new Brick[2];
		tokens[0] = a;
		tokens[1] = b;
		
		location = new Point(250 ,200);
		moveDirection = new Point(0, 1);
		
		Point pos1, pos2;
		pos1 = tokens[PongPanel.PLAYER_1].getPos();
		pos2 = tokens[PongPanel.PLAYER_1].getPos();
		posY = new double[2];
		posY[PongPanel.PLAYER_1] = pos1.getY();
		posY[PongPanel.PLAYER_2] = pos2.getY();
		
		brickSize = new int[2][2];
		brickSize[PongPanel.PLAYER_1] = tokens[PongPanel.PLAYER_1].getSize();
		brickSize[PongPanel.PLAYER_2] = tokens[PongPanel.PLAYER_2].getSize();
		
	}
	
	public void move() {
		
		if (touchesBrick())
			moveDirection.y *= -1.0;
		
		location.y += moveDirection.y;
		
	}
	
	public void draw(Graphics g) {
		
		g.setColor(Color.BLUE);
		g.fillOval(location.x, location.y, BALLSIZE, BALLSIZE);
	}
	
	private boolean touchesBrick() {
		if (( (moveDirection.y > 0.0) && (location.y == posY[PongPanel.PLAYER_1]) )
				|| ( (moveDirection.y < 0.0) && (location.y == posY[PongPanel.PLAYER_2]) )) 
			return true;
		return false;
	}
}
