package com.sjsu.cmpe283;
import java.net.URL;
import com.vmware.vim25.VAppConfigSpec;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualApp;
import com.vmware.vim25.mo.VirtualMachine;

public class createVapp {

	public static void main(String[] args) {
		String username = "root";
		String password = "devanjal";
		String ip = "172.16.109.137";
		System.out.println("HW3 Extra Credit By Devanjal Kotia ");
		
		ServiceInstance s;
		
		try {
			s = new ServiceInstance(new URL("https://" + ip + "/sdk"),username, password, true);
			InventoryNavigator in = new InventoryNavigator(s.getRootFolder());
			ManagedEntity me = in.searchManagedEntity("VirtualApp", "devanjal_vApp_048");
			System.out.println(me.getName());
			VirtualApp ve=(VirtualApp)me;
			
			VirtualApp vap=new VirtualApp(ve.getServerConnection(),ve.getMOR());
			
			VAppConfigSpec vac=new VAppConfigSpec();
			
			vac.entityConfig=vap.getVAppConfig().entityConfig;
			
			vap.createVApp("devanjal_vApp_048_2", ve.getConfig(),vac, ve.getParentFolder());
			
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("Exception Found!!!!"+e);
			}
		

	}

}
