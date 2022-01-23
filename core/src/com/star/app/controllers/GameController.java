package com.star.app.controllers;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.star.app.game.*;
import com.star.app.screen.ScreenManager;
import com.star.app.screen.utils.Assets;

public class GameController {

    private Background background;
    private AsteroidController asteroidController;
    private BulletController bulletController;
    private ParticleController particleController;
    private PowerUpController powerUpController;
    private InfoController infoController;
    private BotController botController;
    private Hero hero;
    private Vector2 tempVec;
    private boolean active;
    private Stage stage;
    private int  a;
    private int level;
    private float timer;
    private Music music;
    private StringBuilder sb;



    public float getTimer() {
        return timer;
    }

    public int getLevel() {
        return level;
    }

    public Stage getStage() {
        return stage;
    }

    public BotController getBotController() {
        return botController;
    }

    public InfoController getInfoController() {
        return infoController;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public PowerUpController powerUpController() {
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

    public GameController(SpriteBatch batch) {
        this.background = new Background(this);
        this.hero = new Hero(this);
        this.asteroidController = new AsteroidController(this);
        this.bulletController = new BulletController(this);
        this.particleController = new ParticleController();
        this.powerUpController = new PowerUpController(this);
        this.infoController = new InfoController();
        this.botController= new BotController(this);
        this.tempVec = new Vector2();
        this.level = 1;
        this.sb = new StringBuilder();
        this.music = Assets.getInstance().getAssetManager().get("audio/mortal.mp3");
        this.music.setLooping(true);
        this.music.play();
        this.stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        stage.addActor(hero.getShop());

        Gdx.input.setInputProcessor(stage);
        generateBigAsteroid(1);
//        generateBigBot(1);

    }

//    public void generateBigBot(int n){
//        for (int i = 0; i < n; i++) {
//            botController.setup(MathUtils.random(0, ScreenManager.SCREEN_WIDTH),
//                    MathUtils.random(0, ScreenManager.SCREEN_HEIGHT));
//       }
//    }

    public void generateBigAsteroid(int n){
        for (int i = 0; i < n; i++) {
            asteroidController.setup(MathUtils.random(0, ScreenManager.SCREEN_WIDTH),
                    MathUtils.random(0, ScreenManager.SCREEN_HEIGHT),
                    MathUtils.random(-200, 200),
                    MathUtils.random(-200, 200), 1.0f);
        }
    }

    public void update(float dt) {
//   пауза в игре
        if (active) {
            return;
        }
        timer += dt;
        background.update(dt);
        hero.update(dt);
        asteroidController.update(dt);
        bulletController.update(dt);
        particleController.update(dt);
        powerUpController.update(dt);
        infoController.update(dt);
        botController.update(dt);
        checkCollisions();
        if (!hero.isAlive()) {
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAMEOVER, hero);
        }


            if(botController.getActiveList().size() == 0){
//                for (int i = 0; i < level; i++) {
            botController.setup(MathUtils.random(0, ScreenManager.SCREEN_WIDTH),
                    MathUtils.random(0, ScreenManager.SCREEN_HEIGHT));
//            }
        }

         if(asteroidController.getActiveList().size() == 0){
                level ++;
                generateBigAsteroid(level <= 3 ? level:3);
                timer = 0.0f;
        }
        stage.act(dt);
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
                if (a.takeDamage(2)) {      //урон астероиду при столкновении с кораблем
                    hero.addScore(a.getHpMax() * 50);
                }
                hero.takeDamage(level*2);  //урон герою при столкновении с астероидом
                sb.setLength(0);
                sb.append("Life -").append(level*2);
                infoController.setup(hero.getPosition().x,hero.getPosition().y,sb.toString(), Color.RED);
            }
        }

//отскакивание ботов от астероидов
        for (int i = 0; i < asteroidController.getActiveList().size(); i++) {
            Asteroid a = asteroidController.getActiveList().get(i);
            for (int j = 0; j < botController.getActiveList().size(); j++) {
                Bot b =  botController.getActiveList().get(j);

                if (a.getHitArea().overlaps(b.getHitArea())) {
                    float dst = a.getPosition().dst(b.getPosition());
                    float halfOverLen = (a.getHitArea().radius + b.getHitArea().radius - dst) / 2;
                    tempVec.set(b.getPosition()).sub(a.getPosition()).nor();
                    b.getPosition().mulAdd(tempVec, halfOverLen);
                    a.getPosition().mulAdd(tempVec, -halfOverLen);

// импульс при столкновении бота с астероидом
                    float sumScl = b.getHitArea().radius + a.getHitArea().radius;
                    b.getVelocity().mulAdd(tempVec, a.getHitArea().radius / sumScl * 100);
                    a.getVelocity().mulAdd(tempVec, -b.getHitArea().radius / sumScl * 100);
                }
            }
        }

//уничтожение астероидов пульками

        for (int i = 0; i < bulletController.getActiveList().size(); i++) {
            Bullet b = bulletController.getActiveList().get(i);
            for (int j = 0; j < asteroidController.getActiveList().size(); j++) {
                Asteroid a = asteroidController.getActiveList().get(j);
                if (a.getHitArea().contains(b.getPosition())) {

                    particleController.getEffectBuilder().bulletCollideWithAsteroid(b);

                    b.deactivate();
//очки герою за астероиды уничтоженные пульками

                    if (a.takeDamage(b.getOwner().getCurrentWeapon().getDamage())) {
                        if(b.getOwner().getOwnerType() == OwnerType.PLAYER){
                            hero.addScore(a.getHpMax() * 100);

//появление бонусов герою при уничтожении астероида

                            for (int k = 0; k < 3; k++) {
                                powerUpController.setup(a.getPosition().x, a.getPosition().y,a.getScale() * 0.3f);
                            }
                        }
                    }
                    break;
                }
            }
        }

//захват бонусов кораблем

        for (int i = 0; i < powerUpController.getActiveList().size(); i++) {
            PowerUp p = powerUpController.getActiveList().get(i);

//  магнит
            a = hero.getMagnetField();
            if ((p.getPosition()).cpy().sub(hero.getPosition()).len() <= a){
                tempVec.set(hero.getPosition()).sub(p.getPosition()).nor();
                p.getVelocity().mulAdd(tempVec, 100);
            }


            if(hero.getHitArea().contains(p.getPosition())){
                hero.consume(p);
                particleController.getEffectBuilder().takePowerUpEffect(p.getPosition().x ,p.getPosition().y,p.getType());

                p.deactivate();
            }
        }

        for (int i = 0; i < bulletController.getActiveList().size(); i++) {
            Bullet b = bulletController.getActiveList().get(i);

            if(b.getOwner().getOwnerType() == OwnerType.BOT){
                if(hero.getHitArea().contains(b.getPosition())){
                    hero.takeDamage(b.getOwner().getCurrentWeapon().getDamage());
                    b.deactivate();
                }
            }
            if(b.getOwner().getOwnerType() == OwnerType.PLAYER){
                for (int j = 0; j < botController.getActiveList().size(); j++) {
                    Bot bot =  botController.getActiveList().get(j);
                    if(bot.getHitArea().contains(b.getPosition())){
                        bot.takeDamage(b.getOwner().getCurrentWeapon().getDamage());
                        b.deactivate();
                    }
                }
            }
        }


    }
    public void dispose(){
        background.dispose();
    }
}
