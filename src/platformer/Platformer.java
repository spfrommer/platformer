package platformer;

import org.dyn4j.geometry.Mass.Type;
import org.dyn4j.geometry.Rectangle;

import commons.Resource;
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
import engine.core.script.XPython;
import engine.imp.physics.dyn4j.CDyn4jBody;
import engine.imp.physics.dyn4j.Dyn4jBodySystem;
import engine.imp.physics.dyn4j.Dyn4jJointSystem;
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

	private Resource m_playerScript;

	public Platformer() {

	}

	public void start() {
		Game game = new Game();

		addSystems(game);
		initResources();

		Scene scene = new Scene(game);
		game.scenes().addScene(scene, "main");

		EntityBuilder lightBuilder = new EntityBuilder();
		/*lightBuilder.addComponentBuilder(new CLight(LightFactory.createDiffusePoint(new Vector3f(0f, 0f, 1f),
				new Vector3f(0.5f, 0.5f, 0.5f), new Color(0.7f, 0.7f, 1f))));*/
		lightBuilder.addComponentBuilder(new CLight(LightFactory.createAmbient(new Color(0.7f, 0.7f, 1f))));
		scene.createEntity("light", scene, lightBuilder);

		makeBackground(scene);
		makePlatforms(scene);
		Entity player = makePlayer(scene);

		EntityBuilder cameraBuilder = new EntityBuilder();
		cameraBuilder.addComponentBuilder(new CCamera(1f, true));
		scene.createEntity("camera", player, cameraBuilder);

		game.start();

		while (true) {
			game.update(16f);
		}
	}

	private void addSystems(Game game) {
		Dyn4jBodySystem bodyPhysics = new Dyn4jBodySystem(new Vector2f(0f, -10f));
		Dyn4jJointSystem jointPhysics = new Dyn4jJointSystem(bodyPhysics);
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
		m_playerScript = new Resource(locator, "platformer/PlayerScript.py");
		Resource rBackground = new Resource(locator, "platformer/background.png");
		Resource rIcicle = new Resource(locator, "platformer/cave_icicle_1_0.png");
		Resource rLake = new Resource(locator, "platformer/cave_lake_1_0.png");
		Resource rPlatform1 = new Resource(locator, "platformer/cave_platform_1_0.png");
		Resource rPlatform4 = new Resource(locator, "platformer/cave_platform_4_0.png");
		Resource rSnowflake = new Resource(locator, "platformer/snowflake.png");

		AssetManager assets = AssetManager.instance();

		assets.defineAsset("background", AssetType.MATERIAL, rBackground, m_factory.createLighted(rBackground));
		assets.defineAsset("icicle", AssetType.MATERIAL, rIcicle, m_factory.createLighted(rIcicle));
		assets.defineAsset("lake", AssetType.MATERIAL, rLake, m_factory.createLighted(rLake));
		assets.defineAsset("platform1", AssetType.MATERIAL, rPlatform1, m_factory.createLighted(rPlatform1));
		assets.defineAsset("platform4", AssetType.MATERIAL, rPlatform4, m_factory.createLighted(rPlatform4));
		assets.defineAsset("snowflake", AssetType.MATERIAL, rSnowflake, m_factory.createLighted(rSnowflake));
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

	private void makePlatform(Scene scene, EntityBuilder builder, int num, Vector2f position, Vector2f scale) {
		Entity platform = scene.createEntity("platform" + num, scene, builder);
		platform.getCTransform().setTransform(new Transform2f(position, 0f, scale));
	}

	private Entity makePlayer(Scene scene) {
		AssetManager assets = AssetManager.instance();
		Material icicle = (Material) assets.getAsset("icicle").getAsset();

		Vector2f playerScale = new Vector2f(0.5f, 1f);
		EntityBuilder builder = new EntityBuilder();
		builder.addComponentBuilder(new CRender(icicle, 1, 1f));
		builder.addComponentBuilder(new PlayerPhysicsBuilder(playerScale));
		Entity player = scene.createEntity("player", scene, builder);
		player.getCTransform().setTransform(new Transform2f(new Vector2f(0f, 2f), 0f, playerScale));
		player.scripts().add(new XPython(m_playerScript));
		return player;
	}

	public static void main(String[] args) {
		new Platformer().start();
	}

	private class GroundPhysicsBuilder implements ComponentBuilder<CDyn4jBody> {
		private Vector2f m_scale;

		public GroundPhysicsBuilder(Vector2f scale) {
			m_scale = scale;
		}

		@Override
		public CDyn4jBody build() {
			CDyn4jBody physics = new CDyn4jBody();
			physics.setShape(new Rectangle(m_scale.getX(), m_scale.getY()));
			physics.setGravityScale(0);
			physics.setMassType(Type.INFINITE);
			return physics;
		}

		@Override
		public String getName() {
			return CDyn4jBody.NAME;
		}
	}

	private class PlayerPhysicsBuilder implements ComponentBuilder<CDyn4jBody> {
		private Vector2f m_scale;

		public PlayerPhysicsBuilder(Vector2f scale) {
			m_scale = scale;
		}

		@Override
		public CDyn4jBody build() {
			CDyn4jBody physics = new CDyn4jBody();
			physics.setShape(new Rectangle(m_scale.getX(), m_scale.getY()));
			physics.setGravityScale(1);
			physics.setMassType(Type.FIXED_ANGULAR_VELOCITY);
			physics.setDensity(5);
			physics.setCollisionFriction(10);
			return physics;
		}

		@Override
		public String getName() {
			return CDyn4jBody.NAME;
		}
	}
}