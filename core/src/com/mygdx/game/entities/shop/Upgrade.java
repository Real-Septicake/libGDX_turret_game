package com.mygdx.game.entities.shop;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.entities.turrets.Act;

public class Upgrade {
    /**
     * The cost to buy this upgrade in the shop
     */
    public final int cost;

    /**
     * The new radius of the turret in pixels
     */
    public final float range;
    /**
     * The new damage done by the turret
     */
    public final float damage;
    /**
     * The new act for the turret.
     *
     * If null, the turret's act remains the same
     */
    public final Act newAct;

    /**
     * The image displayed for this upgrade in the shop
     */
    public final TextureRegion image;
    /**
     * The name displayed for this upgrade in the shop
     */
    public final String name;

    public Upgrade(int c, float r, float d, Act act, TextureRegion i, String n) {
        cost = c;
        range = r;
        damage = d;
        newAct = act;
        image = i;
        name = n;
    }
}
