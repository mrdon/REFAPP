import sys

# generic configuration part
supported_build_script_version = [0, 1, 1]
ssh.setDebug(1)
ssh.setEndOfLine("\n")

# connect to a remote host
print "Enchanter:Connecting to ${server}"
ssh.connect('${server}', None)
# wait instead of prompt *****************************************************************
ssh.waitFor('${unix_prompt}');
print "Enchanter:Connected"

# check version
ssh.sendLine('python build_refapp.py version');
ssh.waitFor('version', 1)
version = map(lambda x:int(x), ssh.lastLine()[8:].split("."))
print "\ndetected version = %s" % version
if (version != supported_build_script_version):
    print "\nbuildscript version mismatch"
    sys.exit(1)

# run the build by answering the command line questions.
ssh.sendLine('python build_refapp.py');
ssh.waitFor('svn username(leave blank=no user/passwd):')
ssh.sendLine('${svn_user}');
print "Enchanter:svn username entered"

ssh.waitFor("svn passwd:");
ssh.sendLine("${svn_passwd}");
print "Enchanter:svn password entered"

ssh.waitFor('project(blank for listing):');
ssh.sendLine('${project_name}');
print "Enchanter:project name entered"

ssh.waitFor('project branch(blank for listing):');
ssh.sendLine('${project_branch}');
print "Enchanter:project branch entered"

ssh.waitFor('refapp branch(blank for listing):');
ssh.sendLine('${refapp_branch}');
print "Enchanter:refapp branch entered"

ssh.setTimeout(600000);
ssh.waitFor('==========START BUILDING==========');
print "\nEnchanter:start building\n"
ssh.waitFor('==========SUCCESSFULLY BUILT REFAPP==========');
print "\nEnchanter:build done\n"
ssh.disconnect();
sys.exit(0)