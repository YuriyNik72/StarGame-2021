package com.star.app.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.star.app.game.Background;
import com.star.app.game.Hero;
import com.star.app.screen.utils.Assets;


public class GameOverScreen extends AbstractScreen {
    private Background background;
    private BitmapFont font72;
    private BitmapFont font48;
    private BitmapFont font24;
    private StringBuilder sb;
    private Hero losingHero;
    private Music music;

    public void setLosingHero(Hero losingHero) {
        this.losingHero = losingHero;
    }

    public GameOverScreen(SpriteBatch batch) {
        super(batch);
        this.sb = new StringBuilder();
    }

    @Override
    public void show() {
        this.background =new Background(null);
        this.font72 = Assets.getInstance().getAssetManager().get("fonts/font72.ttf");
        this.font48 = Assets.getInstance().getAssetManager().get("fonts/font48.ttf");
        this.font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf");
        this.music = Assets.getInstance().getAssetManager().get("audio/gameover.mp3");
        this.music.setLooping(true);
        this.music.play();
    }

    public void update(float dt) {
        background.update(dt);
        if (Gdx.input.justTouched()){
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 1);
        batch.begin();
        background.render(batch);
        font72.draw(batch, "Game Over", 0, 600, 1280, Align.center, false);
        sb.setLength(0);
        sb.append("SCORE: ").append(losingHero.getScore()).append("\n");
        sb.append("LIFE: ").append(losingHero.getHp()).append(" / ").append(losingHero.getHpMax()).append("\n");
        sb.append("MONEY: ").append(losingHero.getMoney()).append("\n");
        font48.draw(batch, sb, 0, 400, 1280, Align.center, false);
        font24.draw(batch, "Click to screen to return to main menu... ",
                0, 60, 1280, Align.center, false);
        batch.end();
    }

    @Override
    public void dispose() {
        background.dispose();
    }
}
