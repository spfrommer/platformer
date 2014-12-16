package platformer;

import org.dyn4j.geometry.Mass.Type;
import org.dyn4j.geometry.Rectangle;

import commons.Resource;
import commons.ResourceLocator.ClasspathResourceLocator;
import commons.Transform2f;
import commons.matrix.Vector2f;
import commons.matrix.Vector3f;

import engine.core.ComponentBuilder;
import engine.core.Entity;
import engine.core.EntityBuilder;
import engine.core.Game;
import engine.core.Scene;
import engine.core.asset.AssetManager;
import engine.core.script.XJava;
import engine.core.script.XPython;
import engine.core.script.XScript;
import engine.imp.physics.dyn4j.BodySystem;
import engine.imp.physics.dyn4j.CBody;
import engine.imp.physics.dyn4j.JointSystem;
import engine.imp.render.AnimationSystem;
import engine.imp.render.CCamera;
import engine.imp.render.CLight;
import engine.imp.render.CRender;
import engine.imp.render.LightFactory;
import engine.imp.render.LightingSystem;
import engine.imp.render.Material2D;
import engine.imp.render.RenderingSystem;
import glcommon.Color;

public class Platformer {
	public Platformer() {

	}

	public void start() {
		Game game = new Game();

		addSystems(game);
		initResources();

		Scene scene = new Scene(game);
		game.scenes().addScene(scene, "main");

		EntityBuilder lightBuilder = new EntityBuilder();
		lightBuilder.addComponentBuilder(new CLight(LightFactory.createAmbient(new Color(0.1f, 0.1f, 0.1f))));
		scene.createEntity("light", scene, lightBuilder);

		makeBackground(scene);
		makePlatforms(scene);
		makeSpawner(scene);
		Entity player = makePlayer(scene);

		EntityBuilder cameraBuilder = new EntityBuilder();
		cameraBuilder.addComponentBuilder(new CCamera(1f, true));
		scene.createEntity("camera", player, cameraBuilder);

		game.start();

		float lastTime = 16f;
		while (true) {
			long startTime = System.nanoTime();
			game.update(lastTime);
			long endTime = System.nanoTime();
			lastTime = (endTime - startTime) / 1000000;
		}
	}

	private void addSystems(Game game) {
		BodySystem bodyPhysics = new BodySystem(new Vector2f(0f, -10f));
		JointSystem jointPhysics = new JointSystem(bodyPhysics);
		AnimationSystem animation = new AnimationSystem();
		RenderingSystem rendering = new RenderingSystem(5f, 5f);
		LightingSystem lighting = new LightingSystem(rendering);
		game.addSystem(bodyPhysics);
		game.addSystem(jointPhysics);
		game.addSystem(animation);
		game.addSystem(rendering);
		game.addSystem(lighting);

		AssetManager.init(rendering.getDisplay().getGL());
	}

	private void initResources() {
		AssetManager manager = AssetManager.instance();

		ClasspathResourceLocator locator = new ClasspathResourceLocator();
		Resource playerScript = new Resource(locator, "platformer/PlayerScript.py");
		Resource snowflakeScript = new Resource(locator, "platformer/SnowflakeScript.py");
		Resource spawnScript = new Resource(locator, "platformer/SpawnScript.py");
		Resource icicleScript = new Resource(locator, "platformer/IcicleScript.py");
		Resource walkScript = new Resource(locator, "platformer/walk.bsh");
		Resource background = new Resource(locator, "platformer/background.png");
		Resource snowman = new Resource(locator, "platformer/snowman.png");
		Resource icicle = new Resource(locator, "platformer/cave_icicle_1_0.png");
		Resource lake = new Resource(locator, "platformer/cave_lake_1_0.png");
		Resource platform1 = new Resource(locator, "platformer/cave_platform_1_0.png");
		Resource platform4 = new Resource(locator, "platformer/cave_platform_4_0.png");
		Resource snowflake = new Resource(locator, "platformer/snowflake.png");

		manager.load("player_script", playerScript, XPython.class);
		manager.load("snowflake_script", snowflakeScript, XPython.class);
		manager.load("spawn_script", spawnScript, XPython.class);
		manager.load("icicle_script", icicleScript, XPython.class);
		manager.load("walk_script", walkScript, XJava.class);
		manager.load("background", background, Material2D.class);
		manager.load("snowman", snowman, Material2D.class);
		manager.load("icicle", icicle, Material2D.class);
		manager.load("lake", lake, Material2D.class);
		manager.load("platform1", platform1, Material2D.class);
		manager.load("platform4", platform4, Material2D.class);
		manager.load("snowflake", snowflake, Material2D.class);
	}

