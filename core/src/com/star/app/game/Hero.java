package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.star.app.controllers.GameController;
import com.star.app.screen.ScreenManager;
import com.star.app.screen.utils.Assets;

public class Hero {
    public enum SKill{
        HP_MAX(10,10), HP(20,20), WEAPON(100,1),MAGNET(50,40);

        int cost;
        int power;

        SKill(int cost, int power) {
            this.cost = cost;
            this.power = power;
        }
    }

    private GameController gc;
    private TextureRegion texture;
    private Vector2 position;
    private Vector2 velocity;
    private float angle;
    private float enginePower;
    private float fireTimer;
    private int score;
    private int scoreView;
    private int hpMax;
    private int hp;
    private StringBuilder sb;
    private Circle hitArea;
    private Weapon currentWeapon;
    private int money;
    private Shop shop;
    private Weapon[] weapons;
    private int weaponNum;
    private int magnetField;


    public int getMagnetField() {
        return magnetField;
    }

    public Shop getShop() {
        return shop;
    }

    public int getScore() {
        return score;
    }

    public int getHpMax() {
        return hpMax;
    }

    public int getMoney() {
        return money;
    }

    public int getHp() {
        return hp;
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

    public void addScore(int amount) {
        score += amount;
    }

    public boolean isAlive(){
        return hp>0;
    }

//проверка: есть ли деньги у героя
    public boolean isMoneyEnough (int amount){
        return money >= amount;
    }

//метод уменьшения денег (плата за PowerUps)
    public void decreaseMoney(int amount){
        money -= amount;
    }

//снятие с паузы
    public void resumeGame(boolean active){
        gc.setActive(active);
    }

    public Hero(GameController gc) {
        this.gc = gc;
        this.texture = Assets.getInstance().getAtlas().findRegion("ship");
        this.position = new Vector2(ScreenManager.SCREEN_WIDTH / 2, ScreenManager.SCREEN_HEIGHT / 2);
        this.velocity = new Vector2(0, 0);
        this.angle = 0.0f;
        this.enginePower = 500.0f;
        this.hpMax = 100;
        this.hp = hpMax;
        this.money = 150;
        this.magnetField = 0;
        this.sb = new StringBuilder();
        this.shop = new Shop(this);
        this.hitArea = new Circle(position, 29);
        this.weaponNum =0;
        createWeapons();
        this.currentWeapon = weapons[weaponNum];

    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        sb.setLength(0);
        sb.append("SCORE: ").append(scoreView).append("\n");
        sb.append("LIFE: ").append(hp).append(" / ").append(hpMax).append("\n");
        sb.append("BULLETS: ").append(currentWeapon.getCurBullets()).append(" / ").append(currentWeapon.getMaxBullets()).append("\n");
        sb.append("MONEY: ").append(money).append("\n");
        sb.append("MAGNET: ").append(magnetField).append("\n");
        font.draw(batch, sb, 20, 700);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - 32, position.y - 32, 32, 32,
                64, 64, 1, 1,
                angle);
    }
//урон жизни
    public void takeDamage(int amount) {
        hp -= amount;
    }

//апгрейд за деньги
    public boolean upgrade(SKill skill){
        switch (skill){
            case HP_MAX:
                hpMax += SKill.HP_MAX.power;
                return true;
            case HP:
                if(hp + SKill.HP.power <= hpMax){
                    hp += SKill.HP.power;
                    return true;
                }
                break;
            case WEAPON:
                if(weaponNum < weapons.length-1){
                    weaponNum ++;
                    currentWeapon = weapons[weaponNum];
                    return true;
                }
            case MAGNET:
                if (magnetField < 100) {
                    magnetField += SKill.MAGNET.power;
                    return true;
                }
        }
        return false;
    }
//добавка при поднятии PowerUps
    public void consume(PowerUp p) {
        switch (p.getType()) {
            case MEDKIT:
                hp += p.getPower();
                break;
            case MONEY:
                money += p.getPower();
                break;
            case AMMOS:
                currentWeapon.addAmmos( p.getPower()) ;
                break;
        }
    }

