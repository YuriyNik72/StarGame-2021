package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.controllers.GameController;
import com.star.app.screen.ScreenManager;
import com.star.app.screen.utils.Assets;

public class Hero extends Ship{
    public enum SKill{
        HP_MAX(10,10), HP(20,20), WEAPON(100,1),MAGNET(50,40);

        int cost;
        int power;

        SKill(int cost, int power) {
            this.cost = cost;
            this.power = power;
        }
    }

    private int score;
    private int scoreView;
    private StringBuilder sb;
    private int money;
    private Shop shop;
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

    public int getMoney() {
        return money;
    }

    public int getHpMax() {
        return hpMax;
    }

    public int getHp() {
        return hp;
    }

    public void addScore(int amount) {
        score += amount;
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
        super(gc,1000,500);
        this.position = new Vector2(ScreenManager.SCREEN_WIDTH / 2, ScreenManager.SCREEN_HEIGHT / 2);
        this.texture = Assets.getInstance().getAtlas().findRegion("ship");
        this.hitArea = new Circle(position, 29);
        this.velocity = new Vector2(0, 0);
        this.money = 1500;
        this.sb = new StringBuilder();
        this.shop = new Shop(this);
        this.ownerType = OwnerType.PLAYER;
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
                if (magnetField < 200) {
                    magnetField += SKill.MAGNET.power;
                    return true;
                }
        }
        return false;
    }
//добавка при поднятии PowerUps
    public void consume(PowerUp p) {
        sb.setLength(0);
        switch (p.getType()) {
            case MEDKIT:
                int oldHp = hp;
                hp += p.getPower();
                if (hp > hpMax){
                    hp = hpMax;
                }
                sb.append("Life +").append(hp - oldHp);
                gc.getInfoController().setup(p.getPosition().x,p.getPosition().y,sb.toString(), Color.GREEN);
                break;
            case MONEY:
                money += p.getPower();
                sb.append("MONEY +").append(p.getPower());
                gc.getInfoController().setup(p.getPosition().x,p.getPosition().y,sb.toString(), Color.YELLOW);
                break;
            case AMMOS:
                currentWeapon.addAmmos( p.getPower()) ;
                sb.append("AMMOS +").append(p.getPower());
                gc.getInfoController().setup(p.getPosition().x,p.getPosition().y,sb.toString(), Color.ORANGE);
                break;
        }
    }

    public void update(float dt) {
        super.update(dt);
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
            accelerate(dt);

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
             brake(dt);

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
//управление паузой
        if (Gdx.input.isKeyPressed(Input.Keys.PAUSE)) {
            shop.setVisible(true);
            gc.setActive(true);
        }
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
}
