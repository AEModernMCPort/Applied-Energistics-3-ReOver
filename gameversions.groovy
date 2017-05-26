import groovy.json.*

def mcVersion = args[0]
def javaVersion = args[1].replace('1.', '') as Integer

def versions = []

new JsonSlurper().parse(new File("gameversions.json")).each { version ->
	if(version.name.contains('Java')){
		if((version.name.replace('Java ', '') as Integer) >= javaVersion) versions << version.id
	} else if(version.name == mcVersion){
		if(version.gameVersionTypeID != 1) versions << version.id
	}
}

print versions