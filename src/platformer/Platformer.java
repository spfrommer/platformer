package platformer;

import commons.Resource;
import commons.ResourceLocator.ClasspathResourceLocator;
import commons.matrix.Vector2f;

import engine.core.Game;
import engine.core.asset.AssetManager;
import engine.imp.physics.dyn4j.BodySystem;
import engine.imp.physics.dyn4j.JointSystem;
import engine.imp.render.AnimationSystem;
import engine.imp.render.LightingSystem;
import engine.imp.render.RenderingSystem;

public class Platformer {
	public Platformer() {

	}

	public void start() {
		Game game = new Game();

		addSystems(game);
		initResources();

		game.scenes().addScene(new Level1(game), "main");
		game.scenes().addScene(new Level2(game), "level2");

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
		manager.loadFromFile(new Resource(locator, "platformer/assets.ast"));
	}

	public static void main(String[] args) {
		new Platformer().start();
	}
}
