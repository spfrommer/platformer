from commons.matrix import Vector2f
from commons import Transform2f

from engine.core import EntityBuilder
from engine.imp.render import CRender
from engine.core.script import XPython

import random

snowflake_image = assets.get("snowflake")
snowflake_builder = EntityBuilder()
snowflake_builder.addComponentBuilder(CRender(snowflake_image, 1, 1.2))

snowflake_script = assets.get("snowflake_script")
snowflake_builder.addScript(snowflake_script)

last_snowflake = 0
snowflake_timeout = 400
snowflake_count = 0
spawn_height = 5
spawn_width = 3

def update(time) :
    global last_snowflake
    global snowflake_count
    last_snowflake += time
    game_mouse = mouse.getMouse()
    if last_snowflake >= snowflake_timeout :
        snowflake = scene.createEntity("snowflake" + str(snowflake_count), scene, snowflake_builder)
        snowflake.getCTransform().setTransform(Transform2f(Vector2f(random.uniform(-spawn_width, spawn_width), spawn_height), 0, Vector2f(0.25, 0.25)))
        last_snowflake = 0
        snowflake_count += 1