import os, sys, time, string
import commands, getpass
from xml.dom import minidom

# current script version
script_version = [0, 2, 0]

# the flag for not deleting the working directory if the build is successful
no_delete = "no_delete"

# configs
project_svn_base = "https://studio.atlassian.com/svn"
refapp_project_name = "REFAPP"

# version type namings
version_types = { "alpha":"alpha",
                  "beta":"beta",
                  "final":"" }

############################# auxiliary functions #############################
def create_svn_cred_param(svn_credential):
    """
    Create the user credential string as used as parameter in svn command line.
    For simplicity of use if it's not required just return an empty string.
    """
    if not svn_credential:
	    return """ --username "%s" --password "%s" """ % (svn_credential[0], svn_credential[1])
    else:
        return ""

def svn_list_dirs(svn_url, svn_credential):
    """
	List all the dirs under the given svn_url. The ending '/' are alwayes stripped.
	"""
    print "please wait while retrieving info from svn......."
    svn_cred_param = create_svn_cred_param(svn_credential)
    list_cmd = "svn list %s %s --non-interactive" % (svn_url, svn_cred_param)
    (list_svn_status, list_svn_output) = commands.getstatusoutput(list_cmd)
    if list_svn_status != 0:
        raise RuntimeError, "having trouble trying to connect to svn:\n" + list_svn_output
    svn_output_lines = list_svn_output.split("\n")
	# only dirs of our interest here. all dirs end with '/'
    dirs = filter(lambda x:x.endswith("/"), svn_output_lines)
    return map(lambda x:x.rstrip("/"), dirs)

def create_new_build_layout():
    """
    Create a new build layout on filesystem.
    """
    # a way to uniquely create a new build dir
    time_millis = str(time.time()).replace(".", "")
    build_dir_base = "/tmp/refapp_build_%s" % time_millis
    build_dir = "%s/build" % build_dir_base
    log_dir = "%s/logs" % build_dir_base
    tmp_dir = "%s/tmp" % build_dir_base

    # create build layout structure
    print "creating build layout at %s" % build_dir_base
    (mkdir_status, message) = commands.getstatusoutput("mkdir " + build_dir_base)
    if (mkdir_status != 0):
        raise RuntimeError, "problem while creating build layout:%s\n%s\n" % (build_dir_base, message)
    (mkdir_status, message) = commands.getstatusoutput("mkdir " + build_dir)
    if (mkdir_status != 0):
        raise RuntimeError, "problem while creating build layout:%s\n%s\n" % (build_dir, message)
    (mkdir_status, message) = commands.getstatusoutput("mkdir " + log_dir)
    if (mkdir_status != 0):
        raise RuntimeError, "problem while creating build layout:%s\n%s\n" % (log_dir, message)
    (mkdir_status, message) = commands.getstatusoutput("mkdir " + tmp_dir)
    if (mkdir_status != 0):
        raise RuntimeError, "problem while creating build layout:%s\n%s\n" % (tmp_dir, message)

    return (time_millis, build_dir_base, build_dir, log_dir, tmp_dir)

def writeFile(file_location, content):
	fp = open(file_location, "w")
	fp.write(content)
	fp.close()

############################# xml related auxiliary functions #############################
def findChildElement(childNodes, elementName):
	for node in childNodes:
		if node.nodeType == minidom.Node.ELEMENT_NODE and node.tagName == elementName:
			return node
	return None

def fixElementValue(childNodes, elementName, newValue):
    """
    Changes the value and return the old value.
    """
    for node in childNodes:
        if node.nodeType == minidom.Node.ELEMENT_NODE and node.tagName == elementName:
            old_value = node.childNodes[0].nodeValue
            node.childNodes[0].nodeValue = newValue
            return old_value
    return None

def getTopLevelElementContent(pom_dom, element_name):
	for node in pom_dom.childNodes[0].childNodes:
		if node.nodeType == minidom.Node.ELEMENT_NODE and node.tagName == element_name:
			return node.childNodes[0].nodeValue
	return None

