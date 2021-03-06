package de.thochhaus.pong;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Brick {

	public static final int BRICK_WIDTH = 60;
	public static final int BRICK_HEIGHT = 10;

	private Point pos;
	private int canvasWidth;

	public Brick(int posx, int posy, int width) {

		pos = new Point(posx, posy);
		canvasWidth = width;

	}

	public int[] getSize() {
		return new int[] { BRICK_WIDTH, BRICK_HEIGHT };
	}

	public Point getPosMid() {
		return new Point(pos.x + (BRICK_WIDTH / 2), pos.y + (BRICK_HEIGHT / 2) );
	}

	/* ---- Movement Functions (only affect x-coordinates) ---- */
	public void moveRight() {
		if (pos.x + 10 > (canvasWidth - BRICK_WIDTH))
			pos.x = canvasWidth - BRICK_WIDTH;
		else
			pos.x += 10;
	}

	public void moveLeft() {
		if (pos.x - 10 < 0)
			pos.x = 0;
		else
			pos.x -= 10;
	}

	public void draw(Graphics g) {

		g.setColor(Color.RED);
		g.fillOval(pos.x, pos.y, BRICK_WIDTH, BRICK_HEIGHT);

	}

}
