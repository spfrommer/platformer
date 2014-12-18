from engine.core import Entity

from org.dyn4j.dynamics.contact import ContactConstraint

start_position = scene.getWorldTransform(entity).getTranslation();

def update(time):
    position = scene.getWorldTransform(entity).getTranslation();
    change = position.subtract(start_position).toVector2f();
    if change.length() > 5 :
        scene.destroyEntity(entity)
        

def onContact(hit, contact):
    hit.scripts().callFunc("damage", 5)
    if (scene.contains(entity)) :
        scene.destroyEntity(entity)