############################## next version determiner #############################
def determine_next_version_number(project_base_url, svn_credential, artifact_id, base_version, version_type):
    """
    Determine the next version by looking at the existing tags.
    ***This should not be used for final versions***.
    """
    tags = svn_list_dirs("%s/tags" % project_base_url, svn_credential)
    full_version_base = "%s-%s.%s" % (artifact_id, base_version, version_types[version_type])
    return determine_next_version_number_internal(tags, full_version_base)

def determine_next_version_number_internal(available_tags, full_base_version):
    """
    Gives the next number as version number.
    """
    # only look at tags of the interested base version
    qualified_tags = filter(lambda x:x.startswith(full_base_version), available_tags)
    # determine the next version
    # if none exists, here we have the first one
    if len(qualified_tags) == 0:
        return 1
    #otherwise get the next number
    else:
        return max(map(lambda x:int(x[len(full_base_version):]), qualified_tags))+1

############################ user parameter taker ####################################
def take_project_params(project_name, svn_credential):
    """
    Take additional project build parameters from users.
    This function assumes the supplied project_name is correct.
    """
    # calculate project base url, this should be valid whether it's actually a project or the refapp project
    project_base_url = "%s/%s" % (project_svn_base, project_name)
    # get available choices = all tags union 'trunk'
    tags = svn_list_dirs("%s/branches" % project_base_url, svn_credential)
    branches = ["trunk"] + tags
    branch = None

    # loop until user selects a legit branch
    while not branch:
        print "%s branches = [ %s ]" % (project_name, ", ".join(branches))
        branch = raw_input("select branch:")
        if not branches.__contains__(branch):
			branch = None
    version_type = None

    # loop until user selects a legit version type
    while not version_type:
        print "version types = [ %s ]" % ", ".join(version_types.keys())
        version_type = raw_input("select version type(blank = alpha):")
        if version_type == "":
            version_type = "alpha"
        if not version_types.__contains__(version_type):
            version_type = None

    src_location = ""
    if branch == "trunk":
        src_location = "%s/%s/trunk" % (project_svn_base, project_name)
    else:
        src_location = "%s/%s/branches/%s" % (project_svn_base, project_name, branch)

    return (branch, src_location, version_type)

def take_next_version_modifier(src_url, project_base_url, version_type, tmp_dir, svn_credential):
    """
    Prompts the release version from user.
    """
    # then try to guess the next version
    if (version_type != "final"):
        # have to checkout project pom to see the artifact_id and base_version
        # first create a tmp dir for it under the current tmp_dir
        current_millis = str(time.time()).replace(".", "")
        sub_tmp_dir = "%s/%s" % (tmp_dir, current_millis)
        (mkdir_status, message) = commands.getstatusoutput("mkdir " + sub_tmp_dir)
        if (mkdir_status != 0):
            raise RuntimeError, "problem while mkdir %s\n%s\n" % (sub_tmp_dir, message)

        # now export only pom.xml of the project
        (svn_export_status, svn_export_output) = commands.getstatusoutput("cd %s; svn export %s/pom.xml %s --non-interactive" % (sub_tmp_dir, src_url, create_svn_cred_param(svn_credential)))
        # check the export outcome
        if svn_export_status != 0:
	        raise RuntimeError, "problem while exporting project pom.xml:%s\n" % svn_export_output

        # load the pom as dom
        dom = minidom.parse("%s/pom.xml" % sub_tmp_dir)
        # read the artifactId and version
        artifact_id = getTopLevelElementContent(dom, "artifactId")
        base_version = getTopLevelElementContent(dom, "version").replace("-SNAPSHOT", "")

        # now we can determine the next version
        next_num = determine_next_version_number(project_base_url, svn_credential, artifact_id, base_version, version_type)
        next_version_modifier = "%s%s" % (version_types[version_type], next_num)

        # let the user override the next version
        user_next_modifier = raw_input("enter the version modifier(blank = %s):" % next_version_modifier)
        if user_next_modifier=="":
            return (next_version_modifier, "%s.%s" % (base_version, next_version_modifier))
        else:
            return (user_next_modifier, "%s.%s" % (base_version, user_next_modifier))
    else:
        # release empty string since a final version doesn't need a version modifier
        return ("", base_version)

