package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
	ShapeRenderer shape;
	ArrayList<Ball> balls = new ArrayList<>();
	Random random = new Random();
	Ball selectedBall;
	Vector2 mousePressPosition;
	Vector2 mouseReleasePosition;

	private boolean isOverlapping(int x, int y, int radius) {
		for (Ball ball : balls) {
			float dx = x - ball.x;
			float dy = y - ball.y;
			int distance = (int) Math.sqrt(dx * dx + dy * dy);
			if (distance < radius + ball.radius) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void create() {
		shape = new ShapeRenderer();
		createBalls();
	}

	private void createBalls() {
		int ballCount = 20;
		int[] sizeLimits = {10, 50};
		int[] speedLimits = {1, 5};

		for (int i = 0; i < ballCount; i++) {
			Color color = getRandomColor();
			int radius = getRandomNumber(sizeLimits[0], sizeLimits[1]);
			ScreenBounds screenBounds = new ScreenBounds(radius);
			int x = getRandomNumber(radius, screenBounds.xMax - radius * 2);
			int y = getRandomNumber(radius, screenBounds.yMax - radius * 2);

			while (isOverlapping(x, y, radius)) {
				x = getRandomNumber(radius, screenBounds.xMax - radius * 2);
				y = getRandomNumber(radius, screenBounds.yMax - radius * 2);
			}

			int size = radius * 2;
			Speed speed = new Speed(
					getRandomNumber(speedLimits[0], speedLimits[1]),
					getRandomNumber(speedLimits[0], speedLimits[1])
			);
			balls.add(new Ball(color, x, y, size, speed.x, speed.y));
		}
	}

	private Color getRandomColor() {
		return new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1);
	}

	private int getRandomNumber(int min, int max) {
		return random.nextInt(max - min) + min;
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			if (selectedBall == null) {
				int mouseX = Gdx.input.getX();
				int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

				for (Ball ball : balls) {
					float dx = ball.x - mouseX;
					float dy = ball.y - mouseY;
					float distance = (float) Math.sqrt(dx * dx + dy * dy);

					if (distance <= ball.radius) {
						selectedBall = ball;
						break;
					}
				}
			} else if (mousePressPosition == null) {
				mousePressPosition = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
			}
		} else {
			if (selectedBall != null && mousePressPosition != null) {
				mouseReleasePosition = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
				throwBall();
			}
			selectedBall = null;
			mousePressPosition = null;
		}

		if (selectedBall != null) {
			selectedBall.x = Gdx.input.getX();
			selectedBall.y = Gdx.graphics.getHeight() - Gdx.input.getY(); // Y-coordinate is inverted in libGDX
		}

		shape.begin(ShapeRenderer.ShapeType.Filled);
		checkCollisions();
		updateAndDrawBalls();
		shape.end();
	}

	private void throwBall() {
		//TODO Fix the throw speed
		//TODO Not here but make it so balls settle properly
		//TODO For some reason balls will all disappear
		float throwSpeedScale = 0.1f;
		float dx = (mouseReleasePosition.x - mousePressPosition.x) * throwSpeedScale;
		float dy = (mouseReleasePosition.y - mousePressPosition.y) * throwSpeedScale;
		selectedBall.speed.x = dx;
		selectedBall.speed.y = dy;
	}

	private void checkCollisions() {
		for (Ball ball : balls) {
			for (Ball other : balls) {
				if (ball != other) {
					ball.checkBallCollision(other);
				}
			}
		}
	}

	private void updateAndDrawBalls() {
		for (Ball ball : balls) {
			ball.update();
			ball.draw(shape);
		}
	}
}