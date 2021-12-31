package com.star.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Hero {
    private Texture texture;
    private Vector2 position;
    private float angle;
    private Vector2 lastDisplacement;

    public Vector2 getLastDisplacement() {
        return lastDisplacement;
    }

    public Hero() {
        this.texture = new Texture("ship.png");
        this.position = new Vector2(ScreenManager.SCREEN_WIDTH / 2, ScreenManager.SCREEN_HEIGHT / 2);
        this.angle = 0.0f;
        this.lastDisplacement = new Vector2(0, 0);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - 32, position.y - 32, 32, 32,
                64, 64, 1, 1,
                angle, 0, 0, 64, 64, false, false);
    }

    public void update(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            angle += 180.0f * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            angle -= 180.0f * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            position.x += MathUtils.cosDeg(angle) * 240.0f * dt;
            position.y += MathUtils.sinDeg(angle) * 240.0f * dt;
            lastDisplacement.set(MathUtils.cosDeg(angle) * 240.0f * dt,
                    MathUtils.sinDeg(angle) * 240.0f * dt);
        } else {
            lastDisplacement.set(0, 0);
        }

        if (position.x < 32) {
            position.x = 32;
        }


    }
}
