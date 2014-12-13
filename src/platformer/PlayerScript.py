from engine.core import CTransform
from engine.core import EntityBuilder
from engine.imp.render import CRender
from commons.matrix import Vector2f
from gltools.input.Mouse import MouseButton

lastJump = 0
jumpTimeout = 1000

lastSnowflake = 0
snowflakeTimeout = 100
snowflakeCount = 0

snowflakeImage = assets.getAsset("snowflake").getAsset()
snowflakeBuilder = EntityBuilder()
snowflakeBuilder.addComponentBuilder(CRender(snowflakeImage, 1, 1))

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
			snowflake = scene.createEntity("snowflake" + str(snowflakeCount), scene, snowflakeBuilder)
			snowflake.getCTransform().translate(mouse.getWorldX(), mouse.getWorldY())
			lastSnowflake = 0
			snowflakeCount += 1

def update(time):
	handleJump(time)
	handleSnowflake(time)
	if keyboard.isKeyPressed(keyboard.getKey("D")) :
		transform.translate(0.03, 0)
	if keyboard.isKeyPressed(keyboard.getKey("A")) :
		transform.translate(-0.03, 0)