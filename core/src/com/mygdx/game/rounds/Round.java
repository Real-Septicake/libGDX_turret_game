package com.mygdx.game.rounds;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.World;

import java.io.FileNotFoundException;
import java.util.*;

public class Round {
    private final List<Spawn> spawns = new ArrayList<>();
    private final List<Spawn> queue = new ArrayList<>();

    public Round(Spawn init) {
        spawns.add(init);
    }

    public void update(float delta, World world) {
        Iterator<Spawn> itr = spawns.iterator();
        Spawn spawn;
        while (itr.hasNext()) {
            spawn = itr.next();
            Spawn.ProcessResult result = spawn.process(world, delta);
            if(spawn.type == Spawn.Type.INSTANT && result.started)
                queue.addAll(spawn.next);
            if(spawn.type == Spawn.Type.DELAY && result.delayOver)
                queue.addAll(spawn.next);
            if(spawn.type == Spawn.Type.FINISH && result.finished)
                queue.addAll(spawn.next);

            if(spawn.finished && spawn.delayOver)
                itr.remove();
        }
        spawns.addAll(queue);
        queue.clear();
    }

    public boolean finished() {
        return spawns.isEmpty();
    }

    public static List<Round> read() throws FileNotFoundException {
        int currentLine = 0;
        int lastTabCount = 0;
        Spawn current = null;
        List<Round> rounds = new ArrayList<>();
        try (Scanner scan = new Scanner(Gdx.files.internal("rounds.txt").file())) {
            while(scan.hasNext()) {
                currentLine++;
                String line = scan.nextLine();
                if(!line.startsWith("#") && !line.isBlank()) {
                    String[] frags = line.split(" ");
                    switch(frags[0]) {
                        case "i" -> {
                            if(frags.length != 4) throw new RuntimeException("Found " + frags.length + " arguments for type Instant, expected 4");
                            System.out.println("Instant: " + frags[1] + ", Count: " + Integer.valueOf(frags[2]) + ", Spacing: " + Float.valueOf(frags[3]));
                        }
                        case "d" -> {
                            if(frags.length != 5) throw new RuntimeException("Found " + frags.length + " arguments for type Delay, expected 5");
                            System.out.println("Delay: " + frags[1] + ", Count: " + Integer.valueOf(frags[2]) + ", Spacing: " + Float.valueOf(frags[3]) + ", Delay: " + Float.valueOf(frags[4]));
                        }
                        case "f" -> {
                            if(frags.length != 4) throw new RuntimeException("Found " + frags.length + " arguments for type Finish, expected 4");
                            System.out.println("Finish: " + frags[1] + ", Count: " + Integer.valueOf(frags[2]) + ", Spacing: " + Float.valueOf(frags[3]));
                        }
                        default -> throw new RuntimeException("Unexpected spawn type: " + frags[0]);
                    }
                }
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Number format error on line " + currentLine, e);
        }

        return rounds;
    }

    private static int countTab(String s) {
        int count = 0;
        for(char c : s.toCharArray())
            if(c == '\t') count++;
        return count;
    }
}
