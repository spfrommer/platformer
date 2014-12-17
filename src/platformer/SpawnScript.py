from engine.core import EntityBuilder
from commons import Transform2f
from engine.imp.render import CRender
from commons.matrix import Vector2f
from engine.core.script import XPython
import random

snowflakeImage = assets.get("snowflake")
snowflakeBuilder = EntityBuilder()
snowflakeBuilder.addComponentBuilder(CRender(snowflakeImage, 2, 0.5))

snowflakeScript = assets.get("snowflake_script")
snowflakeBuilder.addScript(snowflakeScript)

lastSnowflake = 0
snowflakeTimeout = 100
snowflakeCount = 0
spawnHeight = 5
spawnWidth = 20


def update(time) :
    global lastSnowflake
    global snowflakeCount
    lastSnowflake += time
    gameMouse = mouse.getMouse()
    
    camera = scene.find("player.camera")
    trans = scene.getWorldTransform(camera).getTranslation()
    
    if lastSnowflake >= snowflakeTimeout :
        snowflake = scene.createEntity("snowflake" + str(snowflakeCount), scene, snowflakeBuilder)
        snowflake.getCTransform().setTransform(Transform2f(Vector2f(trans.getX() + random.uniform(-spawnWidth, spawnWidth), trans.getY() + spawnHeight), 0, Vector2f(0.25, 0.25)))
        lastSnowflake = 0
        snowflakeCount += 1