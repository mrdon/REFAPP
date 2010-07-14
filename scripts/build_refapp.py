import os, sys, time, string
import commands, getpass
from xml.dom import minidom

# configs
refapp_base = "https://studio.atlassian.com/svn/REFAPP"
project_svn_base = "https://studio.atlassian.com/svn"
# the first %s will be replaced by the major+minor version number, the second by currentMillis()
version_format = "%s.alpha%s"

# current script version
script_version = [0, 1, 1]

# the flag for not deleting the working directory if the build is successful
no_delete = "no_delete"

# environment pre-conditions
if sys.version_info[:3] < [2, 4, 3]:
    	raise RuntimeError, "Requires python >= 2.4.3"

if sys.argv[1:].__contains__("version"):
	print "version %s" % ".".join(map(lambda x:str(x), script_version))
	sys.exit(0)

def isCheckoutSuccessful(captured_output):
	lines = captured_output.split("\n")
	if lines[len(lines)-1].find("Checked out revision") == -1:
		return False
	else:
		return True

def isBuildSuccessful(captured_output):
	lines = captured_output.split("\n")
	if lines[len(lines)-6].find("BUILD SUCCESSFUL") == -1:
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

def listSvnDirs(svn_url, svn_credential):
	"""
		list all the dirs under the given svn_url
	"""
	# simple issue svn list and parse the result
	print "retrieving info from svn......."
	list_cmd = "svn list %s %s --non-interactive" % (svn_url, svn_credential)
	(list_svn_status, list_svn_output) = commands.getstatusoutput(list_cmd)
	if list_svn_status != 0:
		raise RuntimeError, "having trouble trying to connect to svn:\n" + list_svn_output
	svn_output_lines = list_svn_output.split("\n")
	# only dirs are of our interest here. all dirs end with '/'
	return map(lambda x:x.rstrip("/"), svn_output_lines)

def takeUserDirChoice(prompt_text, svn_url, svn_credential):
	"""
		prompt until user makes a valid choice.
		if user inputs something without listing the choices, we assume
		the user knows what he is doing and bypass the checking process
		with remote svn to save time.
	"""
	choice = raw_input(prompt_text)
	choices = None
	while choice == "" or choice.isspace():
		# read from svn only for the first time
		if not choices:
			choices = listSvnDirs(svn_url, svn_credential)
		# print out all available choices
		print "available choices = ['" + "', '".join(choices) + "']\n"
		choice = raw_input(prompt_text)
		# if user enters a non-existing project name, we don't accept it
		if not choices.__contains__(choice):
			choice = ""
	return choice

# clean up configs
refapp_base = refapp_base.rstrip("/")
project_svn_base = project_svn_base.rstrip("/")

# getting inputs from users
svn_user = raw_input("svn username(leave blank=no user/passwd):")
svm_passwd = ""
if (svn_user != ""):
	svn_passwd = getpass.getpass("svn passwd:")

# construct user/passwd parameters for svn command line
svn_credential = ""
if svn_user != "":
	svn_credential = """ --username "%s" --password "%s" """ % (svn_user, svn_passwd)

# getting projects to build
project_name = takeUserDirChoice("project(blank for listing):", project_svn_base, svn_credential)
project_branch = takeUserDirChoice("project branch(blank for listing):", "%s/%s/branches" % (project_svn_base, project_name), svn_credential)
refapp_branch = takeUserDirChoice("refapp branch(blank for listing):", "%s/branches" % refapp_base, svn_credential)

# build layout
timestamp = str(time.time()).replace(".", "")
build_dir_base = "/tmp/refapp_build_%s" % timestamp
build_dir = "%s/build" % build_dir_base
log_dir = "%s/logs" % build_dir_base

print "trying to build the project at %s" % build_dir_base

# create build layout structure
(mkdir_status, message) = commands.getstatusoutput("mkdir " + build_dir_base)
if (mkdir_status != 0):
	raise RuntimeError, "problem while creating build layout:%s\n%s\n" % (build_dir_base, message)
(mkdir_status, message) = commands.getstatusoutput("mkdir " + build_dir)
if (mkdir_status != 0):
	raise RuntimeError, "problem while creating build layout:%s\n%s\n" % (build_dir, message)