def take_project_name(svn_credential):
    """
    Take a valid project_name from user.
    """
    # retrieve all the available projects
    projects = svn_list_dirs(project_svn_base, svn_credential)
    # loop until we get a correct project name
    project_name = None
    while not project_name:
        print "projects = [ %s ]" % ", ".join(projects)
        project_name = raw_input("enter project name:")
        if not projects.__contains__(project_name):
			project_name = None
    # return only a valid one
    return project_name

def take_yes_no(prompt):
    """
    Take yes/no answer from user.
    """
    answer = None
    while not answer:
        full_prompt = "%s [yes/no]" % prompt
        answer = raw_input(full_prompt)
        if answer not in ["yes", "no"]:
            answer = None
    return answer
############################ maven builder #######################################

def svn_checkout(src_url, build_dir, svn_credential, project_name, branch_name):
    """
    Check out source code and return the local source dir.
    """
    print "checking out %s:%s" % (project_name, branch_name)
    checkout_dir = "%s/%s_%s" % (build_dir, project_name, branch_name)
    svn_cred_param = create_svn_cred_param(svn_credential)
    (checkout_status, checkout_output) = commands.getstatusoutput("svn checkout %s %s %s --non-interactive" % (src_url, checkout_dir, svn_cred_param))
    # check if the checkout of project is successful
    if (checkout_status != 0):
        raise RuntimeError, "problem while checking out project:%s" % checkout_output
    print "%s:%s checked out" % (project_name, branch_name)
    return checkout_dir

def svn_commit(local_dir, svn_credential, commit_message):
    """
    Commit the specified dir to svn.
    """
    svn_cred_param = create_svn_cred_param(svn_credential)
    commit_cmd = """cd %s;
                    svn commit -m "%s" %s --non-interactive""" % (local_dir, commit_message, svn_cred_param)
    (commit_status, commit_output) = commands.getstatusoutput(commit_cmd)
    if commit_status != 0:
        raise RuntimeError, "failed while committing %s:%s" % (local_dir, commit_output)

def build_project(project_name, branch_name, build_dir, log_dir, src_url, version_modifier, svn_credential, module_versions):
    """
    Checkout and build the project using mvn release plugin
    """
    # checkout the source code
    src_dir = svn_checkout(src_url, build_dir, svn_credential, project_name, branch_name)
    # the pom location for this project
    pom = "%s/pom.xml" % src_dir
    # calculate the output versions
    dom = minidom.parse(pom)
    current_project_version = getTopLevelElementContent(dom, "version")
    release_version = "%s.%s" % (current_project_version.replace("-SNAPSHOT", ""), version_modifier)

    # if module_versions contains something, we have to change the module versions.
    # old_values contains all the old propery values before we change.
    old_values = {}
    if len(module_versions) > 0:
        propsElem = findChildElement(dom.childNodes[0].childNodes, "properties")
        for project_version_key, indicated_version in module_versions.items():
            old_values[project_version_key] = fixElementValue(propsElem.childNodes, project_version_key, indicated_version)
            print "converted %s = %s" % (project_version_key, indicated_version)
        writeFile(pom, dom.toxml())
        # commit the edited pom
        svn_commit(src_dir, svn_credential, "[buildrefapp] prepare to build")

    # build by calling mvn release plugin
    print "building %s:%s artifact version (in pom):%s release version:%s" % (project_name, branch_name, current_project_version, release_version)
    project_release_prepare_cmd = """cd %s;
                                     mvn release:prepare -DreleaseVersion=%s -DdevelopmentVersion=%s -DautoVersionSubmodules=true --batch-mode""" % (src_dir, release_version, current_project_version)
    print project_release_prepare_cmd
    (prepare_project_status, prepare_project_output) = commands.getstatusoutput(project_release_prepare_cmd)

    # always write the maven build output to log file
    writeFile("%s/%s_%s_release_prepare1.out" % (log_dir, project_name, branch_name), prepare_project_output)

    # usually the mvn release plugin will fail due to the svn problem. following is how to work around it.
    if (prepare_project_status != 0):
        # now update the svn cache and execute the prepare stage again with resume option.
        # if this doesn't work, there is no other way to automatically recover.
        project_release_prepare_resume_cmd = """cd %s;
                                                svn up -r head;
                                                mvn release:prepare -DreleaseVersion=%s -DdevelopmentVersion=%s -DautoVersionSubmodules=true  -Dresume --batch-mode""" % (src_dir, release_version, current_project_version)
        print project_release_prepare_resume_cmd
        (project_release_prepare_resume_status, project_release_prepare_resume_output) = commands.getstatusoutput(project_release_prepare_resume_cmd)
        # always write the log
        writeFile("%s/%s_%s_release_prepare2.out" % (log_dir, project_name, branch_name), prepare_project_output)
        if project_release_prepare_resume_status != 0:
            raise RuntimeError, "the problem with mvn release:prepare bug:%s is unrecoverable\n" % project_release_prepare_resume_output

    # now perform the release
    project_release_perform_cmd = """cd %s;
                                     mvn release:perform""" % src_dir
    print project_release_perform_cmd
    (project_release_perform_status, project_release_perform_output) = commands.getstatusoutput(project_release_perform_cmd)

    # always write the maven build output to log file
    writeFile("%s/%s_%s_release_perform.out" % (log_dir, project_name, branch_name), prepare_project_output)

    # rollback the changes made to pom if applicable
    if len(old_values) > 0:
        propsElem = findChildElement(dom.childNodes[0].childNodes, "properties")
        for project_version_key, old_version in old_values.items():
            fixElementValue(propsElem.childNodes, project_version_key, old_version)
        writeFile(pom, dom.toxml())
        # commit the edited pom
        svn_commit(src_dir, svn_credential, "[buildrefapp] finish the build")

    # if the build of project is unsuccessful
    if project_release_perform_status != 0:
        raise RuntimeError, "the problem with mvn release:perform :%s\n" % project_release_perform_output