	private void makeBackground(Scene scene) {
		AssetManager assets = AssetManager.instance();
		Material2D backgroundImage = assets.get("background", Material2D.class);

		EntityBuilder background = new EntityBuilder();
		background.addComponentBuilder(new CRender(backgroundImage, 0, 2f));
		background.setTransform(new Transform2f(new Vector2f(0f, 0f), 0f, new Vector2f(100f, 20f)));
		scene.createEntity("background", scene, background);
	}

	private void makePlatforms(Scene scene) {
		AssetManager assets = AssetManager.instance();
		Material2D platform1 = assets.get("platform1", Material2D.class);

		Vector2f p1Scale = new Vector2f(1f, 2f);
		EntityBuilder p1Builder = new EntityBuilder();
		p1Builder.addComponentBuilder(new CRender(platform1, 1, 1f));
		p1Builder.addComponentBuilder(new GroundPhysicsBuilder(p1Scale));
		makePlatform(scene, p1Builder, 0, new Vector2f(0f, 0f), new Vector2f(1f, 2f));
		makePlatform(scene, p1Builder, 1, new Vector2f(1.2f, 0f), new Vector2f(1f, 2f));
		makePlatform(scene, p1Builder, 2, new Vector2f(3f, 1.5f), new Vector2f(1f, 2f));
		makePlatform(scene, p1Builder, 3, new Vector2f(1f, 5f), new Vector2f(1f, 2f));
		makePlatform(scene, p1Builder, 4, new Vector2f(6f, 1.5f), new Vector2f(1f, 2f));
		makePlatform(scene, p1Builder, 5, new Vector2f(-3f, -1.5f), new Vector2f(1f, 2f));
		makePlatform(scene, p1Builder, 6, new Vector2f(-1.5f, 3f), new Vector2f(1f, 2f));
	}

	private void makeSpawner(Scene scene) {
		AssetManager assets = AssetManager.instance();
		XScript script = assets.get("spawn_script", XPython.class);

		Entity spawner = scene.createEntity("snowflakespawner", scene);
		spawner.scripts().add(script.duplicate());
	}

	private void makePlatform(Scene scene, EntityBuilder builder, int num, Vector2f position, Vector2f scale) {
		Entity platform = scene.createEntity("platform" + num, scene, builder);
		platform.getCTransform().setTransform(new Transform2f(position, 0f, scale));
	}

	private Entity makePlayer(Scene scene) {
		AssetManager assets = AssetManager.instance();
		Material2D snowman = assets.get("snowman", Material2D.class);
		XScript playerScript = assets.get("player_script", XPython.class);
		XScript moveScript = assets.get("walk_script", XJava.class);

		Vector2f playerScale = new Vector2f(0.5f, 1f);
		EntityBuilder playerBuilder = new EntityBuilder();
		playerBuilder.addComponentBuilder(new CRender(snowman, 1, 1f));
		playerBuilder.addComponentBuilder(new PlayerPhysicsBuilder(playerScale));
		playerBuilder.addScript(playerScript);
		playerBuilder.addScript(moveScript);
		Entity player = scene.createEntity("player", scene, playerBuilder);
		player.getCTransform().setTransform(new Transform2f(new Vector2f(0f, 2f), 0f, playerScale));

		EntityBuilder lightBuilder = new EntityBuilder();
		lightBuilder.addComponentBuilder(new CLight(LightFactory.createDiffusePoint(new Vector3f(0f, 0f, 0f),
				new Vector3f(0.5f, 0.5f, 4f), new Color(1f, 1f, 1f))));
		scene.createEntity("playerLight", player, lightBuilder);
		return player;
	}

	public static void main(String[] args) {
		new Platformer().start();
	}

	private class GroundPhysicsBuilder implements ComponentBuilder<CBody> {
		private Vector2f m_scale;

		public GroundPhysicsBuilder(Vector2f scale) {
			m_scale = scale;
		}

		@Override
		public CBody build() {
			CBody physics = new CBody();
			physics.setShape(new Rectangle(m_scale.getX(), m_scale.getY()));
			physics.setCollisionFriction(0.001f);
			physics.setGravityScale(0);
			physics.setMassType(Type.INFINITE);
			return physics;
		}

		@Override
		public String getName() {
			return CBody.NAME;
		}
	}

	private class PlayerPhysicsBuilder implements ComponentBuilder<CBody> {
		private Vector2f m_scale;

		public PlayerPhysicsBuilder(Vector2f scale) {
			m_scale = scale;
		}

		@Override
		public CBody build() {
			CBody physics = new CBody();
			physics.setShape(new Rectangle(m_scale.getX(), m_scale.getY()));
			physics.setGravityScale(1);
			physics.setMassType(Type.FIXED_ANGULAR_VELOCITY);
			physics.setDensity(5);
			physics.setCollisionFriction(10);
			return physics;
		}

		@Override
		public String getName() {
			return CBody.NAME;
		}
	}
}