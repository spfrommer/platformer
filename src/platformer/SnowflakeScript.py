import random

lastDirChange = 0
dirChangeTime = 300
transx = random.uniform(-0.0002, 0.0002)
rotation = random.uniform(-0.01, 0.01)

def update(time):
	global lastDirChange
	global dirChangeTime
	global transx
	
	transform.translate(transx * time, -0.0005 * time)
	transform.rotate(rotation)
	if transform.getTransform().getTranslation().getY() <= 0 :
		scene.destroyEntity(entity);
	
	lastDirChange += time
	if lastDirChange >= dirChangeTime :
		transx = random.uniform(-0.0002, 0.0002)
		dirChangeTime = random.randint(500, 900)
		lastDirChange = 0