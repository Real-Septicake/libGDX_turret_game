package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.components.BuyingComponent;
import com.mygdx.game.components.TransformComponent;
import com.mygdx.game.components.TurretTextureComponent;

public class TurretRenderingSystem extends IteratingSystem {
    private final SpriteBatch batch;

    private final Array<BaseQueueObject> baseQueue = new Array<>();
    private final Array<GunQueueObject> gunQueue = new Array<>();

    private BaseQueueObject buyingBase;
    private GunQueueObject buyingGun;

    private final ComponentMapper<TurretTextureComponent> turretTextureM;
    private final ComponentMapper<TransformComponent> transformM;

    private final ComponentMapper<BuyingComponent> buyingM;

    public TurretRenderingSystem(SpriteBatch batch) {
        super(Family.all(TurretTextureComponent.class, TransformComponent.class).get());

        this.batch = batch;

        turretTextureM = ComponentMapper.getFor(TurretTextureComponent.class);
        transformM = ComponentMapper.getFor(TransformComponent.class);

        buyingM = ComponentMapper.getFor(BuyingComponent.class);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        batch.begin();

        for(BaseQueueObject base : baseQueue) {
            if(!base.canPlace)
                batch.setColor(0.9f, 0, 0, 1f);
            batch.draw(base.base, base.x, base.y);
            if(!base.canPlace)
                batch.setColor(1, 1, 1, 1f);
        }

        for(GunQueueObject gun : gunQueue) {
            if(!gun.canPlace)
                batch.setColor(0.9f, 0, 0, 1f);
            batch.draw(gun.region, gun.x, gun.y,
                    gun.originX, gun.originY,
                    gun.width, gun.height,
                    gun.scaleX, gun.scaleY,
                    gun.rotation);
            if(!gun.canPlace)
                batch.setColor(1, 1, 1, 1f);
        }

        if(buyingBase != null) {
            if(!buyingBase.canPlace)
                batch.setColor(0.9f, 0, 0, 1f);
            batch.draw(buyingBase.base, buyingBase.x, buyingBase.y);
            batch.draw(buyingGun.region, buyingGun.x, buyingGun.y,
                    buyingGun.originX, buyingGun.originY,
                    buyingGun.width, buyingGun.height,
                    buyingGun.scaleX, buyingGun.scaleY,
                    buyingGun.rotation);
            if(!buyingBase.canPlace)
                batch.setColor(1, 1, 1, 1f);
        }

        batch.end();

        baseQueue.clear();
        gunQueue.clear();
        buyingBase = null;
        buyingGun = null;
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        TurretTextureComponent texture = turretTextureM.get(entity);
        if(texture == null)
            return;
        TransformComponent trans = transformM.get(entity);
        BuyingComponent buy = buyingM.get(entity);

        BaseQueueObject base = new BaseQueueObject(texture.turretBase,
                trans.pos.x - texture.turretBase.getWidth() / 2f,
                trans.pos.y - texture.turretBase.getHeight() / 2f,
                buy == null || buy.canPlace);
        GunQueueObject gun = new GunQueueObject(texture.turretGun,
                trans.pos.x - texture.originX, trans.pos.y - texture.originY,
                texture.originX, texture.originY,
                texture.turretGun.getRegionWidth(),
                texture.turretGun.getRegionHeight(),
                trans.scale.x, trans.scale.y,
                trans.rotation,buy == null || buy.canPlace);

        if(buy == null){
            baseQueue.add(base);
            gunQueue.add(gun);
        } else {
            buyingBase = base;
            buyingGun = gun;
        }
    }

    private record BaseQueueObject(Texture base, float x, float y, boolean canPlace) {
    }

    private record GunQueueObject(TextureRegion region, float x, float y, float originX, float originY, float width,
                                  float height, float scaleX, float scaleY, float rotation, boolean canPlace) {
    }
}
