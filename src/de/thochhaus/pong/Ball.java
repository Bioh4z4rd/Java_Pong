package de.thochhaus.pong;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Ball {

	private static final int BALLSIZE = 20;

	private double touchDistance = 0.0;
	private double speedIncr	 = 0.7;
	private double moveDirX		 = 0.0;
	private double moveDirY		 = 1.0;
	private double cutPointX	 = 0.0;
	private double cutPointY  	 = 0.0;
	
	private Point moveDirection;
	private Point location;
	private Point cutPoint;
	private Brick[] tokens;
	private int[] brickSize;

	public Ball(Brick a, Brick b) {

		tokens = new Brick[2];
		tokens[0] = a;
		tokens[1] = b;

		brickSize = new int[2];
		brickSize = tokens[PongPanel.PLAYER_1].getSize();
		
		touchDistance = ((double) brickSize[0] * 2) + (BALLSIZE / 2);
		location = new Point(250, 200);
		moveDirection = new Point(0, 1);
		cutPoint = new Point(0, 0);

	}

	public void move() {

		if (touchesBrick()) {
			moveDirY += speedIncr;
			speedIncr *= -1.0;
			moveDirY *= -1.0;
			moveDirX *= -1.0;
		}

		location.y += moveDirY;
		location.x += moveDirX;

	}

	public void draw(Graphics g) {

		g.setColor(Color.BLUE);
		g.fillOval(location.x, location.y, BALLSIZE, BALLSIZE);
	}

	private boolean touchesBrick() {
		Point tokenLocation;
		double yQr;
		if (moveDirY < 0.0) {
			// TopBrick
			tokenLocation = tokens[PongPanel.PLAYER_1].getPosMid();
			yQr = (location.y - 10 -(tokenLocation.y - (brickSize[0] * 2) ));
		} else {
			// BotBrick
			tokenLocation = tokens[PongPanel.PLAYER_2].getPosMid();
			yQr = ((tokenLocation.y + 20 + (brickSize[0] * 2)) - location.y);
		}
		double xQr = (location.x - tokenLocation.x);
		double distance = Math.sqrt((xQr * xQr) + (yQr * yQr));

		if (distance <= touchDistance) {
			cutCalculation(tokenLocation);
			return true;
		}
		return false;
	}

	private void cutCalculation(Point tokenLocation) {

		Point dirVector = new Point(tokenLocation.x - location.x,
				tokenLocation.y - location.y);

		System.out.println(dirVector.x+"  "+dirVector.y);
		cutPointX = dirVector.x + (BALLSIZE / 2);
		cutPointY = dirVector.x + (BALLSIZE / 2);

		double absDirVector = Math.sqrt((dirVector.x * dirVector.x)
				+ (dirVector.y * dirVector.y));

		double reflectionVecX = 0.0, reflectionVecY = 0.0;
		reflectionVecX = (1 / absDirVector) * dirVector.x;
		reflectionVecY = (1 / absDirVector) * dirVector.y;
		
		System.out.println("Reflection Vector X: "+reflectionVecX+" Y: "+reflectionVecY);
		moveDirX = reflectionVecX;
		moveDirY = reflectionVecY;
	}
}
