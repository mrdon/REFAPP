ssh.setDebug(1)
ssh.setEndOfLine("\n")

print "Enchanter:Connecting to ${server}"

ssh.connect('${server}', None)
ssh.waitFor('${unix_prompt}');
print "Enchanter:Connected"

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