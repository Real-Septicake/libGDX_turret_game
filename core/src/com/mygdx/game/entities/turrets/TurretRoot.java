package com.mygdx.game.entities.turrets;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.components.*;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Set;

/**
 * The root class of all turrets
 *
 * @implSpec All subclasses must have a public, static method named `getInstance`
 * that returns an instance of the subclass
 */
public abstract class TurretRoot {
    protected static final ComponentMapper<StateComponent> stateM = ComponentMapper.getFor(StateComponent.class);
    public static final HashMap<String, TurretRoot> subclasses = new HashMap<>();

    /**
     * @return The default act of this turret type
     */
    public abstract Act act();
    /**
     * @return The initial texture of the gun of this turret type
     */
    public abstract TextureRegion initTex();
    /**
     * @return The texture of the base of this turret type
     */
    public abstract Texture base();
    /**
     * @return The x coordinate of the origin of this turret type's initial gun texture
     */
    public abstract float originX();
    /**
     * @return The y coordinate of the origin of this turret type's initial gun texture
     */
    public abstract float originY();
    /**
     * @return The radius of this turret type's range in pixels
     */
    public abstract float range();
    /**
     * @return The damage done by this turret type
     */
    public abstract float damage();

    /**
     * @return The cost to buy this turret type from the store
     */
    public abstract int cost();
    /**
     * @return The name displayed for this turret type in the store
     */
    public abstract String name();

    private static void validateTurretClass(Class<? extends TurretRoot> c) {
        try {
            Method m = c.getMethod("getInstance");
            if(m.getGenericParameterTypes().length != 0)
                throw new RuntimeException("Method " + m.getName() + " in class " + c.getSimpleName() + " cannot have parameters");
            if((m.getModifiers() & Modifier.STATIC) == 0)
                throw new RuntimeException("Method " + m.getName() + " in class " + c.getSimpleName() + " must be static");
            if((m.getModifiers() & Modifier.PUBLIC) == 0)
                throw new RuntimeException("Method " + m.getName() + " in class " + c.getSimpleName() + " must be public");
            if(!isSubclass(m.getReturnType()))
                throw new RuntimeException("Method " + m.getName() + " in class " + c.getSimpleName() + " must return a subclass of TurretRoot");
            if(m.getReturnType() != c)
                throw new RuntimeException("Method " + m.getName() + " in class " + c.getSimpleName() + " must return an instance of enclosing class");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Class " + c.getSimpleName() + " does not have method \"getInstance\"");
        }
    }

    private static boolean isSubclass(Class<?> clazz) {
        if(clazz == TurretRoot.class)
            return true;
        Class<?> temp = clazz.getSuperclass();
        while(temp != Object.class) {
            if(temp == TurretRoot.class)
                return true;
            temp = temp.getSuperclass();
        }
        return false;
    }

    public static void loadClasses() throws ClassNotFoundException {
        Reflections reflections = new Reflections("com.mygdx.game");
        Set<Class<? extends TurretRoot>> subs = reflections.getSubTypesOf(TurretRoot.class);
        for(Class<? extends TurretRoot> clazz : subs) {
            validateTurretClass(clazz);
            try {
                Method instance = clazz.getMethod("getInstance");
                subclasses.put(clazz.getSimpleName(), clazz.cast(instance.invoke(null)));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}
        }
    }
}
