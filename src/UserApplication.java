
import java.io.*;
import java.util.*;

public class UserApplication {
	//String pathname = path\\where\image\is\to\be\saved;
	List<Byte> array = new ArrayList<Byte>();
	
	
		
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//remove the line-comments and add the code you like to test to ithaki modem
		
		//(new UserApplication()).echo_request_code("E8718\r");
		//(new UserApplication()).image_request_code("G9982\r");
		//(new UserApplication()).gps_request_code("P2955");
		//(new UserApplication()).arq_request_code("Q8539\r","R6143\r");
	}
	
	
	public void gps_request_code(String code){
		int k;
		Modem modem;
		modem= new Modem();
		modem.setSpeed(80000);
		modem.setTimeout(2000);
		String R = "R=1101099\r";
		StringBuilder result;
		List<String> strings = new ArrayList<String>();
		List<Character> chars = new ArrayList<Character>();
		List<Double> times = new ArrayList<Double>();
		List<String> geoWidth = new ArrayList<String>();
		List<String> geoLength = new ArrayList<String>();
		
		FileOutputStream fos=null;
		try {
			//TODO add path where right transfered image is to be stored
			fos = new FileOutputStream("add path here");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//modem in command mode
		
		modem.write("atd2310ithaki\r".getBytes());
		
		//modem in data mode
		
		for(;;){
			
			try{
				k=modem.read();
				
				if(k==-1){
					break;
				}
				System.out.print((char)k);
				
			}catch(Exception x){
				break;
			}
				
		}
		
		
		
		modem.write((code+R).getBytes());
		for(;;){
			
			try{
				k=modem.read();
				chars.add((char)k);
				if(k==-1){
					System.out.println("I'm out!");
					break;
				}
				System.out.print((char)k);
				
				
			}catch(Exception x){
				break;
			}
				
		}
		
		
		result = new StringBuilder(chars.size());
		for(Character c : chars){
			result.append(c);
		}
		
		String output = result.toString();
		@SuppressWarnings("resource")
		Scanner scn = new Scanner(output);
		scn.useDelimiter("\r\n");
		while(scn.hasNext()){
			strings.add(scn.next());
		}
		
		
		
		System.out.println("done");
		
		
		for(int j=0;j<strings.size();j++){
			List<String> values = new ArrayList<String>();
			@SuppressWarnings("resource")
			Scanner tempScan = new Scanner(strings.get(j));
			tempScan.useDelimiter(",");
			while(tempScan.hasNext()){
				
				values.add(tempScan.next());
				
			}
			
			if(values.size()>2){
				times.add(Double.parseDouble(values.get(1)));
				geoWidth.add(values.get(2));
				geoLength.add(values.get(4));
			}
			
			
			
		}
		int startTime=0;
		int counter=0;

		String Tstring=code;
		for(int j=0;j<times.size();j++){
			
			if(times.get(j)-times.get(startTime)>4){
				System.out.println("I'm in "+j);
				
				startTime=j;
				counter++;
				if(counter>9){
					System.out.println("Done with T");
					break;
				}
				if(counter>=0){
					Tstring=Tstring+"T=";
				}
				String degrees;
				
				degrees=makeTransformation(geoLength.get(j),true);
				
				Tstring=Tstring+degrees;
				
				degrees=makeTransformation(geoWidth.get(j),false);
				
				Tstring=Tstring+degrees;
				
				
			}
		}
		
		System.out.println(Tstring);
		
		modem.write((Tstring+"\r").getBytes());
		
		
		for(;;){
			try{
				k=modem.read();
				
				if(k==-1){
					System.out.println("Done!");
					break;
				}
				fos.write((byte)k);
				
			}catch(Exception e){
				System.out.println("exception");
				break;
			}
		}
		
		//System.out.println(times.size());
		
		try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		chars.clear();
		modem.close();
	}
	
	public void image_request_code(String code){
		//initializing variables
		int k;
		Modem modem;
		modem= new Modem();
		modem.setSpeed(80000);
		modem.setTimeout(2000);
		
		//modem in command mode
		
		modem.write("atd2310ithaki\r".getBytes());
		
		//modem in data mode
		
		for(;;){
			
			try{
				k=modem.read();
				if(k==-1){
					break;
				}
				System.out.print((char)k);
				
			}catch(Exception x){
				break;
			}
				
		}
		
		
		
		modem.write(code.getBytes());
		
		try {
			//TODO add path where faulty image is to be stored
			FileOutputStream fos = new FileOutputStream("add path here");
			for(;;){
				
				try{
					k=modem.read();
					
					fos.write((byte)k);
					
					if(k==-1){
						System.out.println();
						break;
					}
					
					
				}catch(Exception x){
					break;
				}
					
			}
			
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Done");
		
		
		
		modem.close();
	}
	
	public void echo_request_code(String code){
		
		int k;
		Modem modem;
		modem= new Modem();
		modem.setSpeed(80000);
		modem.setTimeout(2000);
		FileOutputStream fos = null;
		
		
		try {
			//TODO add path to where measurements.txt is to be stored
			fos = new FileOutputStream("your path here");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		long start,end,point,dur;
		
		//modem in command mode
		
		modem.write("atd2310ithaki\r".getBytes());
		
		//modem in data mode
		
		for(;;){
			
			try{
				k=modem.read();
				if(k==-1){
					break;
				}
				System.out.print((char)k);
				
			}catch(Exception x){
				break;
			}
				
		}

		start = System.currentTimeMillis();
		
		end = System.currentTimeMillis();
		
		
		while(((end-start)/60000)<4){
			point = System.currentTimeMillis();
			
			modem.write(code.getBytes());
			
			for(;;){
				try{
					k=modem.read();
					
					if(k==-1){
						break;
					}
				}catch(Exception x){
					break;
				}
			}
			
			end = System.currentTimeMillis();
			//difference between request and receive 
			dur=end-point;
			try {
				
				fos.write((Long.toString(dur)).getBytes());
				fos.write(System.getProperty("line.separator").getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(dur);
		}
		try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Done");
	}
	
	public String makeTransformation(String s,boolean isItFirst){
		String wholeThing;
		String decimal="";
		char[] degrees = new char[s.length()];
		degrees=s.toCharArray();
		for(Character c:degrees){
			System.out.print(c);
			
		}
		
		if(isItFirst){
			for(int i=6;i<degrees.length;i++){
				decimal=decimal+degrees[i];
			}
		}else{
			for(int i=5;i<degrees.length;i++){
				decimal=decimal+degrees[i];
			}
		}
		int seconds;
		seconds = Integer.parseInt(decimal);
		seconds=seconds*60;
		seconds=seconds/10000;
		
		if(seconds==33){
			seconds++;		//gps is bugged for 33 secs :p
		}
		System.out.println(seconds);
		if(isItFirst){
			wholeThing=String.valueOf(degrees[1])+String.valueOf(degrees[2])+String.valueOf(degrees[3])+String.valueOf(degrees[4])+Integer.toString(seconds);
			System.out.println(wholeThing);
		}else{
			wholeThing=String.valueOf(degrees[0])+String.valueOf(degrees[1])+String.valueOf(degrees[2])+String.valueOf(degrees[3])+Integer.toString(seconds);
			System.out.println(wholeThing);
		}
		
		return wholeThing;
		
	}
	
	public void arq_request_code(String ackCode, String nackCode){
		int k;
		String code;
		Modem modem;
		modem= new Modem();
		modem.setSpeed(80000);
		modem.setTimeout(2000);
		List<Character> chars = new ArrayList<Character>();
		boolean isNextTrue=true;
		int errorCounter=0;
		long packetStart=0;
		long packetEnd;
		long packetDuration;
		int packetID=0;
		
		FileOutputStream fos=null;
		try {
			//TODO add path to FileOutputSource
			fos = new FileOutputStream("add path here");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//modem in command mode
		
		modem.write("atd2310ithaki\r".getBytes());
		
		//modem in data mode
		
		for(;;){
			
			try{
				k=modem.read();
				
				if(k==-1){
					break;
				}
				System.out.print((char)k);
				
			}catch(Exception x){
				break;
			}
				
		}
		
		long start1 =  System.currentTimeMillis();
		long end1= System.currentTimeMillis();
		
		try {
			fos.write("PID".getBytes());
			fos.write((byte)'\t');
			fos.write("Dur".getBytes());
			fos.write((byte)'\t');
			fos.write("Errors".getBytes());
			fos.write(System.getProperty("line.separator").getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		code=ackCode;
		
		for(;;){
			
			if(isNextTrue==true){
				errorCounter=0;
				packetStart=System.currentTimeMillis();
			}
			else{
				errorCounter++;
			}
						
			modem.write(code.getBytes());
			for(;;){
				
				try{
					k=modem.read();
					chars.add((char)k);
					if(k==-1){
						break;
					}
					System.out.print((char)k);
					
				}catch(Exception x){
					break;
				}
					
			}
			System.out.println(chars.size());
			if(checkARQ(chars)==true){
				code=ackCode;
				packetEnd=System.currentTimeMillis();
				packetDuration=packetEnd-packetStart;
				packetID++;
				isNextTrue=true;
				try {
					fos.write((Integer.toString(packetID)).getBytes());
					fos.write((byte)'\t');
					fos.write((Long.toString(packetDuration)).getBytes());
					fos.write((byte)'\t');
					fos.write((Integer.toString(errorCounter)).getBytes());
					fos.write(System.getProperty("line.separator").getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else{
				isNextTrue=false;
				code=nackCode;
			}
		    end1= System.currentTimeMillis();
		    long duration= end1-start1;
		    
		    chars.clear(); 
		    
		    if  (duration>=240000){
		    	break;
		    }
		}
		
		try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("\n\nDone");
		
		chars.clear();
		modem.close();
	}
	
	public boolean checkARQ(List<Character> chars){
		StringBuilder result;
		char[] packetContent = new char[16];
		char[] packetFCS = new char[3];
		
		int j=0;
		for(int i=31; i<47; i++){
			packetContent[j]=chars.get(i);
			
			j++;			
		}
		
		
		
		j=0;
		for(int i=49;i<52;i++){
			packetFCS[j]=chars.get(i);
			
			j++;
		}
		
		System.out.print("\n");
		
		result = new StringBuilder(3);
		for(Character c : packetFCS){
			result.append(c);
		}
		
		String output = result.toString();
		Integer fcs = Integer.parseInt(output);
		
		Integer xorResult;
	 
	    xorResult = (packetContent[0] ^ packetContent[1]);
	    for(int i=2 ; i<16 ; i++)
	    {
	    	xorResult ^= packetContent[i];
	    }
	    System.out.println(xorResult);
		
	    if(xorResult==fcs){
	    	System.out.println("ACK");
	    	return true;
	    }
	    else{
	    	System.out.println("NACK");
	    	return false;
	    }
	}

}
