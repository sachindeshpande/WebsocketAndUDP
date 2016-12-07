package hello;

import java.beans.PropertyChangeListener;

public class PackageManager
{
		Thread listeningThread = null;
		EKGPackageServer packageServer = new EKGPackageServer();
		
	   public void startListening() throws Exception
	   {
		   listeningThread = new Thread(packageServer);
		   listeningThread.start();
	   }
	   
       public void addChangeListener(PropertyChangeListener newListener) {
    	   packageServer.addChangeListener(newListener);
   	   }
	   
	   
}