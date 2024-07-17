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
import com.mygdx.game.components.*;
import com.mygdx.game.entities.enemies.BasicEnemy;
import com.mygdx.game.entities.turrets.DevTurret;
import com.mygdx.game.shop.Shop;
import com.mygdx.game.rounds.Round;
import com.mygdx.game.systems.*;
import com.mygdx.game.entities.turrets.BasicTurret;

import java.util.List;


public class GameScreen extends ScreenAdapter {
	public final OrthographicCamera camera = new OrthographicCamera();

	public final SpriteBatch batch;
	public final Texture map;

	private final ComponentMapper<BuyingComponent> buyingM = ComponentMapper.getFor(BuyingComponent.class);
	private final ComponentMapper<RectBoundsComponent> boundsM = ComponentMapper.getFor(RectBoundsComponent.class);
	private final ComponentMapper<TurretComponent> turretM = ComponentMapper.getFor(TurretComponent.class);

	private final Family buyingFamily = Family.all(BuyingComponent.class).get();
	private final Family turretFamily = Family.all(TurretComponent.class, RectBoundsComponent.class).get();

	/**
	 * Pooled engine used for the creation of entities and components
	 */
	public final PooledEngine engine = new PooledEngine();

	public final BitmapFont font;
	/**
	 * List of rounds loaded from the file
	 */
	private final List<Round> rounds;

	/**
	 * Current round
	 */
	private Round current;

	/**
	 * World for this game
	 */
	public World world;

	public GameScreen(List<Round> rounds) {
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
		engine.addSystem(new EnemyDisposalSystem(world));

		camera.setToOrtho(false, TurretGame.WIDTH, TurretGame.HEIGHT);
		this.rounds = rounds;
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0.5f, 0, 1);

		batch.begin();
		batch.draw(map, 0, 0);
		batch.end();

		if(current != null && !current.finished()) current.update(delta, world);

		engine.update(delta);

		renderInfoText();
		Shop.render(batch);
	}

	private void renderInfoText() {
		batch.begin();
		font.draw(batch, "Money: $" + World.money, 10, TurretGame.HEIGHT - 10);
		font.draw(batch, "Lives: " + World.lives, 10, TurretGame.HEIGHT - 50);
		font.draw(batch, "Round: " + Math.max(1, World.round), 10, TurretGame.HEIGHT - 90);
		batch.end();
	}

	private class InputProcessing extends InputAdapter {
		Entity turret = null;
		Vector3 touch = new Vector3();

		@Override
		public boolean keyDown(int keycode) {
			switch (keycode) {
				case Input.Keys.SPACE -> {
					if(getBuying() == null) {
						world.createTurret(DevTurret.INSTANCE, 0, 0);
						return true;
					}
				}
				case Input.Keys.E -> {
					world.createEnemy(BasicEnemy.INSTANCE, 0.0f);
					return true;
				}
				case Input.Keys.V -> {
					if(current == null || (current.finished() && engine.getEntitiesFor(Family.all(EnemyComponent.class).exclude(EnemyDisposeComponent.class).get()).size() == 0))
						current = rounds.get(World.round++);
					return true;
				}
				case Input.Keys.TAB -> {
					if(Shop.open) Shop.close();
					else Shop.open = true;
					return true;
				}
				case Input.Keys.ESCAPE -> {
					if((turret = getBuying()) != null) {
						world.sellTurret(turret);
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			touch.set(screenX, screenY, 0);
			camera.unproject(touch);

			if(Shop.handleClick(touch.x, touch.y))
				return true;

			if ((turret = getBuying()) != null) {
				BuyingComponent buy = buyingM.get(turret);
				if (buy.canPlace) {
					turret.remove(BuyingComponent.class);
					TurretComponent t = turretM.get(turret);
					t.value /= TurretGame.VALUE_DIVIDEND;
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

		private Entity getBuying() {
			ImmutableArray<Entity> buying = engine.getEntitiesFor(buyingFamily);
			return buying.size() != 0 ? buying.first() : null;
		}
	}
}
