package platformer;

import org.dyn4j.geometry.Mass.Type;
import org.dyn4j.geometry.Rectangle;

import commons.Resource;
import commons.ResourceFactory;
import commons.ResourceLocator.ClasspathResourceLocator;
import commons.Transform2f;
import commons.matrix.Vector2f;

import engine.core.ComponentBuilder;
import engine.core.Entity;
import engine.core.EntityBuilder;
import engine.core.Game;
import engine.core.Scene;
import engine.core.asset.AssetManager;
import engine.core.asset.AssetType;
import engine.core.script.XJava;
import engine.core.script.XPython;
import engine.imp.physics.dyn4j.BodySystem;
import engine.imp.physics.dyn4j.CBody;
import engine.imp.physics.dyn4j.JointSystem;
import engine.imp.render.AnimationSystem;
import engine.imp.render.CCamera;
import engine.imp.render.CLight;
import engine.imp.render.CRender;
import engine.imp.render.LightFactory;
import engine.imp.render.LightingSystem;
import engine.imp.render.MaterialFactory;
import engine.imp.render.RenderingSystem;
import glcommon.Color;
import glextra.material.Material;

public class Platformer {
	private MaterialFactory m_factory;

	public Platformer() {

	}

	public void start() {
		Game game = new Game();

		addSystems(game);
		initResources();

		Scene scene = new Scene(game);
		game.scenes().addScene(scene, "main");

		EntityBuilder lightBuilder = new EntityBuilder();
		lightBuilder.addComponentBuilder(new CLight(LightFactory.createAmbient(new Color(0.7f, 0.7f, 1f))));
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

		m_factory = new MaterialFactory(rendering);
	}

	private void initResources() {
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

		AssetManager assets = AssetManager.instance();

		assets.defineAsset("player_script", AssetType.SCRIPT, playerScript, ResourceFactory.readString(playerScript));
		assets.defineAsset("snowflake_script", AssetType.SCRIPT, snowflakeScript,
				ResourceFactory.readString(snowflakeScript));
		assets.defineAsset("spawn_script", AssetType.SCRIPT, spawnScript, ResourceFactory.readString(spawnScript));
		assets.defineAsset("icicle_script", AssetType.SCRIPT, icicleScript, ResourceFactory.readString(icicleScript));
		assets.defineAsset("walk_script", AssetType.SCRIPT, walkScript, ResourceFactory.readString(walkScript));
		assets.defineAsset("background", AssetType.MATERIAL, background, m_factory.createLighted(background));
		assets.defineAsset("snowman", AssetType.MATERIAL, snowman, m_factory.createLighted(snowman));
		assets.defineAsset("icicle", AssetType.MATERIAL, icicle, m_factory.createLighted(icicle));
		assets.defineAsset("lake", AssetType.MATERIAL, lake, m_factory.createLighted(lake));
		assets.defineAsset("platform1", AssetType.MATERIAL, platform1, m_factory.createLighted(platform1));
		assets.defineAsset("platform4", AssetType.MATERIAL, platform4, m_factory.createLighted(platform4));
		assets.defineAsset("snowflake", AssetType.MATERIAL, snowflake, m_factory.createLighted(snowflake));
	}

	private void makeBackground(Scene scene) {
		AssetManager assets = AssetManager.instance();
		Material backgroundImage = (Material) assets.getAsset("background").getAsset();

		EntityBuilder background = new EntityBuilder();
		background.addComponentBuilder(new CRender(backgroundImage, 0, 2f));
		background.setTransform(new Transform2f(new Vector2f(0f, 0f), 0f, new Vector2f(100f, 20f)));
		scene.createEntity("background", scene, background);
	}

	private void makePlatforms(Scene scene) {
		AssetManager assets = AssetManager.instance();
		Material platform1 = (Material) assets.getAsset("platform1").getAsset();

		Vector2f p1Scale = new Vector2f(1f, 2f);
		EntityBuilder p1Builder = new EntityBuilder();
		p1Builder.addComponentBuilder(new CRender(platform1, 1, 1f));
		p1Builder.addComponentBuilder(new GroundPhysicsBuilder(p1Scale));
		makePlatform(scene, p1Builder, 0, new Vector2f(0f, 0f), new Vector2f(1f, 2f));
		makePlatform(scene, p1Builder, 1, new Vector2f(1.2f, 0f), new Vector2f(1f, 2f));
		makePlatform(scene, p1Builder, 2, new Vector2f(3f, 1.5f), new Vector2f(1f, 2f));
	}

	private void makeSpawner(Scene scene) {
		AssetManager assets = AssetManager.instance();
		String script = (String) assets.getAsset("spawn_script").getAsset();

		Entity spawner = scene.createEntity("snowflakespawner", scene);
		spawner.scripts().add(new XPython(script));
	}

	private void makePlatform(Scene scene, EntityBuilder builder, int num, Vector2f position, Vector2f scale) {
		Entity platform = scene.createEntity("platform" + num, scene, builder);
		platform.getCTransform().setTransform(new Transform2f(position, 0f, scale));
	}

	private Entity makePlayer(Scene scene) {
		AssetManager assets = AssetManager.instance();
		Material icicle = (Material) assets.getAsset("snowman").getAsset();
		String script = (String) assets.getAsset("player_script").getAsset();
		String testScript = (String) assets.getAsset("walk_script").getAsset();

		Vector2f playerScale = new Vector2f(0.5f, 1f);
		EntityBuilder builder = new EntityBuilder();
		builder.addComponentBuilder(new CRender(icicle, 1, 1f));
		builder.addComponentBuilder(new PlayerPhysicsBuilder(playerScale));
		Entity player = scene.createEntity("player", scene, builder);
		player.getCTransform().setTransform(new Transform2f(new Vector2f(0f, 2f), 0f, playerScale));
		player.scripts().add(new XPython(script));
		player.scripts().add(new XJava(testScript));
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