package com.mygdx.game.rounds;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.World;
import com.mygdx.game.entities.enemies.EnemyRoot;

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
                    int tabs = countTab(line);
                    line = line.substring(tabs * (line.charAt(0) == ' ' ? 4 : 1));
                    int diff = tabs - lastTabCount - 1;
                    lastTabCount = tabs;
                    String[] frags = line.split(" ");
                    EnemyRoot type = EnemyRoot.enemies.get(frags[1]);
                    if(type == null)
                        throw new RuntimeException("Line: " + currentLine + " : Enemy type \"" + frags[1] + "\" does not exist");
                    Spawn next;
                    switch(frags[0]) {
                        case "i" -> {
                            if(frags.length != 4) throw new RuntimeException("Found " + frags.length + " arguments for type Instant on line " + currentLine + ", expected 4");
                            next = Spawn.instant(type, Integer.parseInt(frags[2]), Float.parseFloat(frags[3]), new ArrayList<>());
                        }
                        case "d" -> {
                            if(frags.length != 5) throw new RuntimeException("Found " + frags.length + " arguments for type Delay on line " + currentLine + ", expected 5");
                            next = Spawn.delay(type, Integer.parseInt(frags[2]), Float.parseFloat(frags[3]), Float.parseFloat(frags[4]), new ArrayList<>());
                        }
                        case "f" -> {
                            if(frags.length != 4) throw new RuntimeException("Found " + frags.length + " arguments for type Finish on line " + currentLine + ", expected 4");
                            next = Spawn.finish(type, Integer.parseInt(frags[2]), Float.parseFloat(frags[3]), new ArrayList<>());
                        }
                        default -> throw new RuntimeException("Line: " + currentLine + " : Unexpected spawn type: " + frags[0]);
                    }
                    if(tabs == 0) {
                        if(current != null) {
                            while (current.prev != null) current = current.prev;
                            rounds.add(new Round(current));
                        }
                    } else {
                        if(diff <= 0) {
                            while (diff++ < 0) {
                                if(current == null)
                                    throw new RuntimeException("Mismatched spawn at line " + currentLine);
                                current = current.prev;
                            }
                        }
                        if(current == null)
                            throw new RuntimeException("Mismatched spawn at line " + currentLine);
                        current.add(next);
                    }
                    current = next;
                }
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Number format error on line " + currentLine, e);
        }
        if(current != null) {
            while (current.prev != null) current = current.prev;
            rounds.add(new Round(current));
        }

        return rounds;
    }

    private static int countTab(String s) {
        String str = s.replace("    ", "\t");
        int count = 0;
        for(char c : str.toCharArray())
            if(c == '\t') count++;
        return count;
    }
}
