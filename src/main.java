
import java.net.*;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFileFormat.Type;

public class main {

	public static void main(String[] args) throws IOException {
		//Parameters
		int clientLP = 48031; //my PC's port
		int serverLP = 38031; //ithaki
		String Echo = "E9094";
		String Image = "M5055";
		String Sound = "V4023";

		//UDP Socket object
		DatagramSocket s = new DatagramSocket(serverLP); //send
		DatagramSocket r = new DatagramSocket(clientLP); //receive
		r.setSoTimeout(5000); //5000ms timeout
		byte[] ithakiIP = { (byte)155, (byte)207, (byte) 18, (byte) 208}; //155.207.18.208
		InetAddress ithaki = InetAddress.getByAddress(ithakiIP);
		byte[] clientIP ={ (byte)94, (byte)69, (byte)25, (byte)194};//94.69.25.194 
		InetAddress client = InetAddress.getByAddress(clientIP);
		
		//Set up TCP for IthakiCopter
		Socket s1 = new Socket(ithaki,38048);
		//Send HTTP request
		String sentence="GET /index.html HTTP/1.0\r\n\r\n";
		String modifiedSentence;
		//Set up Input/Output Streams
		BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
		DataOutputStream outToServer = new DataOutputStream(s1.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(s1.getInputStream()));
		outToServer.writeBytes(sentence);
		long startTime = System.currentTimeMillis();
		//get HTTP answers for 1 minute (60 seconds x 1000 milliseconds)
		while ((System.currentTimeMillis()-startTime)< 60*1000){
			  modifiedSentence = inFromServer.readLine();
			  System.out.println("FROM SERVER: " + modifiedSentence);		  
		}
		s1.close();//close the socket for the TCP comms
		
		String content;
		byte[] txBuffer;
		DatagramPacket p;

		//set UDP Packet content to echo request
		content = Echo;
		txBuffer = content.getBytes();
		p = new DatagramPacket(txBuffer, txBuffer.length, ithaki, serverLP);
		GetStrings(s, p, r);
		
		//set UDP Packet content to sound request
		boolean AQ = false;
		content = Sound;
		String soundsrc = AQ?"AQF":"T"; //T = sinewave, F = song, AQ or not
		int numPackets = 200; //number of packets requested
		content = content + soundsrc + numPackets;
		txBuffer = content.getBytes();
		p = new DatagramPacket(txBuffer, txBuffer.length, ithaki, serverLP);
		System.out.println(content);
		GetSound(s, p, r, numPackets,AQ);
		
		//set UDP Packet content to image request
		content = Image;
		txBuffer = content.getBytes();
		p = new DatagramPacket(txBuffer, txBuffer.length, ithaki, serverLP);
		GetImage(s, p, r);
	}

	private static void GetSound(DatagramSocket send, DatagramPacket p, DatagramSocket receive, int numPackets,boolean AQ)
			throws IOException {
		
		byte[] rxBuffer = new byte[2048];
		DatagramPacket q = new DatagramPacket(rxBuffer, rxBuffer.length);
		ArrayList<Byte> sound = new ArrayList<Byte>();
		int timeouts = 0;
		
		send.send(p);
		while(true){
			try {
				receive.receive(q);
				byte [] temp = AQ?decodeAQDPCM(rxBuffer,132):decodeDPCM(rxBuffer, 128);
				int L = temp.length;
				for (int i = 0; i < L; i++){
					sound.add(temp[i]);
				}

			} catch (IOException e) {
				System.out.println(e);
				timeouts++;
				if (timeouts >= 4){
					//connection probably died or sound ended and we didn't catch it
					break;
				}
			}
		}
		if (sound.size() > 0){
			byte[] temp = new byte[sound.size()];
			for (int i = 0; i < temp.length; i++){
				temp[i] = (byte) sound.get(i);
			}

			InputStream b_in = new ByteArrayInputStream(temp);
			DataOutputStream dos = new DataOutputStream(new FileOutputStream("filename.bin"));
			dos.write(temp);
			//q=16 if sinewave, q=8 if song
			AudioFormat format = new AudioFormat(8000f, 16, 1, true, false);
			AudioInputStream stream = new AudioInputStream(b_in, format, temp.length);
			File file = new File("sinewave.wav");
			AudioSystem.write(stream,  Type.WAVE, file);
			System.out.println("Saved file");
			
		}
	}

	private static byte[] decodeDPCM(byte[] sound, int L) {
		
		int b = 1;
		int m = 0;
		byte[] result = new byte[2*L];
		result[0]=0;
		result[1] = 1;
		
		for(int i = 0; i < L; i++){
			int low_nibble = (0b00001111 & sound[i]);
			int high_nibble= ((0b11110000 & sound[i])>>4);
			//System.out.println(sound[i]); //times deigmatwn
			
			result[2*i] = (byte) (result[2*i+1] + (low_nibble-8)*b);
			result[2*i+1] = (byte) (result[2*i] + (high_nibble-8)*b);
			//System.out.println(result[2*i]); //times diaforwn
			//System.out.println(result[2*i+1]); // -//-
		}
		
	
		return result;
		
			
	}
	
