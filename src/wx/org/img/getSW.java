package wx.org.img;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class getSW {
	
	private String reqMAC;

	public getSW(String reqMAC) {
		this.reqMAC = reqMAC;
	}

	/*This function will Authenticated in the server*/
	public String sendSms() throws Exception {
		
		String webUrl = "http://172.16.0.1/soap/netconf/";
		String mxml = getMAC.class.getClassLoader().getResource("Author.xml").getFile();
		String soapActionString = "http://172.16.0.1/";
		
		URL muUrl = new URL(webUrl);
		HttpURLConnection hConnection = (HttpURLConnection) muUrl.openConnection();
		File mfile = new File(mxml);
		byte[] buf = new byte[(int) mfile.length()];
		new FileInputStream(mxml).read(buf);
		
		hConnection.setRequestProperty("Content-Length", String.valueOf(buf.length));
		hConnection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
		hConnection.setRequestProperty("SOAPAction", "http://172.16.0.1/");
		hConnection.setRequestMethod("POST");
		hConnection.setDoOutput(true);
		hConnection.setDoInput(true);
		
		OutputStream outputStream = hConnection.getOutputStream();
		outputStream.write(buf);
		outputStream.close();
		
		byte[] resDatas = readInputStream(hConnection.getInputStream());
		String response = new String(resDatas);

		String sID = parseXML(response);
		String macResult = getInfo(sID);
		//System.out.println("[getSW sendSms Info] This soap response is: " + macResult);
		
		String IP = parseIP(macResult);
		System.out.println("[getSW sendSms Info] This MAC responsed IP is: " + IP);
		
		return IP;
	}
	
	/*This function will extract the ip from the second response*/
	private String parseIP(String macResult) {
		String devIP = null;
		String reg = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
		Pattern pattern = Pattern.compile(reg);
		Matcher m = pattern.matcher(macResult);
		if(m.find()){
			devIP = m.group(0);
			//System.out.println("[parseIP Info] This devIP is: " + devIP);
		}else{
			System.out.println("[getSW parseIP Error] This response does not include devIP && it will try onece");
		}
		
		return devIP;
	}

	private String getInfo(String sID) throws Exception{
		String line = null;
		String result = null;
		String ID = sID;
		StringBuffer xml = new StringBuffer();
		xml.append("<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		xml.append("<env:Header>");
		xml.append("<auth:Authentication env:mustUnderstand=\"1\" xmlns:auth=\"http://www.h3c.com/netconf/base:1.0\">");
		xml.append("<auth:AuthInfo>" + ID + "</auth:AuthInfo>");
		xml.append("</auth:Authentication>");
		xml.append("</env:Header>");
		xml.append("<env:Body>");
		xml.append("<rpc message-id=\"101\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">");
		xml.append("<get>");
		xml.append("<filter>");
		xml.append("<top xmlns=\"http://www.h3c.com/netconf/data:1.0\">");
		xml.append("<ARP>");
		xml.append("<ArpTable>");
		xml.append("<ArpEntry>");
		xml.append("<Ipv4Address></Ipv4Address>");
		xml.append("<MacAddress>"+ reqMAC +"</MacAddress>");
		xml.append("</ArpEntry>");
		xml.append("</ArpTable>");
		xml.append("</ARP>");
		xml.append("</top>");
		xml.append("</filter>");
		xml.append("</get>");
		xml.append("</rpc>");
		xml.append("</env:Body>");
		xml.append("</env:Envelope>");
		
		try {
			String webUrl = "http://172.16.0.1/soap/netconf/";		
			String soapActionString = "http://172.16.0.1";
			URL realUrl = new URL(webUrl);
	        URLConnection conn = realUrl.openConnection();
	        conn.setRequestProperty("Content-Length", String.valueOf(xml.length()));
	        conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
	        conn.setRequestProperty("SOAPAction", soapActionString);
	        conn.setDoOutput(true);
	        conn.setDoInput(true);
	        
	        PrintWriter out = new PrintWriter(conn.getOutputStream());
	        out.print(xml);
	        out.flush();
	        
	        BufferedReader bReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        while((line = bReader.readLine()) != null){
	        	result += line;
	        	//System.out.println("[getMAC getInfo Debug] This Mac-IP Information is: " + result);
	        }
		} catch (Exception e) {
			System.out.println("[getSW getInfo Error]Post is Error" +e);
			e.printStackTrace();
		}        
        
		return result;
	}

	/*This function will parse the xml && it will get the sessionID*/
	private String parseXML(String response) {
		String mresponse = response;
		String sessionID = null;
		String reg = "\\w{36}"; //Patter the sessionID
		Pattern pattern = Pattern.compile(reg);
		Matcher m = pattern.matcher(mresponse);
		if(m.find()){
			sessionID = m.group(0);
			System.out.println("[getSW parseXML Info] This sessionID is: " + sessionID);
		}else{
			System.out.println("[getSW parseXML Error] This response does not include SessionID && it will try reAuthentication");
		}
		 	
		return sessionID;
	}
	
	/*This function will read the data from the Server*/
	private static byte[] readInputStream(InputStream inputStream) throws Exception{
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while((len = inputStream.read(buffer)) != -1){
			outputStream.write(buffer, 0 ,len);
		}
		byte[] data = outputStream.toByteArray();
		outputStream.close();
		inputStream.close();
		return data;

	}
	
}
