import os, sys
import shutil
import subprocess

for i in os.listdir(os.getcwd()):
	filename, ext = os.path.splitext(i)
	if ("svg" in ext):
		args = ["inkscape", "-z", "-e",
			os.path.join("..", "drawable-ldpi", filename+".png"),
			"-w", "36", "-h", "36", i]
		p = subprocess.Popen(args)
		p.communicate()
		args = ["inkscape", "-z", "-e",
			os.path.join("..", "drawable-mdpi", filename+".png"),
			"-w", "48", "-h", "48", i]
		p = subprocess.Popen(args)
		p.communicate()
		args = ["inkscape", "-z", "-e",
			os.path.join("..", "drawable-hdpi", filename+".png"),
			"-w", "72", "-h", "72", i]
		p = subprocess.Popen(args)
		p.communicate()
		args = ["inkscape", "-z", "-e",
			os.path.join("..", "drawable-xhdpi", filename+".png"),
			"-w", "96", "-h", "96", i]
		p = subprocess.Popen(args)
		p.communicate()
sys.exit("Done")