	private static byte[] decodeAQDPCM(byte[] sound, int L)
	{
		
		
		int b = (256*sound[3]&0x0000FF00)+(sound[2]&0x000000FF);//step
	    int m = (256*sound[1]&0x0000FF00)+(sound[0]&0x000000FF);//mean
//		System.out.println("b=" + b);
//		System.out.println("m=" + m);
		
		
		byte[] decoded = new byte[4*L];
		//start off with mean value
		decoded[0]=(byte)m;
		decoded[1]=(byte)m;
		decoded[2]=(byte)m;
		decoded[3]=(byte)m;
		
		for (int i = 0; i < L; i++){
			int low = sound[i+4]&0b00001111 ; //D{i+1}
			int high = (( (sound[i+4]>>4) & 0b1111));//Di
			System.out.println("s=" + sound[i]);
			low = low -8;
			high = high -8;
			low = low*b;
			high = high*b ;
			
			decoded[4*i + 0] = (byte) ((high )&0b00001111);	
			decoded[4*i + 1] = (byte) (((high)>>8)&0xFF);
			decoded[4*i + 2] = (byte) ((low)&0b00001111);
			decoded[4*i + 3] = (byte) (((low)>>8)&0xFF);
			
//			System.out.println("d="+decoded[4*i + 0]);
//			System.out.println("d="+decoded[4*i + 1]);
//			System.out.println("d="+decoded[4*i + 2]);
//			System.out.println("d="+decoded[4*i + 3]);

		}
		
		return decoded;
	}

	/*
	 * Method that sends requests and waits for image responses
	 */
	private static void GetImage(DatagramSocket send, DatagramPacket p, DatagramSocket receive)
			throws IOException {
		byte[] rxBuffer = new byte[2048]; //packet size up to 2 KB
		DatagramPacket q = new DatagramPacket(rxBuffer, rxBuffer.length);

		ArrayList<Byte> image = new ArrayList<Byte>();
		int packetlength = 0;
		int timeouts = 0;
		
		send.send(p);
		while(true){
			try {
				receive.receive(q);
				byte [] temp = rxBuffer;
				int L = q.getLength();
				if (packetlength == 0){
					packetlength = L; //first packet received
				}else if (packetlength != L){
					break; //first packet different from all other packets is the last packet
				}
				//add received bytes to image
				for (int i = 0; i < L; i++){
					image.add(temp[i]);
				}

			} catch (IOException e) {
				System.out.println(e);
				timeouts++;
				if (timeouts >= 10){
					//connection probably died or image ended and we didn't catch it
					break;
				}
			}
		}
		byte[] temp = new byte[image.size()];
		for (int i = 0; i < temp.length; i++){
			temp[i] = (byte) image.get(i);
		}
		
		//dynamic Image name generations
		String filename = "Image";
		filename = filename + System.currentTimeMillis();
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(temp));
		File save = new File(filename);
		save.createNewFile();
		ImageIO.write(img, "jpg", save);
		System.out.println("Received a shitload of bytes with great success");
		
		
		
	}

	/*
	 * Method that sends requests and waits for string responses
	 */
	public static void GetStrings(DatagramSocket send, DatagramPacket p, DatagramSocket receive) throws IOException{
		
		byte[] rxBuffer = new byte[2048]; //up to 2KB
		DatagramPacket q = new DatagramPacket(rxBuffer, rxBuffer.length); 

		ArrayList<Long> responseTimes = new ArrayList<Long>();
		int running_time = 6*60*1000;
		int granularity = 8;
		double []throughput = new double[running_time/(1000*granularity)+1];
		Long teststart = System.currentTimeMillis();
		while(true){
			Long tstart = System.currentTimeMillis();
			send.send(p);
			while(true){
				try{
					receive.receive(q);
					String message = new String(rxBuffer, 0, q.getLength());
					Long tstop = System.currentTimeMillis();
					throughput[(int) ((tstop - teststart)/(1000*granularity))]+=1;
					
					System.out.println(message);
					responseTimes.add(tstop-tstart);
					break;
				} catch (Exception e) {
					System.out.println(e);
					break;
				}
			}
			Long teststop = System.currentTimeMillis();
			if (teststop - teststart > running_time){ //test conducted for 4 minutes :)
				break;
			}
		}
		System.out.println("=========== End Of Transmission ===========");
		System.out.println("Packets Received:\n p="+responseTimes.toString());
		System.out.println("Number of Packets: "+responseTimes.size());
		for (int i = 0; i < throughput.length; i++){
			System.out.println(throughput[i]);
		}
		
	}
}
