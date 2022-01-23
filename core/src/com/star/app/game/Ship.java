package com.star.app.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.star.app.controllers.GameController;
import com.star.app.screen.ScreenManager;

public class Ship {
    protected GameController gc;
    protected TextureRegion texture;
    protected Vector2 position;
    protected Vector2 velocity;
    protected float angle;
    protected float enginePower;
    protected float fireTimer;
    protected int hpMax;
    protected int hp;
    protected Circle hitArea;
    protected Weapon currentWeapon;
    protected Weapon[] weapons;
    protected int weaponNum;
    protected OwnerType ownerType;

    public OwnerType getOwnerType() {
        return ownerType;
    }


//метод для стрельбы
    public void tryToFire() {
        if (fireTimer > currentWeapon.getFirePeriod()) {
            fireTimer = 0.0f;
            currentWeapon.fire();
        }
    }


    public void update(float dt) {
        fireTimer += dt;
        position.mulAdd(velocity, dt);
        hitArea.setPosition(position);
//коэффициент замедления при отпускаии кнопки вперед или назад
        float stopKoef = 1.0f - 0.8f * dt;
        if (stopKoef < 0.0f) {
            stopKoef = 0.0f;
        }
        velocity.scl(stopKoef);
        checkSpaceBorders();
    }

//урон жизни
    public void takeDamage(int amount) {
        hp -= amount;
    }

//ускорение
    public void accelerate(float dt){
        velocity.x += MathUtils.cosDeg(angle) * enginePower * dt;
        velocity.y += MathUtils.sinDeg(angle) * enginePower * dt;
    }

//замедление
    public void brake (float dt){
        velocity.x += MathUtils.cosDeg(angle) * -enginePower / 2 * dt;
        velocity.y += MathUtils.sinDeg(angle) * -enginePower / 2 * dt;
    }


    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - 32, position.y - 32, 32, 32,
                64, 64, 1, 1,
                angle);
    }

    public boolean isAlive(){
        return hp>0;
    }

//проверка корабля на столкновение с препятствиями
    private void checkSpaceBorders() {
        if (position.x < 32) {
            position.x = 32;
            velocity.x *= -0.5f;
        }
        if (position.x > ScreenManager.SCREEN_WIDTH - 32f) {
            position.x = ScreenManager.SCREEN_WIDTH - 32f;
            velocity.x *= -0.5f;
        }
        if (position.y < 32f) {
            position.y = 32f;
            velocity.y *= -0.5f;
        }
        if (position.y > ScreenManager.SCREEN_HEIGHT - 32f) {
            position.y = ScreenManager.SCREEN_HEIGHT - 32f;
            velocity.y *= -0.5f;
        }
    }

    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    public float getAngle() {
        return angle;
    }

    public Circle getHitArea() {
        return hitArea;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Vector2 getPosition() {
        return position;
    }



    public Ship(GameController gc, int hpMax,float enginePower) {
        this.gc = gc;
        this.angle = 0.0f;
        this.hpMax = hpMax;
        this.hp = hpMax;
        this.enginePower = enginePower;
        createWeapons(gc.getLevel());
        this.currentWeapon = weapons[weaponNum];
    }
//склад оружия
    private void createWeapons(int level){
        weapons = new Weapon[]{
                new Weapon(gc, this, "Laser", 0.2f, 1, 300.0f, 300,
                        new Vector3[]{
                                new Vector3(28, 90, 0),
                                new Vector3(28, -90, 0)
                        }),
                new Weapon(gc, this, "Laser", 0.2f, 1, 600.0f, 500,
                        new Vector3[]{
                                new Vector3(28, 0, 0),
                                new Vector3(28, 90, 20),
                                new Vector3(28, -90, -20)
                        }),
                new Weapon(gc, this, "Laser", 0.1f, 1, 600.0f, 700,
                        new Vector3[]{
                                new Vector3(28, 0, 0),
                                new Vector3(28, 90, 15),
                                new Vector3(28, -90, -15)
                        }),
                new Weapon(gc, this, "Laser", 0.1f, 2, 600.0f, 1000,
                        new Vector3[]{
                                new Vector3(28, 90, 0),
                                new Vector3(28, 90, 15),
                                new Vector3(28, -90, 0),
                                new Vector3(28, -90, -15)
                        }),
                new Weapon(gc, this, "Laser", 0.1f, 3, 600.0f, 1500,
                        new Vector3[]{
                                new Vector3(28, 0, 0),
                                new Vector3(28, 90, 10),
                                new Vector3(28, 90, 20),
                                new Vector3(28, -90, -10),
                                new Vector3(28, -90, -20)
                        })
        };
    }
}
