import random

lastDirChange = 0
dirChangeTime = 300
transx = 0.001
rotation = random.uniform(-0.01, 0.01)

def update(time):
	global lastDirChange
	global dirChangeTime
	global transx
	
	transform.translate(transx, -0.005)
	transform.rotate(rotation)
	if transform.getTransform().getTranslation().getY() <= 0 :
		scene.destroyEntity(entity);
	
	lastDirChange += time
	if lastDirChange >= dirChangeTime :
		transx = random.uniform(-0.002, 0.002)
		dirChangeTime = random.randint(500, 900)
		lastDirChange = 0