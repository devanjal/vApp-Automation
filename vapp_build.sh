p="/opt/vmware/var/lib/build"; 

if [ "$1" == "vapp" ] || [ "$1" == "VAPP" ]; then
    entirePath=$p/vapp_profiles/$2;
else
	entirePath=$p/profiles/$2;
fi
/bin/bash /etc/environment;/opt/vmware/bin/studiocli --createbuild --verbose --profile $entirePath;