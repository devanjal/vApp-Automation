# vApp-Automation

• Developed a Java based application for composing vApp and .OVF generation leveraging VMware studio.
• Interactively imported vApp on vCenter Server and performed power on/off, shutdown and recent task retrieval.


vapp_build script takes two Parameters : i) vapp
					ii) Name of the vApp Profile with .xml format

If this script is on our local machine then,
command: ssh user_name_studiocli@remote_ip ‘bash -s’ < vapp_build.sh param1 param2

For e.g.:ssh root@172.16.109.137 'bash -s' < vapp_build.sh vapp Devanjal_vApp_048.xml 

If this app is on studiocli then, 

./vapp_build.sh vapp Devanjal_vApp_048.xml  