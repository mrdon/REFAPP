===Build tool (v. 0.1)===
build_refapp.py can be used to automate the build of plugin project and reffapp.

1. It works on default Python >= 2.4.3 package without any additional libraries.   Check your Python version by issuing "python -V" command.
2. Since the tool relies on Maven, common maven settings are required. The sample setting.xml file can be found at https://svn.atlassian.com/svn/private/atlassian/maven2settings/trunk/settings.xml.developer .
3. Run the tool by "python build_refapp.py". The build output will be stored at /tmp/refapp_build_xyz where xyz is the build timestamp. The dir /tmp/refapp_build_xyz/build contains all the projects involved and the dir /tmp/refapp_build_xyz/logs contains all the logs.
4. When running the tool, user has to enter some additional information such as following:-
	project:SAL
	project branch:sal-2.1.x
	refapp branch:refapp-2.5.x
   project = the plugin project which has to match the project name in svn under https://studio.atlassian.com/svn
   project branch = the branch we want to build
   refapp branch = the branch of refapp that we want the new project's jar to be included

===Known issues===
1. There is a bug with svn code in maven which from time to time result in something such as:-

[ERROR] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Unable to tag SCM
Provider message:
The svn tag command failed.

To the best that I know, this occurs in certain versions of svn versions. More details will be included soon!
