package wx.org.img;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class testRegex {
	
	public void run(){
		String myString = "%Mar 15 17:01:11:924 2017 H3C STAMGR/6/STAMGR_CLIENT_ONLINE: Client 7048-0f4f-71f4 went online from BSS 70ba-efb8-5e00 with SSID LHZFH on AP 70ba-efb8-5e00. State changed to Run";
		
		String reg = "STAMGR_CLIENT_ONLINE:\\s*Client\\s*(\\w{4}-\\w{4}-\\w{4})"; //Patter the phone MAC
		Pattern pattern = Pattern.compile(reg);
		Matcher m = pattern.matcher(myString);
		
		if(m.find()){
			String phoneMac = m.group(1);
			System.out.println("[testRegex Debug] This phone mac is: " + phoneMac);
		}else{
			System.out.println("[testRegex Warning] This function has pattern the mac from the syslog");
		}
	}

	
	public static void main(String args[]){
		testRegex tRegex = new testRegex();
		tRegex.run();
	}

}
