import random

last_dir_change = 0
dir_change_time = 300
transx = random.uniform(-0.0002, 0.0002)
rotation = random.uniform(-0.01, 0.01)

def update(time):
	global last_dir_change
	global dir_change_time
	global transx
	
	transform.translate(transx * time, -0.0005 * time)
	transform.rotate(rotation)
	if transform.getTransform().getTranslation().getY() <= 0 :
		scene.destroyEntity(entity);
	
	last_dir_change += time
	if last_dir_change >= dir_change_time :
		transx = random.uniform(-0.0002, 0.0002)
		dir_change_time = random.randint(500, 900)
		last_dir_change = 0