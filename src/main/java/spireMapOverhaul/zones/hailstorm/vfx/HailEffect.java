package spireMapOverhaul.zones.hailstorm.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class HailEffect extends AbstractGameEffect {
    private float x;
    private float y;
    private float vY;
    private float vX;
    private float scaleY;
    private int frame = 0;
    private float animTimer = 0.05F;
    private static final int W = 32;

    public HailEffect() {
        this.x = MathUtils.random(100.0F * Settings.scale, 2420.0F * Settings.scale);
        this.y = (float) Settings.HEIGHT + MathUtils.random(20.0F, 300.0F) * Settings.scale;
        this.frame = MathUtils.random(3);
        this.rotation = MathUtils.random(05.0F, 10.0F);
        this.scale = MathUtils.random(.05F, .25F);
        this.scaleY = MathUtils.random(1.20F, 1.25F);
        if (this.scale < 1.5F) {
            this.renderBehind = true;
        }

        this.vY = MathUtils.random(600.0F, 900.0F) * this.scale * Settings.scale;
        this.vX = MathUtils.random(-300.0F, -50.0F) * this.scale * Settings.scale;
        this.scale *= Settings.scale;
        if (MathUtils.randomBoolean()) {
            this.rotation += 180.0F;
        }
        this.renderBehind = MathUtils.randomBoolean();

        this.color = Color.WHITE.cpy();;
        this.duration = 4.0F;
    }

    public void update() {
        this.y -= this.vY * Gdx.graphics.getDeltaTime() * 2F;
        this.x += this.vX * Gdx.graphics.getDeltaTime() * 2F;
        this.animTimer -= Gdx.graphics.getDeltaTime() / this.scale;
        if (this.animTimer < 0.0F) {
            this.animTimer += 0.05F;
            ++this.frame;
            if (this.frame > 3) {
                this.frame = 0;
            }
        }

        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            this.isDone = true;
        }
    }

    public void render(SpriteBatch sb) {
        switch (this.frame) {
            case 0:
                this.renderImg(sb, ImageMaster.PETAL_VFX[0], false, false);
                break;
            case 1:
                this.renderImg(sb, ImageMaster.PETAL_VFX[1], false, false);
                break;
            case 2:
                this.renderImg(sb, ImageMaster.PETAL_VFX[0], true, true);
                break;
            case 3:
                this.renderImg(sb, ImageMaster.PETAL_VFX[1], true, true);
        }

    }

    public void dispose() {
    }

    private void renderImg(SpriteBatch sb, Texture img, boolean flipH, boolean flipV) {
        sb.setColor(this.color);
        sb.draw(img, this.x, this.y, 16.0F, 16.0F, 32.0F, 32.0F, this.scale, this.scale * this.scaleY, this.rotation, 0, 0, 32, 32, flipH, flipV);
        this.color.a = MathUtils.random(0.4F, 0.6F);
    }
}