package wx.org.img;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class logServer extends Thread{
	
	String phoneMac;
	String mac;		
	private boolean flag = true;
	byte[] buf = new byte[10240];
	DatagramSocket dSocket = new DatagramSocket(514);
	DatagramPacket dPacket = new DatagramPacket(buf, 10240);
	String reg = "STAMGR_CLIENT_ONLINE:\\s*Client\\s*(\\w{4}-\\w{4}-\\w{4})"; //Patter the phone MAC
	Pattern pattern = Pattern.compile(reg);
	List<String> macList = new ArrayList();
	private BlockingQueue<String> phMACs;
	private Lock macLock;
	private Logger mLogger;
	
	/*
	 * Create the syslog server so it can listening the socket*/
	public logServer(BlockingQueue<String> phMACs, Lock macLock, Logger mLogger) throws UnknownHostException, IOException{
		this.phMACs = phMACs;
		this.macLock = macLock;
		this.mLogger = mLogger;

	}
		
		public void run(){
			System.out.println("[logServer run Info] This syslog Server Thread has start" );
			while(flag){
				try {
					dSocket.receive(dPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
				String sysInfo = new String(dPacket.getData(), 0, dPacket.getLength());
				//System.out.println("[logServer run Info] This syslog Server get the Information from the AC is: " + sysInfo);
				Matcher m = pattern.matcher(sysInfo);
	    		if(m.find()){
	    			phoneMac = m.group(1);
	    			//if(!macList.contains(phoneMac)){
	    			macList.add(phoneMac);
	    			String regMAC = formatMAC(phoneMac);
	    			System.out.println("[logServer run Info] This phone is online and the mac is: " + regMAC);
	    			//System.out.println("[logServer run Debug] I am waiting the lock");
	    			macLock.lock();
	    			//System.out.println("[logServer run Debug] I have acquire the lock");
	    			phMACs.add(regMAC);
	    			macLock.unlock();
	    			//System.out.println("[logServer run Debug] I have release the lock");
	    				    			
	    		}else{
	    			//System.out.println("[logServer run Info] This syslog does not has the MAC information");
	    		}
			}
		}

		/*
		 * This function will format the MAC Address*/
		private String formatMAC(String phoneMac2) {
            StringBuffer sBuffer = new StringBuffer(phoneMac2);
            String reString = "-";
            for(int i=2; i<=16; i+=6){
                sBuffer.insert(i, reString);
            }
         		
		return sBuffer.toString();
		}

	
	
	
}
