import os, time, string
import commands, getpass
from xml.dom import minidom

def isCheckoutSuccessful(captured_output):
	lines = captured_output[1].split("\n")
	if (captured_output[0] == 0) and (lines[len(lines)-1].find("Checked out revision") == -1):
		return False	
	else:
		return True

def isBuildSuccessful(captured_output):
	lines = captured_output[1].split("\n")
	if (captured_output[0] == 0) and (lines[len(lines)-6].find("BUILD SUCCESSFUL") == -1):
		return False	
	else:
		return True

def findChildElement(childNodes, elementName):
	for node in childNodes:
		if node.nodeType == minidom.Node.ELEMENT_NODE and node.tagName == elementName:
			return node
	return None

def fixElementValue(childNodes, elementName, newValue):
	done = False
	for node in childNodes:
		if node.nodeType == minidom.Node.ELEMENT_NODE and node.tagName == elementName:
			node.childNodes[0].nodeValue = newValue
			done = True
			break
	return done

def writeFile(file_location, content):
	fp = open(file_location, "w")
	fp.write(content)
	fp.close()

def fixParentPomVersion(pom_location, parentVersion):
	# load the dom and fix it in memory
	dom = minidom.parse(pom_location)
	parentElement = findChildElement(dom.childNodes[0].childNodes, "parent")
	if parentElement:
		if not fixElementValue(parentElement.childNodes, "version", parentVersion):
			raise RuntimeError, "/project/parent/version not found in %s " % pom_location
	# overwrite the old file
	writeFile(pom_location, dom.toxml())

def getPomVersion(pom_dom):
	for node in pom_dom.childNodes[0].childNodes:
		if node.nodeType == minidom.Node.ELEMENT_NODE and node.tagName == "version":
			return node.childNodes[0].nodeValue
	return None

def fixPomVersion(pom_dom, version):
	if not fixElementValue(pom_dom.childNodes[0].childNodes, "version", version):
		raise RuntimeError, "/project/version not found in %s " % pom_location

def findModules(pom_dom):
	output = []
	modules_element = findChildElement(pom_dom.childNodes[0].childNodes, "modules")
	if modules_element:
		for node in modules_element.childNodes:
			if node.nodeType == minidom.Node.ELEMENT_NODE and node.tagName == "module":
				output.append(node.childNodes[0].nodeValue)
	return output
	
# location configs
refapp_base = "https://studio.atlassian.com/svn/REFAPP"
project_svn_base = "https://studio.atlassian.com/svn"

project_name = raw_input("project:")
project_branch = raw_input("project branch:")
refapp_branch = raw_input("refapp branch:")
svn_user = raw_input("svn username(leave blank=no user/passwd):")
svm_passwd = ""
if (svn_user != ""):
	svn_passwd = getpass.getpass("svn passwd:")

# build layout
timestamp = str(time.time()).replace(".", "")
build_dir_base = "/tmp/refapp_build_%s" % timestamp
build_dir = "%s/build" % build_dir_base
log_dir = "%s/logs" % build_dir_base

print "trying to build the project at %s" % build_dir_base

# create build layout structure
(mkdir_status, message) = commands.getstatusoutput("mkdir " + build_dir_base)
if (mkdir_status != 0):
	raise Exception, "problem while creating build layout:%s\n%s\n" % (build_dir_base, message)
(mkdir_status, message) = commands.getstatusoutput("mkdir " + build_dir)
if (mkdir_status != 0):
	raise Exception, "problem while creating build layout:%s\n%s\n" % (build_dir, message)
(mkdir_status, message) = commands.getstatusoutput("mkdir " + log_dir)
if (mkdir_status != 0):
	raise Exception, "problem while creating build layout:%s\n%s\n" % (log_dir, message)

# if user/passwd are needed for svn
svn_credential = ""
if (len(svn_user) != 0):
	svn_credential = """ --username "%s" --password "%s" """ % (svn_user, svn_passwd)

print "checking out refapp"
checkout_refapp = commands.getstatusoutput("svn checkout %s/branches/%s %s/refapp %s" % (refapp_base, refapp_branch, build_dir, svn_credential))
# if the checkout of refapp is unsuccessful
if (not isCheckoutSuccessful(checkout_refapp)):
	raise Exception, "problem while checking out refapp:\n" + checkout_refapp

print "refapp checked out"

print "checking out %s" % project_name
checkout_project = commands.getstatusoutput("svn checkout %s/%s/branches/%s %s/project %s" % (project_svn_base, project_name, project_branch, build_dir, svn_credential))
# if the checkout of project is unsuccessful
if not isCheckoutSuccessful(checkout_project):
	raise Exception, "problem while checking out project:\n" + checkout_project
print "%s checked out" % project_name

# now fix the project version number to the timestamp
project_pom = "%s/project/pom.xml" % build_dir
project_dom = minidom.parse(project_pom)
# get the old artifact version in project pom
old_project_version = getPomVersion(project_dom)
# now reformat it with timestamp and then replace the old version in the pom
timestamped_version = "%s.b%s" % (old_project_version.replace("-SNAPSHOT", ""), timestamp)
print "project version transformed %s => %s" % (old_project_version, timestamped_version)

# build project
print "building %s" % project_name
build_project = commands.getstatusoutput("""cd %s/project; 
                                            mvn release:prepare -DreleaseVersion=%s -DdevelopmentVersion=%s -DautoVersionSubmodules=true --batch-mode;
                                            mvn release:perform""" % (build_dir, timestamped_version, old_project_version))
# if the build of project is unsuccessful
if not isBuildSuccessful(build_project):
	raise RuntimeError, "problem while building project:\n" + build_project[1]

# write the maven build output to log file and display result on the screen
writeFile("%s/project_build.out" % log_dir, build_project[1])
print "%s built" % project_name
print "project version %s deployed" % timestamped_version

# now fix up the refapp pom before build
refapp_pom = "%s/refapp/pom.xml" % build_dir
refapp_dom = minidom.parse(refapp_pom)
refapp_props = refapp_dom.getElementsByTagName("properties")
# we just want to last one
refapp_props = refapp_props[len(refapp_props)-1]
# fix the project version
for node in refapp_props.childNodes:
	if node.nodeType == minidom.Node.ELEMENT_NODE and node.tagName == "%s.version" % project_name.lower():
		node.childNodes[0].nodeValue=timestamped_version
#		node.childNodes[0].nodeValue=upload_version
		break
# overwrite the old refapp pom
writeFile(refapp_pom, refapp_dom.toxml())

# build refapp
print "building refapp"
build_refapp = commands.getstatusoutput("cd %s/refapp; mvn deploy -Dmaven.test.skip=true" % build_dir)
# if the build of refapp is unsuccessful
if not isBuildSuccessful(build_refapp):
	raise RuntimeError, "problem while building refapp:\n" + build_refapp[1]

# write the maven build output to log file and display result on the screen
writeFile("%s/refapp_build.out" % log_dir, build_refapp[1])
print "refapp built"
