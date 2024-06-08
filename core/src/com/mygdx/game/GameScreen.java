package com.mygdx.game;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.components.BuyingComponent;
import com.mygdx.game.factories.enemies.BasicEnemy;
import com.mygdx.game.systems.*;
import com.mygdx.game.factories.turrets.BasicTurret;


public class GameScreen extends ScreenAdapter {
	public final OrthographicCamera camera = new OrthographicCamera();

	public final SpriteBatch batch;
	public final Texture map;

	private final ComponentMapper<BuyingComponent> buyingM = ComponentMapper.getFor(BuyingComponent.class);

	private final Family buyingFamily = Family.all(BuyingComponent.class).get();

	public final Engine engine = new Engine();

	public GameScreen() {
		Gdx.input.setInputProcessor(new InputProcessing());

		map = new Texture("Map.png");
		batch = new SpriteBatch();

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
	}

	private class InputProcessing extends InputAdapter {
		@Override
		public boolean keyDown(int keycode) {
			if(keycode == Input.Keys.SPACE && engine.getEntitiesFor(buyingFamily).size() == 0){
				engine.addEntity(BasicTurret.create(0, 0));
				return true;
			}
			if(keycode == Input.Keys.E){
				engine.addEntity(BasicEnemy.create());
				return true;
			}
			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			Vector3 touch = new Vector3(screenX, screenY, 0);
			camera.unproject(touch);

			ImmutableArray<Entity> buying = engine.getEntitiesFor(buyingFamily);
			if (buying.size() == 0)
				return false;

			Entity turret = buying.get(0);
			BuyingComponent buy = buyingM.get(turret);
			if (buy.canPlace) {
				turret.remove(BuyingComponent.class);
				return true;
			}

			return false;
		}
	}
}
