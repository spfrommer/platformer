from commons import Transform2f
from commons.matrix import Vector2f

from engine.core import CTransform, TagList
from engine.core import EntityBuilder
from engine.core import ComponentBuilder
from engine.core import Entity
from engine.core import CTags
from engine.core import TagList
from engine.core.script import XPython
from engine.imp.physics.dyn4j import CollisionFilter
from engine.imp.physics.dyn4j import CBody
from engine.imp.render import CRender

from gltools.input.Mouse import MouseButton

from org.dyn4j.geometry import Rectangle
from org.dyn4j.geometry.Mass import Type
from org.dyn4j.dynamics.contact import ContactConstraint

icicle_width = 0.2
icicle_height = 0.5

class IcicleCollision(CollisionFilter):
	def canCollide(self, entity1, entity2):
		ctags = entity2.getCTags()
		if (ctags.getTags().hasTag("icicle")):
			return False
		return True
	
	def continueCollision(self, entity1, entity2, contact):
		return True

class IciclePhysics(ComponentBuilder):
	angle = 0
	
	def setAngle(self, angle):
		self.angle = angle
		
	def build(self):
		physics = CBody();
		physics.setShape(Rectangle(icicle_width, icicle_height))
		physics.setVelocity(Vector2f(self.angle).setLength(10))
		physics.setCollisionFilter(IcicleCollision())
		return physics
		
	def getName(self):
		return CBody.NAME


icicle_image = assets.get("icicle")
iphysics = IciclePhysics()
icicle_script = assets.get("icicle_script")

icicle_builder = EntityBuilder()
icicle_builder.setTagList(TagList().newAdd("icicle"))
icicle_builder.addComponentBuilder(CRender(icicle_image, 2, 1))
icicle_builder.addComponentBuilder(iphysics)
icicle_builder.addScript(icicle_script)

last_jump = 0
jump_timeout = 1000

last_icicle = 0
icicle_timeout = 100
icicle_count = 0

def handleJump(time) :
	global last_jump
	last_jump += time
	if keyboard.isKeyPressed(keyboard.getKey("W")) :
		if last_jump >= jump_timeout and abs(body.getVelocity().getY()) < 0.00001 :
			body.applyForce(Vector2f(0, 15))
			last_jump = 0

def handleIcicle(time) :
	global last_icicle
	global icicle_count
	last_icicle += time
	game_mouse = mouse.getMouse()
	if game_mouse.isButtonDown(game_mouse.getButton(MouseButton.LEFT_BUTTON_NAME)) :
		if last_icicle >= icicle_timeout :
			translation = transform.getTransform().getTranslation()
			mouse_vector = Vector2f(mouse.getWorldX() - translation.getX(), mouse.getWorldY() - translation.getY())
			iphysics.setAngle(mouse_vector.angle())
			icicle_builder.setTransform(Transform2f(translation.add(mouse_vector.setLength(0.8)).toVector2f(), mouse_vector.angle(), Vector2f(icicle_width, icicle_height)))
			icicle = scene.createEntity("icicle" + str(icicle_count), scene, icicle_builder)
			last_icicle = 0
			icicle_count += 1
			body.applyForce(mouse_vector.setLength(-2))

def update(time):
	handleJump(time)
	handleIcicle(time)
	if (keyboard.isKeyPressed(keyboard.getKey("L"))) :
		game.scenes().setCurrentScene("level2")
	if (keyboard.isKeyPressed(keyboard.getKey("M"))) :
		game.scenes().setCurrentScene("main")