from engine.core import EntityBuilder
from commons import Transform2f
from engine.imp.render import CRender
from commons.matrix import Vector2f
from engine.core.script import XPython
import random

snowflakeImage = assets.getAsset("snowflake").getAsset()
snowflakeBuilder = EntityBuilder()
snowflakeBuilder.addComponentBuilder(CRender(snowflakeImage, 1, 1))

snowflakeScript = XPython(assets.getAsset("snowflake_script").getAsset())
snowflakeBuilder.addScript(snowflakeScript)

lastSnowflake = 0
snowflakeTimeout = 400
snowflakeCount = 0
spawnHeight = 5
spawnWidth = 3

def update(time) :
    global lastSnowflake
    global snowflakeCount
    lastSnowflake += time
    gameMouse = mouse.getMouse()
    if lastSnowflake >= snowflakeTimeout :
        snowflake = scene.createEntity("snowflake" + str(snowflakeCount), scene, snowflakeBuilder)
        snowflake.getCTransform().setTransform(Transform2f(Vector2f(random.uniform(-spawnWidth, spawnWidth), spawnHeight), 0, Vector2f(0.5, 0.5)))
        lastSnowflake = 0
        snowflakeCount += 1