package wx.org.img;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.logging.Logger;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;


public class getMAC extends Thread{
	
	
	private BlockingQueue<String> phMACs;
	private Lock macLock;
	private Logger mLogger;
	private String pMAC;
	
	private static String userName = "";
	private static String passWord = "";
	private static String url = "";
	private static List<String> headerList = new ArrayList();
	private static String devIP;
	private static String picPath = "c:\\sheep";
	private static File file = new File(picPath);

	public getMAC(BlockingQueue<String> phMACs, Lock macLock, Logger mLogger) {
		this.phMACs = phMACs;
		this.macLock = macLock;
		this.mLogger = mLogger;
	}

	public void run(){
		System.out.println("[getMAC run Debug] This getMAC Thread has start");
		boolean flag = true;
		while(flag ){
			int macNum = phMACs.size(); 
			if(macNum>0){
				//System.out.println("[getMAC run Debug] I am waiting the lock");
				macLock.lock();
				//System.out.println("[getMAC run Debug] I have acquire the lock");
				pMAC = phMACs.remove();
				macLock.unlock();
				System.out.println("[getMAC run Debug] I have release the lock and the phone MAC is: " + pMAC);
				
				/*This gSW will call the getSW class*/
				getSW gSW = new getSW(pMAC);
				try {
					Thread.sleep(2500);//保证senSms()有足够的时间通过MAC_soap协议获取到设备的IP
					devIP = gSW.sendSms();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				HTTPDigestClient hClient = new HTTPDigestClient(userName, passWord);
				HttpPost hPost = new HttpPost(url);
				try {
					String param = "{\"macs\":[\""+pMAC+"\"],\"macType\":1}";
					StringEntity stringEntity = new StringEntity(param);
			        stringEntity.setContentType("application/json");
			        stringEntity.setContentEncoding("UTF-8");
			    	hPost.setEntity(stringEntity);
			    	String hImgURL = hClient.send(hPost);
			    	//System.out.println("[getMAC run Info] This headImgUrl is: " + hImgURL);
			    	if((!hImgURL.isEmpty()) && (!headerList.contains(hImgURL))){
			    		File imgFile = new File(file.getPath()+ "\\" +devIP +".jpg");
				    	if(!imgFile.exists()){
				    		getPageImg(hImgURL, devIP);
					    	headerList.add(hImgURL);
				    	}else{
				    		System.out.println("[getMAC run Warn] This header Image has download before");
				    	}

			    	}else{
			    		System.out.println("[getMAC run Warn] This url is null");
			    	}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
			}else{
				continue;
			}

			}
		}
    /*
     * This function will download the weChat header Image from the Web*/
	private static void getPageImg(String hImgURL, String devIP2) {
		StringBuffer sBuffer = new StringBuffer();
		try{
			URL url = new URL(hImgURL);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(5*1000);
			InputStream iStream = conn.getInputStream();
			byte[] bs = new byte[10240];
			int len;
			
			if(!file.exists()){
				file.mkdirs();
			}
			
			OutputStream oStream = new FileOutputStream(file.getPath() + "\\" + devIP+".jpg");
			while((len = iStream.read(bs)) != -1){
				oStream.write(bs, 0, len);
			}
			oStream.close();
			iStream.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
