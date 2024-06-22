package com.mygdx.game.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.entities.turrets.TurretRoot;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Set;

public abstract class EnemyRoot {
    public static final HashMap<String, EnemyRoot> enemies = new HashMap<>();

    public abstract Texture texture();
    public abstract float originX();
    public abstract float originY();
    public abstract float speed();
    public abstract int maxHealth();
    public abstract int value();

    private static void validateClass(Class<? extends EnemyRoot> clazz) {
        try {
            Method m = clazz.getMethod("getInstance");
            if(m.getGenericParameterTypes().length != 0)
                throw new RuntimeException("Method " + m.getName() + " in class " + clazz.getSimpleName() + " cannot have parameters");
            if((m.getModifiers() & Modifier.STATIC) == 0)
                throw new RuntimeException("Method " + m.getName() + " in class " + clazz.getSimpleName() + " must be static");
            if((m.getModifiers() & Modifier.PUBLIC) == 0)
                throw new RuntimeException("Method " + m.getName() + " in class " + clazz.getSimpleName() + " must be public");
            if(!isSubclass(m.getReturnType()))
                throw new RuntimeException("Method " + m.getName() + " in class " + clazz.getSimpleName() + " must return a subclass of TurretRoot");
            if(m.getReturnType() != clazz)
                throw new RuntimeException("Method " + m.getName() + " in class " + clazz.getSimpleName() + " must return an instance of enclosing class");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Class " + clazz.getSimpleName() + " does not have method \"getInstance\"");
        }
    }

    private static boolean isSubclass(Class<?> clazz) {
        if(clazz == EnemyRoot.class)
            return true;
        Class<?> temp = clazz.getSuperclass();
        while(temp != Object.class) {
            if(temp == EnemyRoot.class)
                return true;
            temp = temp.getSuperclass();
        }
        return false;
    }

    public static void loadClasses() {
        Reflections r = new Reflections("com.mygdx.game");
        Set<Class<? extends EnemyRoot>> subs = r.getSubTypesOf(EnemyRoot.class);
        for(Class<? extends EnemyRoot> c : subs) {
            validateClass(c);
            try {
                Method instance = c.getMethod("getInstance");
                enemies.put(c.getSimpleName(), c.cast(instance.invoke(null)));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}
        }
    }
}
