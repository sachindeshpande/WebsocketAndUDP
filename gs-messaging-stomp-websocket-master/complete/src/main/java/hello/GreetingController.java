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
    	System.out.println("Message received : " + message.getName());
    	Thread.sleep(1000); // simulated delay
    	double tone = Double.parseDouble(message.getName());
    	processMidi((int)Math.floor(tone + 60));
    	
        return new Greeting("Hello, " + message.getName() + "!");
    }
    
    //The routing below works. I was not able to get the data structure mapping correctly
    @MessageMapping("/bpmpacket")   
    public void sendBPMPacket(BPMPacket packet) throws Exception
    {
    	System.out.println("packet received : " + packet);
    }
    
    @Override
   
    public void propertyChange(PropertyChangeEvent event) {
            System.out.println("Property: " + event.getNewValue());
            try {
//				greeting(new HelloMessage(event.getNewValue().toString()));
            	int note = processEvent(event.getNewValue().toString());
                processMidi(note);
            	//this.template.convertAndSend("/topic/greetings", new Greeting("New value is " + event.getNewValue()));
            	new WebSocketMessageBroker(this.template, (new Integer(note).toString())).broadcastMessage();;
            } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
    }
    
    private int processEvent(String eventMsg)
    {
        String[] splitString = (eventMsg.split("#BPM:"));
        String noteStr = splitString[1].replaceAll("\r\n", "");
        double noteD = Double.parseDouble(noteStr);
        int note =  (int)Math.floor(noteD);
        return note;
        
    }
    
    private void processMidi(int note)
    {
		ShortMessage myMsg = new ShortMessage();
		  // Start playing the note Middle C (60), 
		  // moderately loud (velocity = 93).
		  try {
			  note = note - 40;
			  if(note > 130)
				  note = 130;
			  if(note < 40)
				  note = 40;
			  
			System.out.println("note = " + note);
			//myMsg.setMessage(ShortMessage.NOTE_ON, 0, note, 90);
			
			long timeStamp = -1;
			Receiver       rcvr = MidiSystem.getReceiver();
			//rcvr.send(myMsg, timeStamp);	
			
			playSound(rcvr,2, note, note);
			
		  
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	
    }
    
	private void playSound(Receiver       rcvr,int channel, int data1 , int data2) throws Exception
	{
		Thread.sleep(10);
		System.out.println("tone = " + data1);
		long timeStamp = -1;
		ShortMessage myMsg = new ShortMessage();
		myMsg.setMessage(ShortMessage.NOTE_ON, channel, data1 , data2);		

		rcvr.send(myMsg, timeStamp);	
		
	}    

}
