package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Ball {
    Color color;
    float x;
    float y;
    int size;

    int radius;
    ScreenBounds screenBounds;
    Speed speed;
    float gravity;
    float airResistance;
    float friction;
    boolean gravityEnabled;

    public Ball(Color color, int x, int y, int size, float xSpeed, float ySpeed) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.size = size;

        this.radius = size / 2;
        this.screenBounds = new ScreenBounds(radius);
        this.speed = new Speed(xSpeed, ySpeed);

        this.gravity = 0.5f;
        this.airResistance = 0.01f;
        this.friction = 0.1f;
        this.gravityEnabled = false;
    }

    public void update() {
        // Apply air resistance
        speed.x *= (1 - airResistance);
        speed.y *= (1 - airResistance);

        // If speed falls below threshold, set it to zero
        if (Math.abs(speed.x) < 0.01f) {
            speed.x = 0;
        }
        if (Math.abs(speed.y) < 0.01f) {
            speed.y = 0;
        }

        checkBorderCollision();
            if (gravityEnabled) {
                speed.y -= gravity;
            }
            x += speed.x;
            y += speed.y;
    }

    public void toggleGravity() {
        gravityEnabled = !gravityEnabled;
    }

    public void draw(ShapeRenderer shape) {
        shape.circle(x, y, radius);
        shape.setColor(color);
    }

    public void bounce(Ball other) {
        // Calculate the distance between the balls' centers
        float dx = x - other.x;
        float dy = y - other.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // Calculate the minimum translation distance
        float minDistance = radius + other.radius - distance;

        // Add a small padding to the minimum translation distance
        float padding = 0.01f; // Adjust this value as needed
        minDistance += padding;

        // Normalize the distance vector
        float dxNormalized = dx / distance;
        float dyNormalized = dy / distance;

        // Push the balls apart by the full minimum translation distance
        x += dxNormalized * minDistance;
        other.x -= dxNormalized * minDistance;
        y += dyNormalized * minDistance;
        other.y -= dyNormalized * minDistance;

        // Calculate the angle of the collision
        float collisionAngle = (float) Math.atan2(dy, dx);

        // Calculate the speeds of the balls in the x and y directions
        float speed1 = (float) Math.sqrt(speed.x * speed.x + speed.y * speed.y);
        float speed2 = (float) Math.sqrt(other.speed.x * other.speed.x + other.speed.y * other.speed.y);

        // Calculate the directions of the balls
        float direction1 = (float) Math.atan2(speed.y, speed.x);
        float direction2 = (float) Math.atan2(other.speed.y, other.speed.x);

        // Calculate the new speeds of the balls after the collision
        float newSpeed1 = speed2 * (float) Math.cos(direction2 - collisionAngle);
        float newSpeed2 = speed1 * (float) Math.cos(direction1 - collisionAngle);

        // Calculate the new velocities of the balls after the collision
        float newSpeedX1 = newSpeed1 * (float) Math.cos(collisionAngle) + newSpeed2 * (float) Math.cos(collisionAngle + Math.PI / 2);
        float newSpeedY1 = newSpeed1 * (float) Math.sin(collisionAngle) + newSpeed2 * (float) Math.sin(collisionAngle + Math.PI / 2);
        float newSpeedX2 = newSpeed2 * (float) Math.cos(collisionAngle) + newSpeed1 * (float) Math.cos(collisionAngle + Math.PI / 2);
        float newSpeedY2 = newSpeed2 * (float) Math.sin(collisionAngle) + newSpeed1 * (float) Math.sin(collisionAngle + Math.PI / 2);

        // Calculate the total kinetic energy before and after the collision
        float initialTotalEnergy = 0.5f * size * speed1 * speed1 + 0.5f * other.size * speed2 * speed2;
        float finalTotalEnergy = 0.5f * size * (newSpeedX1 * newSpeedX1 + newSpeedY1 * newSpeedY1) + 0.5f * other.size * (newSpeedX2 * newSpeedX2 + newSpeedY2 * newSpeedY2);

        // Scale the final speeds to conserve the total kinetic energy
        float energyScaleFactor = (float) Math.sqrt(initialTotalEnergy / finalTotalEnergy);
        newSpeedX1 *= energyScaleFactor;
        newSpeedY1 *= energyScaleFactor;
        newSpeedX2 *= energyScaleFactor;
        newSpeedY2 *= energyScaleFactor;

        // Update the speeds of the balls
        speed.x = newSpeedX1;
        speed.y = newSpeedY1;
        other.speed.x = newSpeedX2;
        other.speed.y = newSpeedY2;
    }

    private float[] handleBorderCollision(float position, float speed, float min, float max) {
        float borderPadding = 0.01f; // Small distance to move the ball away from the border

        if (position < min) {
            speed *= -1 * (1 - friction);
            position = min + borderPadding;
        } else if (position > max) {
            speed *= -1 * (1 - friction);
            position = max - borderPadding;
        }
        return new float[]{position, speed};
    }

    public void checkBorderCollision() {
        float[] xResult = handleBorderCollision(x, speed.x, screenBounds.xMin, screenBounds.xMax);
        x = xResult[0];
        speed.x = xResult[1];

        float[] yResult = handleBorderCollision(y, speed.y, screenBounds.yMin, screenBounds.yMax);
        y = yResult[0];
        speed.y = yResult[1];
    }

    public void checkBallCollision(Ball other) {
        float dx = x - other.x;
        float dy = y - other.y;
        int distance = (int) Math.sqrt(dx * dx + dy * dy);
        if (distance < radius + other.radius) {
            bounce(other);
        }
    }
}