############################ main #################################
if __name__ == "__main__":
    # getting svn login/pass from user
    svn_credential = None
    svn_user = raw_input("svn username(leave blank=no user/passwd):")
    svm_passwd = ""
    if (svn_user != ""):
        svn_passwd = getpass.getpass("svn passwd:")
        svn_credential = (svn_user, svm_passwd)

    # prepare the layout
    (time_millis, build_dir_base, build_dir, log_dir, tmp_dir) = create_new_build_layout()

    # take the set of projects to be built
    projects = []
    build_another_project = "yes"
    while build_another_project == "yes":
        # take the project name
        project_name = take_project_name(svn_credential)
        # then take branch
        (branch, src_location, version_type) = take_project_params(project_name, svn_credential)
        # build the project base url string
        project_base_url = "%s/%s" % (project_svn_base, project_name)
        # take the output modifier
        (modifier, full_version) = take_next_version_modifier(src_location, project_base_url, version_type, tmp_dir, svn_credential)
        # save all the projects to be built in the list
        projects.append((project_name, branch, src_location, modifier, full_version))
        # do we need to build more?
        build_another_project = take_yes_no("build another project?")

    # take the set of refapp branches to be built
    refapps = []
    build_another_refapp = "yes"
    while build_another_refapp == "yes":
        (branch, src_location, version_type) = take_project_params(refapp_project_name, svn_credential)
        # take the output modifier
        (modifier, full_version) = take_next_version_modifier(src_location, "%s/%s" % (project_svn_base, refapp_project_name), version_type, tmp_dir, svn_credential)
        # save the build configs
        refapps.append((branch, src_location, modifier, full_version))
        # build more?
        build_another_refapp = take_yes_no("build another refapp?")

    print "==========START BUILDING=========="

    mod_versions = {}
    # build all the projects
    for project in projects:
        # build
        build_project(project[0], project[1], build_dir, log_dir, project[2], project[3], svn_credential, {})
        # take the output versions. all the keys have to be lower case here.
        mod_versions["%s.version" % project[0].lower()] = project[4]
        print "%s %s built and deployed" % (project[0], project[4])

    # build all the refapp branches
    for refapp in refapps:
        build_project(refapp_project_name, refapp[0], build_dir, log_dir, refapp[1], refapp[2], svn_credential, mod_versions)
        print "refapp %s built and deployed" % refapp[3]

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