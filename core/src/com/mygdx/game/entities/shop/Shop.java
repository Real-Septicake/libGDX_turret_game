package com.mygdx.game.entities.shop;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.TurretGame;
import com.mygdx.game.components.TurretComponent;
import com.mygdx.game.entities.turrets.TurretRoot;

import java.util.HashMap;

public class Shop {
    private static final ComponentMapper<TurretComponent> turretM = ComponentMapper.getFor(TurretComponent.class);

    public static final BitmapFont font = new BitmapFont(Gdx.files.internal("store_font.fnt"), Gdx.files.internal("store_font.png"), false);
    public static final Texture shop = new Texture("ShopBG.png");
    public static final TextureRegion closedShop = new TextureRegion(shop, 240, 0, 40, 100);
    public static final TextureRegion openShop = new TextureRegion(shop, 240, 800);

    private static final Rectangle closeButtonBounds = new Rectangle(TurretGame.WIDTH - 240, TurretGame.HEIGHT - 124, 40, 100);
    private static final Rectangle openButtonBounds = new Rectangle(TurretGame.WIDTH - 40, TurretGame.HEIGHT - 124, 40, 100);
    private static final Rectangle targetButtonBounds = new Rectangle(TurretGame.WIDTH - 100 - 27, TurretGame.HEIGHT - 120 - 15, 54, 10);

    private static final GlyphLayout[] targeting = new GlyphLayout[] {
            new GlyphLayout(font, "First"),
            new GlyphLayout(font, "Last"),
            new GlyphLayout(font, "Close"),
            new GlyphLayout(font, "Far"),
    };

    private static final HashMap<TurretRoot, GlyphLayout> turretNames = new HashMap<>();
    private static final HashMap<Upgrade, GlyphLayout[]> upgradeNames = new HashMap<>();

    public static boolean open = false;

    public static Entity target = null;

    static ShapeRenderer shape = new ShapeRenderer();

    public static void open(Entity target) {
        Shop.target = target;
        open = true;
    }

    public static void applyUpgrade(Upgrade upgrade) {
        TurretComponent t = turretM.get(target);
        t.range = upgrade.range;
        t.damage = upgrade.damage;
        if(upgrade.newAct != null)
            t.act = upgrade.newAct;
    }

    public static GlyphLayout getTurretName(TurretRoot turret) {
        GlyphLayout layout = turretNames.get(turret);
        if(layout == null) {
            layout = new GlyphLayout(font, turret.name());
            turretNames.put(turret, layout);
        }
        return layout;
    }

    public static GlyphLayout[] getUpgradeText(Upgrade upgrade) {
        GlyphLayout[] layouts = upgradeNames.get(upgrade);
        if(layouts == null) {
            GlyphLayout name = new GlyphLayout(font, upgrade.name);
            GlyphLayout price = new GlyphLayout(font, "$"+upgrade.cost);
            layouts = new GlyphLayout[]{name, price};
            upgradeNames.put(upgrade, layouts);
        }
        return layouts;
    }

    public static void render(SpriteBatch batch) {
        batch.begin();
        if(open) {
            batch.draw(openShop, TurretGame.WIDTH - 240, 0);
            if(target != null) {
                TurretComponent t = turretM.get(target);
                GlyphLayout layout = getTurretName(t.type);
                font.draw(batch, layout, TurretGame.WIDTH - 100 - layout.width/2, TurretGame.HEIGHT - 100 - layout.height/2);
                GlyphLayout target = targeting[t.targeting];
                float x = TurretGame.WIDTH - 100 - target.width/2;
                float y = TurretGame.HEIGHT - 120 - layout.height/2;
                font.draw(batch, target, x, y);
            }
        } else
            batch.draw(closedShop, TurretGame.WIDTH - 40, TurretGame.HEIGHT - 24 - 100);
        batch.end();
    }

    public static boolean handleClick(float x, float y) {
        if(open) {
            if(closeButtonBounds.contains(x, y)) {
                close();
                return true;
            }
            if(target != null) {
                if(targetButtonBounds.contains(x, y)){
                    TurretComponent t = turretM.get(target);
                    if (t.targeting < targeting.length - 1) t.targeting++;
                    else t.targeting = 0;
                    System.out.println(targeting[t.targeting].width);
                }
            }
        } else {
            if(openButtonBounds.contains(x, y)) {
                open = true;
                return true;
            }
        }
        return false;
    }

    public static void close() {
        open = false;
        target = null;
    }

    static {
        GlyphLayout t = new GlyphLayout(font, "Strong");
        System.out.println(t.width);
        System.out.println(t.height);
    }
}
