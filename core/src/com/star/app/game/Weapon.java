package com.star.app.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.star.app.controllers.GameController;
import com.star.app.screen.utils.Assets;

public class Weapon {
    private GameController gc;
    private Ship ship;
    private String title;
    private float firePeriod;
    private int damage;
    private float bulletSpeed;
    private int maxBullets;
    private int curBullets;
    private Sound shootSound;

    // x - растояние от центра
    // y угол от основного направления корабля
    // z угол стрельбы
    private Vector3[] slots;

    public float getFirePeriod() {
        return firePeriod;
    }

    public int getDamage() {
        return damage;
    }

    public int getMaxBullets() {
        return maxBullets;
    }

    public int getCurBullets() {
        return curBullets;
    }

//боеприпасы

    public Weapon(GameController gc, Ship ship, String title, float firePeriod, int damage,
                  float bulletSpeed, int maxBullets, Vector3[] slots) {
        this.gc = gc;
        this.ship = ship;
        this.title = title;
        this.firePeriod = firePeriod;
        this.damage = damage;
        this.bulletSpeed = bulletSpeed;
        this.maxBullets = maxBullets;
        this.slots = slots;
        this.curBullets = maxBullets;
        this.shootSound = Assets.getInstance().getAssetManager().get("audio/shoot.mp3");

    }
//пульки
    public void fire() {
        if (curBullets > 0) {
            curBullets--;
            shootSound.play();
            for (int i = 0; i < slots.length; i++) {
                float x, y, vx, vy;
                x = ship.getPosition().x + MathUtils.cosDeg(ship.getAngle() + slots[i].y) * slots[i].x;
                y = ship.getPosition().y + MathUtils.sinDeg(ship.getAngle() + slots[i].y) * slots[i].x;
                vx = ship.getVelocity().x + bulletSpeed * MathUtils.cosDeg(ship.getAngle() + slots[i].z);
                vy = ship.getVelocity().y + bulletSpeed * MathUtils.sinDeg(ship.getAngle() + slots[i].z);
                gc.getBulletController().setup(ship, x, y, vx, vy);
            }
        }
    }

//бонус патронов
    public void addAmmos(int amount) {
        curBullets += amount;
        if (curBullets > maxBullets) {
            curBullets = maxBullets;
        }
    }
}
