from engine.core import CTransform
from engine.core import EntityBuilder
from commons import Transform2f
from engine.imp.render import CRender
from commons.matrix import Vector2f
from gltools.input.Mouse import MouseButton
from engine.core.script import XPython

lastJump = 0
jumpTimeout = 1000

lastSnowflake = 0
snowflakeTimeout = 100
snowflakeCount = 0

snowflakeImage = assets.getAsset("snowflake").getAsset()
snowflakeBuilder = EntityBuilder()
snowflakeBuilder.addComponentBuilder(CRender(snowflakeImage, 1, 1))

snowflakeScript = XPython(assets.getAsset("snowflake_script").getAsset())
snowflakeBuilder.addScript(snowflakeScript)

def handleJump(time) :
	global lastJump
	lastJump += time
	if keyboard.isKeyPressed(keyboard.getKey("W")) :
		if lastJump >= jumpTimeout :
			dyn4jBody.applyForce(Vector2f(0, 15))
			lastJump = 0

def handleSnowflake(time) :
	global lastSnowflake
	global snowflakeCount
	lastSnowflake += time
	gameMouse = mouse.getMouse()
	if gameMouse.isButtonDown(gameMouse.getButton(MouseButton.LEFT_BUTTON_NAME)) :
		if lastSnowflake >= snowflakeTimeout :
			snowflake = scene.createEntity("playersnowflake" + str(snowflakeCount), scene, snowflakeBuilder)
			#snowflake.getCTransform().translate(mouse.getWorldX() , mouse.getWorldY())
			snowflake.getCTransform().setTransform(Transform2f(Vector2f(mouse.getWorldX(), mouse.getWorldY()), 0, Vector2f(0.5, 0.5)))
			lastSnowflake = 0
			snowflakeCount += 1

def update(time):
	handleJump(time)
	handleSnowflake(time)
	if keyboard.isKeyPressed(keyboard.getKey("D")) :
		transform.translate(0.03, 0)
	if keyboard.isKeyPressed(keyboard.getKey("A")) :
		transform.translate(-0.03, 0)