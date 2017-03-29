package wx.org.img;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;


public class wxAccess {

	public static BlockingQueue<String> phMACs = new LinkedBlockingQueue<String>();
	public static Lock macLock = new ReentrantLock();
	public static Logger mLogger = Logger.getAnonymousLogger();
	
	
	
	public static void main(String args[]) throws Exception{
		
		logServer lServer = new logServer(phMACs, macLock, mLogger);
		lServer.start();
		
		Thread.sleep(1000);		
		getMAC gMac = new getMAC(phMACs, macLock, mLogger);
		gMac.start();
		
		lServer.join();
		gMac.join();
		}

}
