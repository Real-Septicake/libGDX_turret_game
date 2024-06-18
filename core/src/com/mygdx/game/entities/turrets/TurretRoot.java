package com.mygdx.game.entities.turrets;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.components.*;
import com.mygdx.game.entities.shop.Shop;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class TurretRoot {
    protected static final ComponentMapper<StateComponent> stateM = ComponentMapper.getFor(StateComponent.class);
    public static final List<Class<? extends TurretRoot>> subclasses = new ArrayList<>();

    public abstract Act act();
    public abstract TextureRegion initTex();
    public abstract Texture base();
    public abstract float originX();
    public abstract float originY();
    public abstract float range();
    public abstract float damage();

    public abstract int cost();
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
            subclasses.add(clazz);
        }
        Shop.populatePages();
    }

    public static TurretRoot getInstance(Class<? extends TurretRoot> clazz) {
        try {
            Method instance = clazz.getMethod("getInstance");
            return (TurretRoot) instance.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}
        return null;
    }
}
