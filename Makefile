all: OBJS

FLAGS = -g
JC = javac

SRCS =\
	Model.java\
	Parser.java\
	Models.java\
	StateSets.java

OBJS: $(SRCS:.java=.class)

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

clean:
	rm -f *.class

tar:
	tar czvf java.tgz .
