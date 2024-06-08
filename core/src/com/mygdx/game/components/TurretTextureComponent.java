package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TurretTextureComponent implements Component {
    public TextureRegion turretGun = null;
    public Texture turretBase = null;
    public float originX = 0, originY = 0;
    public float centerX = 0, centerY = 0;
}
