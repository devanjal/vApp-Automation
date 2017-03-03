package com.sjsu.cmpe283;
import java.io.*;
import java.net.URL;
import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;
import javax.net.ssl.*;
 
public class ovfGen
{
	public static httpUpdate leaseProgUpdater;
 
     public static void main(String[] args) throws Exception
      {
    	 System.out.println("HW3 Extra Credit 2 By Devanjal Kotia ");
    	 String ip="130.65.159.14", username="cmpe283_sec3_student@vsphere.local", password="cmpe-W6ik";
    	 ServiceInstance s = new ServiceInstance(new URL("https://" + ip + "/sdk"), username, password, true);
 
        String vrapp = "devanjal_vApp_048", host_ip = "130.65.159.11", entityType = "VirtualApp", dest_Dir = "/Users/devanjal/Downloads/extracredit/";
        HostSystem h = (HostSystem) s.getSearchIndex().findByIp(null, host_ip, false);
        System.out.println("Host Name : " + h.getName());
        System.out.println("Network : " + h.getNetworks()[0].getName());
        System.out.println("Datastore : " + h.getDatastores()[0].getName());
 
        InventoryNavigator in = new InventoryNavigator(s.getRootFolder());
 
        HttpNfcLease hnl = null;
 
        ManagedEntity m = null;
        if (entityType.equals("VirtualApp"))
        {
          m = in.searchManagedEntity("VirtualApp", vrapp);
          hnl = ((VirtualApp)m).exportVApp();
        }
        else
        {
          m = in.searchManagedEntity("VirtualMachine", vrapp);
          hnl = ((VirtualMachine)m).exportVm();
        }
        HttpNfcLeaseState hs;
        for(;;)
        {
          hs = hnl.getState();
          if(hs == HttpNfcLeaseState.ready)
          {
            break;
          }
          if(hs == HttpNfcLeaseState.error)
          {
            s.getServerConnection().logout();
            return;
          }
        }
        HttpNfcLeaseInfo httpNfcLeaseInfo = hnl.getInfo();
        httpNfcLeaseInfo.setLeaseTimeout(300*1000*1000);
       
        long disk_size = (httpNfcLeaseInfo.getTotalDiskCapacityInKB()) * 1024;
 
        leaseProgUpdater = new httpUpdate(hnl, 5000);
        leaseProgUpdater.start();
 
        long dwnld_file = 0;
        HttpNfcLeaseDeviceUrl[] dev_urls = httpNfcLeaseInfo.getDeviceUrl();
        if (dev_urls != null)
        {
          OvfFile[] ovf = new OvfFile[dev_urls.length];
          System.out.println("Downloading Files:");
          for (int i = 0; i < dev_urls.length; i++)
          {
            String did = dev_urls[i].getKey();
            String devurl_str = dev_urls[i].getUrl();
            String disk_name = devurl_str.substring(devurl_str.lastIndexOf("/") + 1);
            String disk_url = devurl_str.replace("*", host_ip);
            String disk_path = dest_Dir + disk_name;
            System.out.println("file_name: " + disk_name);
            System.out.println("VMDK's url: " + disk_url);
            String k = s.getServerConnection().getVimService().getWsc().getCookie();
            long disk_file_size = writeVMDKFile(disk_path, disk_url, k, dwnld_file, disk_size);
            dwnld_file += disk_file_size;
            OvfFile ovf_file = new OvfFile();
            ovf_file.setPath(disk_name);
            ovf_file.setDeviceId(did);
            ovf_file.setSize(disk_file_size);
            ovf[i] = ovf_file;
          }
 
          OvfCreateDescriptorParams ovf_desc = new OvfCreateDescriptorParams();
          ovf_desc.setOvfFiles(ovf);
          OvfCreateDescriptorResult ovf_out =
            s.getOvfManager().createDescriptor(m, ovf_desc);
 
          String ovf_path = dest_Dir + vrapp + ".ovf";
          FileWriter outp = new FileWriter(ovf_path);
          outp.write(ovf_out.getOvfDescriptor());
          outp.close();
          System.out.println("Successfull Download !!!! Expoted OVF to : " + ovf_path);
        }
 
        leaseProgUpdater.interrupt();
        hnl.httpNfcLeaseProgress(100);
        hnl.httpNfcLeaseComplete();
 
        s.getServerConnection().logout();
      }
    private static long writeVMDKFile(String local_path, String disk_url, String c,
            long dwnld_size, long total_size) throws IOException
    {
        HttpsURLConnection con = getHTTPConnection(disk_url, c);
        InputStream input = con.getInputStream();
        OutputStream output = new FileOutputStream(new File(local_path));
        byte[] b = new byte[102400];
        int size = 0;
        long downloaded_bytes = 0;
        while ((size = input.read(b)) > 0)
        {
            output.write(b, 0, size);
            downloaded_bytes += size;
            int agg = (int)(((dwnld_size + downloaded_bytes) * 100) / total_size);
          leaseProgUpdater.setPercent(agg);
            System.out.println("downloaded: " + downloaded_bytes/1024+ "kb");
        }
        input.close();
        output.close();
        return downloaded_bytes;
    }
 
    private static HttpsURLConnection getHTTPConnection(String url_string, String cookie_string) throws IOException
    {
        HostnameVerifier hsn = new HostnameVerifier()
        {
            public boolean verify(String url_hname, SSLSession session)
            {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hsn);
        URL url = new URL(url_string);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
 
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setAllowUserInteraction(true);
        con.setRequestProperty("Cookie",   cookie_string);
        con.connect();
        return con;
    }
 
}
