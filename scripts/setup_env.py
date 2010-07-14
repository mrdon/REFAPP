import os
import commands, getpass

# locations for $HOME/.m2/settings.html file
maven_m2_location = "%s/.m2" % os.getenv("HOME")
maven_settings_location = "%s/settings.xml" % maven_m2_location

# check for maven configuration
if (not os.path.isdir(maven_m2_location)) or (not os.path.isfile(maven_settings_location)):
    choice = raw_input("maven2 settings.xml not found. create one? [yes/no]")
    if choice == "yes":
        # get user/passwd to be put in setting.xml
        crowd_user = raw_input("enter your crowd username:")
        crowd_passwd = getpass.getpass("enter your crowd password:")
        # create the .m2 directory if none exists
        if not os.path.isdir(maven_m2_location):
            os.mkdir(maven_m2_location)
        # create the settings file
        setting_wget_cmd = """cd %s;
                              wget https://svn.atlassian.com/svn/private/atlassian/maven2settings/trunk/settings.xml.developer --http-user="%s" --http-password="%s" """ % (maven_m2_location, crowd_user, crowd_passwd)
        (status, msg) = commands.getstatusoutput(setting_wget_cmd)
        if status != 0:
            raise RuntimeError, "trouble while trying to get the setttings.xml.developer file: %s" % msg
        sed_replace_cmd =  """cd %s;
                              sed "s/<username>yourusername<\/username>/<username>%s<\/username>/g" setttings.xml.developer | sed "s/<password>yourpassword<\/password>/<password>%s<\/password>/g" > settings.xml""" % (crowd_user.replace("/", "\/"), crowd_passwd.replace("/", "\/"))
        (status, msg) = commands.getstatusoutput(sed_replace_cmd)
        if status != 0:
            raise RuntimeError, "trouble while trying to generate the $HOME/.m2/settings.xml file: %s" % msg
    if not os.path.isfile("build_refapp.py"):
        choice = raw_input("the refapp build script doesn't exist. download the latest one? [yes/no]")
        if choice == "yes":
            script_wget_cmd = """wget https://studio.atlassian.com/svn/REFAPP/trunk/build_refapp.py --http-user="%s" --http-password="%s" """ % (crowd_user, crowd_passwd)
            (status, msg) = commands.getstatusoutput(script_wget_cmd)
            if status != 0:
                raise RuntimeError, "trouble while trying to download build_refapp.py file: %s" % msg
    else:
        choice = raw_input("the refapp build script might not be the latest. download the latest one? [yes/no]")
        script_wget_cmd = """wget https://studio.atlassian.com/svn/REFAPP/trunk/build_refapp.py --http-user="%s" --http-password="%s" -q -O build_refapp.py """ % (crowd_user, crowd_passwd)
        (status, msg) = commands.getstatusoutput(script_wget_cmd)
        if status != 0:
            raise RuntimeError, "trouble while trying to download build_refapp.py file: %s" % msg
# good exit
print "========== SETUP DONE =========="
sys.exit(0)