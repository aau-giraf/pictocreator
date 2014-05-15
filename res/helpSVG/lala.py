import os, sys
import shutil
import subprocess

for i in os.listdir(os.getcwd()):
	filename, ext = os.path.splitext(i)
	if ("svg" in ext):
		args = ["inkscape", "-z", "-e",
			os.path.join("..", "drawable-mdpi", filename+".png"),
			"-w", "1280", "-h", "1650", i]
		p = subprocess.Popen(args)
		p.communicate()
sys.exit("Done")
