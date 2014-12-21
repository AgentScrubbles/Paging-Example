proj2:
	javac src/proj2/*.java -d bin

outputDir:
	mkdir bin

clean:
	rm bin/proj2/*.class
