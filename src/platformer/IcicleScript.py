startPosition = scene.getWorldTransform(entity).getTranslation();

def update(time):
    position = scene.getWorldTransform(entity).getTranslation();
    change = position.subtract(startPosition).toVector2f();
    if change.length() > 5 :
        scene.destroyEntity(entity)