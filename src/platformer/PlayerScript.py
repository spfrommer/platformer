from engine.core import CTransform
from engine.core import EntityBuilder
from engine.core import ComponentBuilder
from engine.imp.physics.dyn4j import CDyn4jBody
from commons import Transform2f
from engine.imp.render import CRender
from commons.matrix import Vector2f
from gltools.input.Mouse import MouseButton
from engine.core.script import XPython
from org.dyn4j.geometry import Rectangle
from org.dyn4j.geometry.Mass import Type

icicleWidth = 0.2
icicleHeight = 0.5

class IciclePhysics(ComponentBuilder):
	m_angle = 0
	
	def setAngle(self, angle):
		self.m_angle = angle
		
	def build(self):
		physics = CDyn4jBody();
		physics.setShape(Rectangle(icicleWidth, icicleHeight))
		physics.setVelocity(Vector2f(self.m_angle).setLength(5))
		return physics
		
	def getName(self):
		return CDyn4jBody.NAME

lastJump = 0
jumpTimeout = 1000

lastIcicle = 0
icicleTimeout = 100
icicleCount = 0

icicleImage = assets.getAsset("icicle").getAsset()
iPhysics = IciclePhysics()
icicleBuilder = EntityBuilder()
icicleBuilder.addComponentBuilder(CRender(icicleImage, 1, 1))
icicleBuilder.addComponentBuilder(iPhysics)

icicleScript = XPython(assets.getAsset("icicle_script").getAsset())
icicleBuilder.addScript(icicleScript)

def handleJump(time) :
	global lastJump
	lastJump += time
	if keyboard.isKeyPressed(keyboard.getKey("W")) :
		if lastJump >= jumpTimeout and dyn4jBody.getVelocity().getY() < 0.1 :
			dyn4jBody.applyForce(Vector2f(0, 15))
			lastJump = 0

def handleJetpack(time) :
	gameMouse = mouse.getMouse()
	if gameMouse.isButtonDown(gameMouse.getButton(MouseButton.RIGHT_BUTTON_NAME)) :
		dyn4jBody.applyForce(Vector2f(0, 0.5))

def handleIcicle(time) :
	global lastIcicle
	global icicleCount
	lastIcicle += time
	gameMouse = mouse.getMouse()
	if gameMouse.isButtonDown(gameMouse.getButton(MouseButton.LEFT_BUTTON_NAME)) :
		if lastIcicle >= icicleTimeout :
			translation = transform.getTransform().getTranslation()
			mouseVector = Vector2f(mouse.getWorldX() - translation.getX(), mouse.getWorldY() - translation.getY())
			iPhysics.setAngle(mouseVector.angle())
			icicle = scene.createEntity("icicle" + str(icicleCount), scene, icicleBuilder)
			icicle.getCTransform().setTransform(Transform2f(translation.add(mouseVector.setLength(1)).toVector2f(), mouseVector.angle(), Vector2f(icicleWidth, icicleHeight)))
			lastIcicle = 0
			icicleCount += 1

def update(time):
	handleJump(time)
	handleJetpack(time)
	handleIcicle(time)