from engine.core import CTransform
from engine.core import EntityBuilder
from engine.core import ComponentBuilder
from engine.imp.physics.dyn4j import CBody
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
		physics = CBody();
		physics.setShape(Rectangle(icicleWidth, icicleHeight))
		physics.setVelocity(Vector2f(self.m_angle).setLength(5))
		return physics
		
	def getName(self):
		return CBody.NAME

lastJump = 0
jumpTimeout = 1000

lastIcicle = 0
icicleTimeout = 100
icicleCount = 0

icicleImage = assets.get("icicle")
iPhysics = IciclePhysics()
icicleBuilder = EntityBuilder()
icicleBuilder.addComponentBuilder(CRender(icicleImage, 1, 1))
icicleBuilder.addComponentBuilder(iPhysics)

icicleScript = assets.get("icicle_script")
icicleBuilder.addScript(icicleScript)

def handleJump(time) :
	global lastJump
	lastJump += time
	if keyboard.isKeyPressed(keyboard.getKey("W")) :
		if lastJump >= jumpTimeout and body.getVelocity().getY() < 0.01 :
			body.applyForce(Vector2f(0, 15))
			lastJump = 0

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
			icicleBuilder.setTransform(Transform2f(translation.add(mouseVector.setLength(1)).toVector2f(), mouseVector.angle(), Vector2f(icicleWidth, icicleHeight)))
			icicle = scene.createEntity("icicle" + str(icicleCount), scene, icicleBuilder)
			lastIcicle = 0
			icicleCount += 1
			body.applyForce(mouseVector.setLength(-5))

def update(time):
	handleJump(time)
	handleIcicle(time)