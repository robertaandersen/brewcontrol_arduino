import javax.comm.*;
import java.io.*; 
import java.util.*; 


public class ArduinoSerialReader {

	static CommPortIdentifier portId1; 
	static CommPortIdentifier portId2;
	SerialPort serialPort1, serialPort2;
	
	public static void main(String[] args) 
	 {
	        System.out.println("Hello World!"); // Display the string.
	        
	        new ArduinoSerialReader().list(); 
	   
	        /*portId1 = CommPortIdentifier.getPortIdentifier("COM4");
	        portId2 = CommPortIdentifier.getPortIdentifier("COM5");/*/
	        
	 }

	protected void list()
	{
		
		Enumeration pList = CommPortIdentifier.getPortIdentifiers();
		System.out.println(pList.hasMoreElements()); 
		while(pList.hasMoreElements())
		{
			CommPortIdentifier cpi = (CommPortIdentifier)pList.nextElement();
			System.out.println(cpi.getName()); 			
		}
	}
	/*@Override
	public void serialEvent(SerialPortEvent arg0) {
		// TODO Auto-generated method stub
		
	}*/
	

}
