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
    private PowerUpController powerUpController;
    private Hero hero;
    private Vector2 tempVec;
    private boolean active;

    public void setActive(boolean active) {
        this.active = active;
    }

    public PowerUpController getFirstAidKitController() {
        return powerUpController;
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
        this.powerUpController = new PowerUpController(this);
        this.tempVec = new Vector2();

        for (int i = 0; i < 3; i++) {
            asteroidController.setup(MathUtils.random(0, ScreenManager.SCREEN_WIDTH),
                    MathUtils.random(0, ScreenManager.SCREEN_HEIGHT),
                    MathUtils.random(-200, 200),
                    MathUtils.random(-200, 200), 1.0f);
        }
    }

    public void update(float dt) {
//        пауза в игре
        if(active){
            return;
        }
        background.update(dt);
        hero.update(dt);
        asteroidController.update(dt);
        bulletController.update(dt);
        particleController.update(dt);
        powerUpController.update(dt);
        checkCollisions();
        if(!hero.isAlive()){
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAMEOVER,hero);
        }
    }

//игровая логика

    private void checkCollisions() {

//уничтожение астероидов кораблем

        for (int i = 0; i < asteroidController.getActiveList().size(); i++) {
            Asteroid a = asteroidController.getActiveList().get(i);
            if (a.getHitArea().overlaps(hero.getHitArea())) {
                float dst = a.getPosition().dst(hero.getPosition());
                float halfOverLen = (a.getHitArea().radius + hero.getHitArea().radius - dst) / 2;
                tempVec.set(hero.getPosition()).sub(a.getPosition()).nor();
                hero.getPosition().mulAdd(tempVec, halfOverLen);
                a.getPosition().mulAdd(tempVec, -halfOverLen);

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

//уничтожение астероидов пульками

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
//очки герою за астероиды уничтоженные пудьками

                    if (a.takeDamage(hero.getCurrentWeapon().getDamage())) {
                        hero.addScore(a.getHpMax() * 100);

//появление бонусов герою при уничтожении астероида

                        for (int k = 0; k < 3; k++) {
                            powerUpController.setup(a.getPosition().x, a.getPosition().y,a.getScale() * 0.25f);
                        }
                    }
                    break;
                }
            }
        }

//захват бонусов кораблем

        for (int i = 0; i < powerUpController.getActiveList().size(); i++) {
            PowerUp p = powerUpController.getActiveList().get(i);
            if(hero.getHitArea().contains(p.getPosition())){
                hero.consume(p);
                particleController.getEffectBuilder().takePowerUpEffect(p.getPosition().x,p.getPosition().y );
                p.deactivate();
            }
        }
    }
//    public void dispose(){
//        background.dispose();
//    }
}