(mkdir_status, message) = commands.getstatusoutput("mkdir " + log_dir)
if (mkdir_status != 0):
	raise RuntimeError, "problem while creating build layout:%s\n%s\n" % (log_dir, message)

print "==========START BUILDING=========="
print "checking out refapp"
(checkout_refapp_status, checkout_refapp_output) = commands.getstatusoutput("svn checkout %s/branches/%s %s/refapp %s --non-interactive" % (refapp_base, refapp_branch, build_dir, svn_credential))
# if the checkout of refapp is unsuccessful
if (checkout_refapp_status != 0) or (not isCheckoutSuccessful(checkout_refapp_output)):
	raise RuntimeError, "problem while checking out refapp:\n" + checkout_refapp_output

print "refapp checked out"

print "checking out %s" % project_name
(checkout_project_status, checkout_project_output) = commands.getstatusoutput("svn checkout %s/%s/branches/%s %s/project %s --non-interactive" % (project_svn_base, project_name, project_branch, build_dir, svn_credential))
# if the checkout of project is unsuccessful
if (checkout_project_status != 0) or (not isCheckoutSuccessful(checkout_project_output)):
	raise RuntimeError, "problem while checking out project:\n" + checkout_project_output
print "%s checked out" % project_name

# now fix the project version number to the timestamp
project_pom = "%s/project/pom.xml" % build_dir
project_dom = minidom.parse(project_pom)
# get the old artifact version in project pom
old_project_version = getPomVersion(project_dom)
# now reformat it with timestamp and then replace the old version in the pom
timestamped_version = version_format % (old_project_version.replace("-SNAPSHOT", ""), timestamp)
print "project version transformed %s => %s" % (old_project_version, timestamped_version)

# build project
print "building %s" % project_name
project_build_cmd = """cd %s/project;
                       mvn release:prepare -DreleaseVersion=%s -DdevelopmentVersion=%s -DautoVersionSubmodules=true --batch-mode;
                       mvn release:perform""" % (build_dir, timestamped_version, old_project_version)
(build_project_status, build_project_output) = commands.getstatusoutput(project_build_cmd)
# write the maven build output to log file
writeFile("%s/project_build.out" % log_dir, build_project_output)
# if the build of project is unsuccessful
if (build_project_status != 0) or (not isBuildSuccessful(build_project_output)):
	raise RuntimeError, "problem while building project:\n" + build_project_output

print "%s version %s built and deployed" % (project_name, timestamped_version)


# build refapp
# get the old artifact version in refapp pom
refapp_pom = "%s/refapp/pom.xml" % build_dir
refapp_dom = minidom.parse(refapp_pom)
old_refapp_version = getPomVersion(refapp_dom)
# the property key to point to the project version
project_version_key = "%s.version" % project_name.lower()
# now reformat it with timestamp and then replace the old version in the pom
refapp_timestamped_version = version_format % (old_refapp_version.replace("-SNAPSHOT", ""), timestamp)
print "refapp version transformed %s => %s" % (old_refapp_version, refapp_timestamped_version)
print "building refapp"
refapp_build_cmd = """cd %s/refapp;
                      mvn release:prepare -DreleaseVersion=%s -DdevelopmentVersion=%s -DautoVersionSubmodules=true --batch-mode -D%s=%s;
                      mvn release:perform -D%s=%s""" % (build_dir, refapp_timestamped_version, old_refapp_version, project_version_key, timestamped_version, project_version_key, timestamped_version)
(build_refapp_status, build_refapp_output) = commands.getstatusoutput(refapp_build_cmd)

# write the maven build output to log file and display result on the screen
writeFile("%s/refapp_build.out" % log_dir, build_refapp_output)

# if the build of refapp is unsuccessful
if (build_refapp_status != 0) or (not isBuildSuccessful(build_refapp_output)):
	raise RuntimeError, "problem while building refapp:\n" + build_refapp_output

# (possibly) the final message to user
print "refapp version %s built and deployed" % refapp_timestamped_version
print "==========SUCCESSFULLY BUILT REFAPP=========="

# delete the working dir if the whole thing is successful
if sys.argv[1:].__contains__("no_delete"):
	print "=========================================================="
	print "the build result can be found at %s" % build_dir
	print "the build logs can be found at %s" % log_dir
	print "=========================================================="
else:
	commands.getstatusoutput("rm -rf %s" % build_dir_base)

# return a successful code
sys.exit(0)
