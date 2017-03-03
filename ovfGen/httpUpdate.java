package com.sjsu.cmpe283;
import com.vmware.vim25.mo.*;
public class httpUpdate extends Thread
{
    private HttpNfcLease hn = null;
    private int aggregate = 0;
    private int interval;
 
    public httpUpdate(HttpNfcLease httpNfcLease, int updateInterval)
    {
        this.hn = httpNfcLease;
        this.interval = updateInterval;
    }
 
    public void run()
    {
        while (true)
        {
            try
            {
                hn.httpNfcLeaseProgress(aggregate);
                Thread.sleep(interval);
            }
            catch(InterruptedException ie)
            {
                break;
            }
            catch(Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }
 
    public void setPercent(int percent)
    {
        this.aggregate = percent;
    }
}