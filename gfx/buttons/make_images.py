#!/usr/bin/python3

import glob, subprocess, sys

def export_as_png(filename, s):
	fout = "%s_%sx%s.png" % (filename.replace(".svg",""), s, s)
	subprocess.call("inkscape -f %s -e %s -w %s -h %s" % (filename, fout, s, s), shell=True, stdout=subprocess.PIPE)
	return fout

try:
	subprocess.check_call(["inkscape", "--help"], stdout=subprocess.PIPE)
except Error as e:
	print("You don't seem to have inkscape installed in your path. Install and try again so \"inkscape --help\" works from your shell.")
	sys.exit(1)

files = glob.glob("*.svg")

print("Exporting %s files." % len(files))

for f in files:
	# print("%s as %s (%s,%s)" % (f, f.replace("svg", "png"), 100, 100))
	#export_as_png(f, 250)
	#export_as_png(f, 50)
	print(f)
	subprocess.call("mv %s ../../source/PictoCreator/res/drawable/" % export_as_png(f, 100), shell=True, stdout=subprocess.PIPE)
