from commons import Transform2f
from commons.matrix import Vector2f

from engine.core import EntityBuilder
from engine.imp.render import CRender
from engine.core.script import XPython
from engine.imp.physics.dyn4j import CBody

from org.dyn4j.geometry import Rectangle
from org.dyn4j.geometry import Circle
from org.dyn4j.geometry.Mass import Type
import random

enemy_width = 0.5
enemy_height = 0.5

enemy_image = assets.get("target")
enemy_script = assets.get("enemy_script")
enemy_physics = CBody()
enemy_physics.setShape(Circle(enemy_width / 2));
enemy_physics.setMassType(Type.INFINITE)

enemy_builder = EntityBuilder()
enemy_builder.addComponentBuilder(CRender(enemy_image, 2, 1))
enemy_builder.addComponentBuilder(enemy_physics)
enemy_builder.addScript(enemy_script)

last_enemy = 0
enemy_timeout = 5000
enemy_count = 0
spawn_height = 3
spawn_width = 3
    
def update(time) :
    global last_enemy
    global enemy_count
    last_enemy += time
    game_mouse = mouse.getMouse()
    if last_enemy >= enemy_timeout :
        snowflake = scene.createEntity("enemy" + str(enemy_count), scene, enemy_builder)
        snowflake.getCTransform().setTransform(Transform2f(Vector2f(random.uniform(-spawn_width, spawn_width), random.uniform(-spawn_height, spawn_height) + 2),
                                                            0, Vector2f(enemy_width, enemy_height)))
        last_enemy = 0
        enemy_count += 1