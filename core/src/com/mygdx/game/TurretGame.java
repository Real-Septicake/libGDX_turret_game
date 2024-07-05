package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.enemies.EnemyRoot;
import com.mygdx.game.shop.Shop;
import com.mygdx.game.entities.turrets.TurretRoot;
import com.mygdx.game.rounds.Round;
import com.mygdx.game.rounds.Spawn;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class TurretGame extends Game {
    public static final float WIDTH = 1280, HEIGHT = 800;
    public static final Rectangle WORLD_BOUNDS = new Rectangle(0, 0, WIDTH, HEIGHT);
    public static final String CONFIG_FILE_NAME = "config.txt";

    public static final int resolution = 200;
    public static final Vector2[] points = new Vector2[resolution];
    public static CatmullRomSpline<Vector2> spline;

    public static final int VALUE_DIVIDEND = 3;

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
            ArrayList<Round> r = new ArrayList<>();
            readConfig(r);
            setScreen(new GameScreen(r));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void readConfig(ArrayList<Round> rounds) throws FileNotFoundException {
        enum State {
            ROUNDS,
            TURRETS
        }

        State state = null;
        int currentLine = 0;
        int lastTabCount = 0;
        Spawn current = null;
        try (Scanner scan = new Scanner(Gdx.files.internal(TurretGame.CONFIG_FILE_NAME).file())) {
            while(scan.hasNext()) {
                currentLine++;
                String line = scan.nextLine();
                if(line.startsWith("#") || line.isBlank())
                    continue;
                if (line.trim().startsWith("[")) {
                    state = switch(line.trim()) {
                        case "[ROUNDS]" -> State.ROUNDS;
                        case "[SHOP]" -> State.TURRETS;
                        default -> throw new RuntimeException("Line: " + currentLine + " : Unknown config header " + line.trim());
                    };
                }
                else if (state != null) {
                    if(state == State.TURRETS) {
                        TurretRoot t = TurretRoot.subclasses.get(line.trim());
                        if(t == null)
                            throw new RuntimeException("Line: " + currentLine + " : Turret type \"" + line.trim() + "\" does not exist");
                        Shop.addTurret(t);
                    } else {
                        int tabs = countTab(line);
                        line = line.trim();
                        int diff = tabs - lastTabCount - 1;
                        lastTabCount = tabs;
                        String[] frags = line.split(" ");
                        EnemyRoot type = EnemyRoot.enemies.get(frags[1]);
                        if (type == null)
                            throw new RuntimeException("Line: " + currentLine + " : Enemy type \"" + frags[1] + "\" does not exist");
                        Spawn next;
                        switch (frags[0]) {
                            case "i" -> {
                                if (frags.length != 4)
                                    throw new RuntimeException("Found " + frags.length + " arguments for type Instant on line " + currentLine + ", expected 4");
                                next = Spawn.instant(type, Integer.parseInt(frags[2]), Float.parseFloat(frags[3]), new ArrayList<>());
                            }
                            case "d" -> {
                                if (frags.length != 5)
                                    throw new RuntimeException("Found " + frags.length + " arguments for type Delay on line " + currentLine + ", expected 5");
                                next = Spawn.delay(type, Integer.parseInt(frags[2]), Float.parseFloat(frags[3]), Float.parseFloat(frags[4]), new ArrayList<>());
                            }
                            case "f" -> {
                                if (frags.length != 4)
                                    throw new RuntimeException("Found " + frags.length + " arguments for type Finish on line " + currentLine + ", expected 4");
                                next = Spawn.finish(type, Integer.parseInt(frags[2]), Float.parseFloat(frags[3]), new ArrayList<>());
                            }
                            default ->
                                    throw new RuntimeException("Line: " + currentLine + " : Unexpected spawn type: " + frags[0]);
                        }
                        if (tabs == 0) {
                            if (current != null) {
                                while (current.prev != null) current = current.prev;
                                rounds.add(new Round(current));
                            }
                        } else {
                            if (diff <= 0) {
                                while (diff++ < 0) {
                                    if (current == null)
                                        throw new RuntimeException("Mismatched spawn at line " + currentLine);
                                    current = current.prev;
                                }
                            }
                            if (current == null)
                                throw new RuntimeException("Mismatched spawn at line " + currentLine);
                            current.add(next);
                        }
                        current = next;
                    }
                }
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Number format error on line " + currentLine, e);
        }
        if(current != null) {
            while (current.prev != null) current = current.prev;
            rounds.add(new Round(current));
        }
    }

    private static int countTab(String s) {
        String str = s.replace("    ", "\t");
        int count = 0;
        for(char c : str.toCharArray())
            if(c == '\t') count++;
        return count;
    }
}
