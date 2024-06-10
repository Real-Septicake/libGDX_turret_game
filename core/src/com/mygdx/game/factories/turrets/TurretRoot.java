package com.mygdx.game.factories.turrets;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.components.*;

public abstract class TurretRoot {
    protected static final ComponentMapper<StateComponent> stateM = ComponentMapper.getFor(StateComponent.class);

    public abstract Act act();
    public abstract TextureRegion initTex();
    public abstract Texture base();
    public abstract float originX();
    public abstract float originY();
    public abstract float centerX();
    public abstract float centerY();
    public abstract float range();
    public abstract float damage();
}