    public void update(float dt) {
        fireTimer += dt;
        updateScore(dt);

//клавиша управления огнем

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            tryToFire();
        }
//клавиши управления кораблем

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            angle += 180.0f * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            angle -= 180.0f * dt;
        }

//клавиша движения вперед

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocity.x += MathUtils.cosDeg(angle) * enginePower * dt;
            velocity.y += MathUtils.sinDeg(angle) * enginePower * dt;

            float bx = position.x + MathUtils.cosDeg(angle + 180) * 20;
            float by = position.y + MathUtils.sinDeg(angle + 180) * 20;
            for (int i = 0; i < 3; i++) {
                gc.getParticleController().setup(bx + MathUtils.random(-4, 4), by + MathUtils.random(-4, 4),
                        velocity.x * -0.3f + MathUtils.random(-20, 20), velocity.y * -0.3f + MathUtils.random(-20, 20),
                        0.5f, 1.2f, 0.2f,
                        1.0f, 0.5f, 0, 1,
                        1, 1, 1, 0);
            }
        }

//клавиша движения назад

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocity.x += MathUtils.cosDeg(angle) * -enginePower / 2 * dt;
            velocity.y += MathUtils.sinDeg(angle) * -enginePower / 2 * dt;
//левый двигатель
            float bx = position.x + MathUtils.cosDeg(angle + 90) * 20;
            float by = position.y + MathUtils.sinDeg(angle + 90) * 20;
            for (int i = 0; i < 2; i++) {
                gc.getParticleController().setup(bx + MathUtils.random(-4, 4), by + MathUtils.random(-4, 4),
                        velocity.x * 0.1f + MathUtils.random(-20, 20), velocity.y * 0.1f + MathUtils.random(-20, 20),
                        0.4f, 1.2f, 0.2f,
                        1.0f, 0.5f, 0, 1,
                        1, 1, 1, 0);
            }
//правый двигатель
            bx = position.x + MathUtils.cosDeg(angle - 90) * 20;
            by = position.y + MathUtils.sinDeg(angle - 90) * 20;
            for (int i = 0; i < 2; i++) {
                gc.getParticleController().setup(bx + MathUtils.random(-4, 4), by + MathUtils.random(-4, 4),
                        velocity.x * 0.1f + MathUtils.random(-20, 20), velocity.y * 0.1f + MathUtils.random(-20, 20),
                        0.4f, 1.2f, 0.2f,
                        1.0f, 0.5f, 0, 1,
                        1, 1, 1, 0);
            }

        }
        if (Gdx.input.isKeyPressed(Input.Keys.PAUSE)) {
            shop.setVisible(true);
            gc.setActive(true);
        }

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

//очки за астероиды

    private void updateScore(float dt) {
        if (scoreView < score) {
            scoreView += 2000 * dt;
            if (scoreView > score) {
                scoreView = score;
            }
        }
    }

//метод для стрельбы

    private void tryToFire() {
        if (fireTimer > currentWeapon.getFirePeriod()) {
            fireTimer = 0.0f;
            currentWeapon.fire();
        }
    }

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

    private void createWeapons(){
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
                new Weapon(gc, this, "Laser", 0.1f, 1, 600.0f, 1000,
                        new Vector3[]{
                                new Vector3(28, 0, 0),
                                new Vector3(28, 90, 20),
                                new Vector3(28, -90, -20)
                        }),
                new Weapon(gc, this, "Laser", 0.1f, 2, 600.0f, 1500,
                        new Vector3[]{
                                new Vector3(28, 90, 0),
                                new Vector3(28, 90, 15),
                                new Vector3(28, -90, 0),
                                new Vector3(28, -90, -15)
                        }),
                new Weapon(gc, this, "Laser", 0.1f, 3, 600.0f, 2000,
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
