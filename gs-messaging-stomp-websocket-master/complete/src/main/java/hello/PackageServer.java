package hello;


import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.beans.*;

public class PackageServer implements Runnable
{
	private List<PropertyChangeListener> listener = new ArrayList<PropertyChangeListener>();
	
	   public void run()
	      {
		     
	         DatagramSocket serverSocket;
			try {
				serverSocket = new DatagramSocket(9876);
	            byte[] receiveData = new byte[1024];
	            byte[] sendData = new byte[1024];
	            while(true)
	               {
	            	System.out.println("Started Server thread");
	            		
	                  DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	                  serverSocket.receive(receivePacket);
	                  String sentence = new String( receivePacket.getData());
	                  System.out.println("RECEIVED: " + sentence);
	                  InetAddress IPAddress = receivePacket.getAddress();
	                  int port = receivePacket.getPort();
	                  String capitalizedSentence = sentence.toUpperCase();
	                  sendData = capitalizedSentence.getBytes();
	                  DatagramPacket sendPacket =
	                  new DatagramPacket(sendData, sendData.length, IPAddress, port);
	                  serverSocket.send(sendPacket);
	                  String dataString = new String(receivePacket.getData());
                      notifyListeners(
                              this,
                              null,
                              dataString,
                              dataString);	                  
	               }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	            
	      }
	   
	   //TODO : Use Queue mechanism
       private void notifyListeners(Object object, String property, String oldValue, String newValue) {
           for (PropertyChangeListener name : listener) {
                   name.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
           }
   }

       public void addChangeListener(PropertyChangeListener newListener) {
           listener.add(newListener);
   }	   
	   
	   
}
