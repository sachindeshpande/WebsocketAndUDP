package hello;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
            	this.template.convertAndSend("/topic/greetings", new Greeting("New value is " + event.getNewValue()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
    }    

}
