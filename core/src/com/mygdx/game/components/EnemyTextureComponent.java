package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;

public class EnemyTextureComponent implements Component {
    public Texture texture = null;
    public float originX = 0, originY = 0;
}
