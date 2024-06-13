package com.mygdx.game.entities.shop;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.entities.turrets.Act;

public class Upgrade {
    public final int cost;

    public final float range;
    public final float damage;
    public final Act newAct;

    public final TextureRegion image;
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
