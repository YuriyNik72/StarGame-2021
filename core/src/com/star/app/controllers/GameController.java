package com.star.app.controllers;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.*;
import com.star.app.screen.ScreenManager;

public class GameController {
    private Background background;
    private AsteroidController asteroidController;
    private BulletController bulletController;
    private ParticleController particleController;
    private FirstAidKitController firstAidKitController;
    private FirstAidKit firstAidKit;
    private Hero hero;
    private Vector2 tempVec;

    public FirstAidKitController getFirstAidKitController() {
        return firstAidKitController;
    }

    public ParticleController getParticleController() {
        return particleController;
    }

    public AsteroidController getAsteroidController() {
        return asteroidController;
    }

    public Hero getHero() {
        return hero;
    }

    public Background getBackground() {
        return background;
    }

    public BulletController getBulletController() {
        return bulletController;
    }

    public GameController() {
        this.background = new Background(this);
        this.hero = new Hero(this);
        this.asteroidController = new AsteroidController(this);
        this.bulletController = new BulletController(this);
        this.particleController = new ParticleController();
        this.firstAidKitController = new FirstAidKitController(this);
        this.firstAidKit = new FirstAidKit(this);
        this.tempVec = new Vector2();

        for (int i = 0; i < 3; i++) {
            asteroidController.setup(MathUtils.random(0, ScreenManager.SCREEN_WIDTH),
                    MathUtils.random(0, ScreenManager.SCREEN_HEIGHT),
                    MathUtils.random(-200, 200),
                    MathUtils.random(-200, 200), 1.0f);
        }
    }

    public void update(float dt) {
        background.update(dt);
        hero.update(dt);
        asteroidController.update(dt);
        bulletController.update(dt);
        particleController.update(dt);
        firstAidKitController.update(dt);
        firstAidKit.update(dt);
        checkCollisions();
        checkCollisionsApt();
    }
    private void checkCollisionsApt(){

        if (hero.getHitArea().overlaps(firstAidKit.getHitArea())){
            hero.plusDamage(20);
            firstAidKit.deactivate();
        }
    }

    private void checkCollisions() {

        for (int i = 0; i < asteroidController.getActiveList().size(); i++) {
            Asteroid a = asteroidController.getActiveList().get(i);
            if (a.getHitArea().overlaps(hero.getHitArea())) {
                float dst = a.getPosition().dst(hero.getPosition());
                float halfOverLen = (a.getHitArea().radius + hero.getHitArea().radius - dst) / 2;
                tempVec.set(hero.getPosition()).sub(a.getPosition()).nor();
                hero.getPosition().mulAdd(tempVec, halfOverLen);
                a.getPosition().mulAdd(tempVec, -halfOverLen);


// аптечка
                firstAidKitController.setup(a.getPosition().x +MathUtils.random(-4, 4) , a.getPosition().y +MathUtils.random(-4, 4) ,
                         0.0f ,  0.0f ,10.0f, 5.5f, 5.5f,
                        1.0f, 1.0f, 1.0f, 1, 1, 1, 1, 1);




// импульс при столкновении корабля с астероидом
                float sumScl = hero.getHitArea().radius + a.getHitArea().radius;
                hero.getVelocity().mulAdd(tempVec, a.getHitArea().radius / sumScl * 100);
                a.getVelocity().mulAdd(tempVec, -hero.getHitArea().radius / sumScl * 100);
//урон герою при столкновении корабля с астероидом
                if (a.takeDamage(2)) {
                    hero.addScore(a.getHpMax() * 50);
                }
                hero.takeDamage(2);
            }


        }



        for (int i = 0; i < bulletController.getActiveList().size(); i++) {
            Bullet b = bulletController.getActiveList().get(i);
            for (int j = 0; j < asteroidController.getActiveList().size(); j++) {
                Asteroid a = asteroidController.getActiveList().get(j);
                if (a.getHitArea().contains(b.getPosition())) {

                    particleController.setup(b.getPosition().x +MathUtils.random(-4, 4), b.getPosition().y + MathUtils.random(-4, 4),
                            b.getVelocity().x * -0.3f + MathUtils.random(-30, 30), b.getVelocity().y * -0.3f + MathUtils.random(-30, 30),
                            0.2f, 2.2f, 1.5f,
                            1.0f, 1.0f, 1.0f, 1,
                            0, 0, 1, 0);

                    b.deactivate();
                    if (a.takeDamage(1)) {
                        hero.addScore(a.getHpMax() * 100);
                    }
                    break;
                }
            }
        }

    }
}
