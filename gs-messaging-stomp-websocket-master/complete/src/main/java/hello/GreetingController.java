package hello;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController implements PropertyChangeListener {

	@Autowired
    private SimpMessagingTemplate template;

	PackageManager server = new PackageManager();

	public GreetingController()
	{
        try {
			server.startListening();
			server.addChangeListener(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {

    	Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + message.getName() + "!");
    }
    
    @Override
   
    public void propertyChange(PropertyChangeEvent event) {
            System.out.println("Property: " + event.getNewValue());
            try {
//				greeting(new HelloMessage(event.getNewValue().toString()));
            	processEvent(event.getNewValue().toString());
            	//this.template.convertAndSend("/topic/greetings", new Greeting("New value is " + event.getNewValue()));
            	new WebSocketMessageBroker(this.template, event.getNewValue().toString()).broadcastMessage();;
            } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
    }
    
    private String processEvent(String eventMsg)
    {
        String[] splitString = (eventMsg.split("#BPM:"));
        String noteStr = splitString[1].replaceAll("\r\n", "");
        double noteD = Double.parseDouble(noteStr);
        int note =  (int)Math.floor(noteD);
        processMidi(note);
        return null;
        

    }
    
    private void processMidi(int note)
    {
		ShortMessage myMsg = new ShortMessage();
		  // Start playing the note Middle C (60), 
		  // moderately loud (velocity = 93).
		  try {
			  if(note > 120)
				  note = 120;
			myMsg.setMessage(ShortMessage.NOTE_ON, 0, note, 90);
			
			long timeStamp = -1;
			Receiver       rcvr = MidiSystem.getReceiver();
			rcvr.send(myMsg, timeStamp);	
			
			playSound(rcvr,2, 60, 90);
			
		  
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	
    }
    
	private void playSound(Receiver       rcvr,int channel, int data1 , int data2) throws Exception
	{
		Thread.sleep(10);

		long timeStamp = -1;
		ShortMessage myMsg = new ShortMessage();
		myMsg.setMessage(ShortMessage.NOTE_ON, channel, data1 , data2);		

		rcvr.send(myMsg, timeStamp);	
		
	}    

}
