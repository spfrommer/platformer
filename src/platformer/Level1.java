package platformer;

import org.dyn4j.geometry.Mass.Type;
import org.dyn4j.geometry.Rectangle;

import commons.Transform2f;
import commons.matrix.Vector2f;
import commons.matrix.Vector3f;

import engine.core.Entity;
import engine.core.EntityBuilder;
import engine.core.Game;
import engine.core.Scene;
import engine.core.asset.AssetManager;
import engine.core.script.XJava;
import engine.core.script.XPython;
import engine.core.script.XScript;
import engine.imp.physics.dyn4j.CBody;
import engine.imp.render.CCamera;
import engine.imp.render.CLight;
import engine.imp.render.CRender;
import engine.imp.render.LightFactory;
import engine.imp.render.Material2D;
import glcommon.Color;

/**
 * The first level.
 */
public class Level1 extends Scene {
	public Level1(Game game) {
		super(game);

		EntityBuilder lightBuilder = new EntityBuilder();
		lightBuilder.addComponentBuilder(new CLight(LightFactory.createAmbient(new Color(0.8f, 0.8f, 0.8f))));
		this.createEntity("light", this, lightBuilder);

		Entity player = makePlayer();
		EntityBuilder cameraBuilder = new EntityBuilder();
		cameraBuilder.addComponentBuilder(new CCamera(1f, true));
		this.createEntity("camera", player, cameraBuilder);

		makeBackground();
		makePlatforms();
		makeSpawners();
	}

	private void makeBackground() {
		AssetManager assets = AssetManager.instance();
		Material2D backgroundImage = assets.get("background", Material2D.class);

		EntityBuilder background = new EntityBuilder();
		background.addComponentBuilder(new CRender(backgroundImage, 0, 2f));
		background.setTransform(new Transform2f(new Vector2f(0f, 0f), 0f, new Vector2f(100f, 20f)));
		this.createEntity("background", this, background);
	}

	private void makePlatforms() {
		AssetManager assets = AssetManager.instance();
		Material2D platform1 = assets.get("platform1", Material2D.class);

		Vector2f p1Scale = new Vector2f(1f, 2f);
		EntityBuilder p1Builder = new EntityBuilder();
		p1Builder.addComponentBuilder(new CRender(platform1, 2, 1f));
		CBody physics = new CBody();
		physics.setShape(new Rectangle(p1Scale.getX(), p1Scale.getY()));
		physics.setCollisionFriction(0.001f);
		physics.setGravityScale(0);
		physics.setMassType(Type.INFINITE);
		p1Builder.addComponentBuilder(physics);

		makePlatform(p1Builder, 0, new Vector2f(0f, 0f), new Vector2f(1f, 2f));
		makePlatform(p1Builder, 1, new Vector2f(1.2f, 0f), new Vector2f(1f, 2f));
		makePlatform(p1Builder, 2, new Vector2f(3f, 1.5f), new Vector2f(1f, 2f));
		makePlatform(p1Builder, 3, new Vector2f(1f, 5f), new Vector2f(1f, 2f));
		makePlatform(p1Builder, 4, new Vector2f(6f, 1.5f), new Vector2f(1f, 2f));
		makePlatform(p1Builder, 5, new Vector2f(-3f, -1.5f), new Vector2f(1f, 2f));
		makePlatform(p1Builder, 6, new Vector2f(-1.5f, 3.5f), new Vector2f(1f, 2f));
	}

	private void makeSpawners() {
		AssetManager assets = AssetManager.instance();
		XScript snowflake = assets.get("snowflake_spawn_script", XPython.class);
		XScript enemy = assets.get("enemy_spawn_script", XPython.class);

		Entity snowflakeSpawner = this.createEntity("snowflake_spawner", this);
		snowflakeSpawner.scripts().add(snowflake.duplicate());

		Entity enemySpawner = this.createEntity("enemy_spawner", this);
		enemySpawner.scripts().add(enemy.duplicate());
	}

	private void makePlatform(EntityBuilder builder, int num, Vector2f position, Vector2f scale) {
		Entity platform = this.createEntity("platform" + num, this, builder);
		platform.getCTransform().setTransform(new Transform2f(position, 0f, scale));
	}

	private Entity makePlayer() {
		AssetManager assets = AssetManager.instance();
		Material2D snowman = assets.get("snowman", Material2D.class);
		XScript playerScript = assets.get("player_script", XPython.class);
		XScript moveScript = assets.get("walk_script", XJava.class);

		Vector2f playerScale = new Vector2f(0.5f, 1f);

		CBody physics = new CBody();
		physics.setShape(new Rectangle(playerScale.getX(), playerScale.getY()));
		physics.setGravityScale(1);
		physics.setMassType(Type.FIXED_ANGULAR_VELOCITY);
		physics.setDensity(5);
		physics.setCollisionFriction(10);

		EntityBuilder playerBuilder = new EntityBuilder();
		playerBuilder.addComponentBuilder(new CRender(snowman, 2, 1f));
		playerBuilder.addComponentBuilder(physics);
		playerBuilder.addScript(playerScript);
		playerBuilder.addScript(moveScript);
		Entity player = this.createEntity("player", this, playerBuilder);
		player.getCTransform().setTransform(new Transform2f(new Vector2f(0f, 2f), 0f, playerScale));

		EntityBuilder lightBuilder = new EntityBuilder();
		lightBuilder.addComponentBuilder(new CLight(LightFactory.createDiffusePoint(new Vector3f(0f, 0f, 0f), new Vector3f(0.5f,
				0.5f, 4f), new Color(0f, 0f, 1f))));
		this.createEntity("playerLight", player, lightBuilder);
		return player;
	}
}
