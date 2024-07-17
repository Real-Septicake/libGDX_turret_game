package com.mygdx.game.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Set;

/**
 * The root class of all enemies
 *
 * @implSpec All subclasses must have a public, static method named `getInstance`
 * that returns an instance of the subclass
 */
public abstract class EnemyRoot {
    public static final HashMap<String, EnemyRoot> enemies = new HashMap<>();

    /**
     * @return The texture for this enemy type
     */
    public abstract Texture texture();
    /**
     * @return The x coordinate of the origin this enemy type's texture
     */
    public abstract float originX();
    /**
     * @return The y coordinate of the origin this enemy type's texture
     */
    public abstract float originY();
    /**
     * @return The speed of this enemy type
     */
    public abstract float speed();
    /**
     * @return The maximum health of this enemy type
     */
    public abstract int maxHealth();
    /**
     * @return The amount of cash gained from killing this enemy type
     */
    public abstract int value();

    /**
     * @return The enemy listener used by this enemy type
     */
    public abstract EnemyListener listener();

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
