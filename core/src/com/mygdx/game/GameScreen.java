package com.mygdx.game;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.components.BuyingComponent;
import com.mygdx.game.components.RectBoundsComponent;
import com.mygdx.game.components.TurretComponent;
import com.mygdx.game.entities.enemies.BasicEnemy;
import com.mygdx.game.entities.shop.Shop;
import com.mygdx.game.systems.*;
import com.mygdx.game.entities.turrets.BasicTurret;


public class GameScreen extends ScreenAdapter {
	public final OrthographicCamera camera = new OrthographicCamera();

	public final SpriteBatch batch;
	public final Texture map;

	private final ComponentMapper<BuyingComponent> buyingM = ComponentMapper.getFor(BuyingComponent.class);
	private final ComponentMapper<RectBoundsComponent> boundsM = ComponentMapper.getFor(RectBoundsComponent.class);
	private final ComponentMapper<TurretComponent> turretM = ComponentMapper.getFor(TurretComponent.class);

	private final Family buyingFamily = Family.all(BuyingComponent.class).get();
	private final Family turretFamily = Family.all(TurretComponent.class, RectBoundsComponent.class).get();

	public final PooledEngine engine = new PooledEngine();

	public final BitmapFont font;

	public World world;

	public GameScreen() {
		Gdx.input.setInputProcessor(new InputProcessing());

		map = new Texture("Map.png");
		batch = new SpriteBatch();

		world = new World(engine);
		Shop.setWorld(world);

		font = new BitmapFont(Gdx.files.internal("font.fnt"), Gdx.files.internal("font.png"), false);

		// Add systems
		engine.addSystem(new StateSystem());
		engine.addSystem(new EnemySystem());
		engine.addSystem(new TurretSystem(camera));
		engine.addSystem(new TurretRenderingSystem(batch));
		engine.addSystem(new EnemyRenderingSystem(batch));
		engine.addSystem(new EnemyDisposalSystem());

		camera.setToOrtho(false, TurretGame.WIDTH, TurretGame.HEIGHT);
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0.5f, 0, 1);

		batch.begin();
		batch.draw(map, 0, 0);
		batch.end();

		engine.update(delta);

		renderInfoText();
		Shop.render(batch);
	}

	private void renderInfoText() {
		batch.begin();
		font.draw(batch, "Money: $" + World.money, 10, TurretGame.HEIGHT - 10);
		font.draw(batch, "Lives: " + World.lives, 10, TurretGame.HEIGHT - 50);
		batch.end();
	}

	private class InputProcessing extends InputAdapter {
		@Override
		public boolean keyDown(int keycode) {
			if(keycode == Input.Keys.SPACE && engine.getEntitiesFor(buyingFamily).size() == 0){
				world.createTurret(BasicTurret.INSTANCE, 0, 0);
				return true;
			}
			if(keycode == Input.Keys.E){
				world.createEnemy(BasicEnemy.INSTANCE);
				return true;
			}
			if(keycode == Input.Keys.TAB) {
				if(Shop.open)
					Shop.close();
				else
					Shop.open = true;
			}
			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			Vector3 touch = new Vector3(screenX, screenY, 0);
			camera.unproject(touch);

			System.out.println(touch.x + ":" + touch.y);

			if(Shop.handleClick(touch.x, touch.y))
				return true;

			ImmutableArray<Entity> buying = engine.getEntitiesFor(buyingFamily);
			if (buying.size() != 0) {
				Entity turret = buying.get(0);
				BuyingComponent buy = buyingM.get(turret);
				if (buy.canPlace) {
					turret.remove(BuyingComponent.class);
					TurretComponent t = turretM.get(turret);
					t.value = t.type.cost() / 3;
					Shop.open(turret);
					return true;
				}
				return false;
			}

			ImmutableArray<Entity> turrets = engine.getEntitiesFor(turretFamily);
			RectBoundsComponent bounds;
			for(Entity t : turrets) {
				bounds = boundsM.get(t);
				if(bounds.bounds.contains(touch.x, touch.y)) {
					Shop.open(t);
					return true;
				}
			}

			return false;
		}
	}
}
