package com.mygdx.game.entities.shop;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.TurretGame;
import com.mygdx.game.World;
import com.mygdx.game.components.TurretComponent;
import com.mygdx.game.entities.turrets.TurretRoot;

import java.util.ArrayList;
import java.util.HashMap;

public class Shop {
    private static final ComponentMapper<TurretComponent> turretM = ComponentMapper.getFor(TurretComponent.class);

    public static final BitmapFont font = new BitmapFont(Gdx.files.internal("store_font.fnt"), Gdx.files.internal("store_font.png"), false);
    public static final BitmapFont sellFont = new BitmapFont(Gdx.files.internal("sell.fnt"), Gdx.files.internal("sell.png"), false);
    public static final Texture shop = new Texture("ShopBG.png");
    public static final TextureRegion closedShop = new TextureRegion(shop, 240, 0, 40, 100);
    public static final TextureRegion openShop = new TextureRegion(shop, 240, 800);
    public static final TextureRegion sell = new TextureRegion(shop, 240, 100, 60, 30);

    private static final Rectangle closeButtonBounds = new Rectangle(TurretGame.WIDTH - 240, TurretGame.HEIGHT - 124, 40, 100);
    private static final Rectangle openButtonBounds = new Rectangle(TurretGame.WIDTH - 40, TurretGame.HEIGHT - 124, 40, 100);

    private static final Rectangle targetButtonBounds = new Rectangle(TurretGame.WIDTH - 100 - 27, TurretGame.HEIGHT - 120 - 15, 54, 10);
    private static final Rectangle sellButtonBounds = new Rectangle(1090, 20, 60, 30);

    private static final ArrayList<Page> pages = new ArrayList<>();
    private static int currentPage = 0;

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

    static World world;

    public static void setWorld(World world) {
        Shop.world = world;
    }

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
                renderTurret(batch, t.type, TurretGame.HEIGHT - 90);
                GlyphLayout layout = getTurretName(t.type);
                font.draw(batch, layout, TurretGame.WIDTH - 100 - layout.width/2, TurretGame.HEIGHT - 140 - layout.height/2);
                GlyphLayout target = targeting[t.targeting];
                float x = TurretGame.WIDTH - 100 - target.width/2;
                float y = TurretGame.HEIGHT - 160 - layout.height/2;
                font.draw(batch, target, x, y);
                batch.draw(sell, 1090, 20);
                sellFont.draw(batch, "$" + t.value, 1160, 43);
            } else {
                for(int i = 0; i < pages.get(currentPage).count; i++) {
                    TurretRoot type = pages.get(currentPage).get(i);
                    if(type == null) throw new UnknownError("How the fuck");
                    renderTurret(batch, type, TurretGame.HEIGHT - 130 - 170*i);
                    GlyphLayout name = getTurretName(type);
                    font.draw(batch, name, TurretGame.WIDTH - 100 - name.width/2f, TurretGame.HEIGHT - 170 - 170*i - name.height/2f);
                    GlyphLayout price = new GlyphLayout(font, "$"+type.cost());
                    font.draw(batch, price, TurretGame.WIDTH - 100 - price.width/2f, TurretGame.HEIGHT - 190 - 170*i - price.height/2f);
                }
            }
        } else
            batch.draw(closedShop, TurretGame.WIDTH - 40, TurretGame.HEIGHT - 24 - 100);
        batch.end();
    }

    private static void renderTurret(SpriteBatch batch, TurretRoot turret, float y) {
        boolean drawing = batch.isDrawing();
        boolean canAfford = World.money >= turret.cost();
        if(!drawing)
            batch.begin();
        if(!canAfford)
            batch.setColor(0.9f, 0, 0, 1f);
        batch.draw(turret.base(), (float) 1180.0 - turret.base().getWidth()/2f, y - turret.base().getHeight()/2f);
        batch.draw(turret.initTex(), (float) 1180.0 - turret.originX(), y - turret.originY(), turret.originX(), turret.originY(),
                turret.initTex().getRegionWidth(), turret.initTex().getRegionHeight(),
                1.0f, 1.0f, 90f);
        if(!canAfford)
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        if(!drawing)
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
                    return true;
                }
                if(sellButtonBounds.contains(x, y)) {
                    world.sellTurret(target);
                    target = null;
                    close();
                    return true;
                }
            } else {
                Page page = pages.get(currentPage);
                if(page.count >= 1 && Page.firstBounds.contains(x, y))
                    return buyTurret(page.first, x, y);
                if(page.count >= 2 && Page.secondBounds.contains(x, y))
                    return buyTurret(page.second, x, y);
                if(page.count >= 3 && Page.thirdBounds.contains(x, y))
                    return buyTurret(page.third, x, y);
                if(page.count >= 4 && Page.fourthBounds.contains(x, y))
                    return buyTurret(page.fourth, x, y);
            }
        } else {
            if(openButtonBounds.contains(x, y)) {
                open = true;
                return true;
            }
        }
        return false;
    }

    private static boolean buyTurret(TurretRoot type, float x, float y) {
        if(World.money >= type.cost()) {
            world.createTurret(type, x, y);
            World.money -= type.cost();
            close();
            return true;
        }
        return false;
    }

    public static void close() {
        open = false;
        target = null;
    }

    public static void populatePages() {
        pages.add(new Page());
        for(Class<? extends TurretRoot> type : TurretRoot.subclasses) {
            if(!pages.getLast().add(TurretRoot.getInstance(type))) {
                pages.add(new Page());
                pages.getLast().add(TurretRoot.getInstance(type));
            }
        }
    }

    private static class Page {
        public TurretRoot first, second, third, fourth;
        public static final Rectangle
                firstBounds = new Rectangle(1120f, 590f, 120f, 160f),
                secondBounds = new Rectangle(1120f, 420f, 120f, 160f),
                thirdBounds = new Rectangle(1120f, 250f, 120f, 160f),
                fourthBounds = new Rectangle(1120f, 80f, 120f, 160f);
        public int count = 0;

        public boolean add(TurretRoot type) {
            if(first == null) {
                first = type;
                count++;
                return true;
            }
            if(second == null) {
                second = type;
                count++;
                return true;
            }
            if(third == null) {
                third = type;
                count++;
                return true;
            }
            if(fourth == null) {
                fourth = type;
                count++;
                return true;
            }
            return false;
        }

        public TurretRoot get(int index) {
            return switch(index) {
                case 0 -> first;
                case 1 -> second;
                case 2 -> third;
                case 3 -> fourth;
                default -> null;
            };
        }
    }
}
