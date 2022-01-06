package com.star.app.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.star.app.controllers.GameController;

public class Weapon {
    private GameController gc;
    private Hero hero;
    private String title;
    private float firePeriod;
    private int damage;
    private float bulletSpeed;
    private int maxBullets;
    private int curBullets;

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

    public Weapon(GameController gc, Hero hero, String title, float firePeriod, int damage,
                  float bulletSpeed, int maxBullets, Vector3[] slots) {
        this.gc = gc;
        this.hero = hero;
        this.title = title;
        this.firePeriod = firePeriod;
        this.damage = damage;
        this.bulletSpeed = bulletSpeed;
        this.maxBullets = maxBullets;
        this.slots = slots;
        this.curBullets = maxBullets;
    }

    public void fire() {
        if (curBullets > 0) {
            curBullets--;
            for (int i = 0; i < slots.length; i++) {
                float x, y, vx, vy;
                x = hero.getPosition().x + MathUtils.cosDeg(hero.getAngle() + slots[i].y) * slots[i].x;
                y = hero.getPosition().y + MathUtils.sinDeg(hero.getAngle() + slots[i].y) * slots[i].x;
                vx = hero.getVelocity().x + bulletSpeed * MathUtils.cosDeg(hero.getAngle() + slots[i].z);
                vy = hero.getVelocity().y + bulletSpeed * MathUtils.sinDeg(hero.getAngle() + slots[i].z);
                gc.getBulletController().setup(x, y, vx, vy);
            }
        }
    }
}
