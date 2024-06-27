package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.enemies.EnemyRoot;
import com.mygdx.game.entities.turrets.TurretRoot;
import com.mygdx.game.rounds.Round;

import java.io.FileNotFoundException;

public class TurretGame extends Game {
    public static final float WIDTH = 1280, HEIGHT = 800;
    public static final Rectangle WORLD_BOUNDS = new Rectangle(0, 0, WIDTH, HEIGHT);

    public static final int resolution = 200;
    public static final Vector2[] points = new Vector2[resolution];
    public static CatmullRomSpline<Vector2> spline;

    @Override
    public void create() {
        // Generate path
        spline = new CatmullRomSpline<>(CurvePath.NODES, false);
        for(int i = 0; i < resolution; ++i) {
            points[i] = new Vector2();
            spline.valueAt(points[i], ((float)i)/((float)resolution-1));
        }

        // Load and cache the turret classes
        try {
            TurretRoot.loadClasses();
            EnemyRoot.loadClasses();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            Round.read();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        setScreen(new GameScreen());
    }
}
