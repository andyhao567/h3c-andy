package wx.test.img;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;






public class HTTPDigestClient { 
	
	private static String userName = "security_super";
	private static String passWord = "lvzhou1-super";
	private static String url = "https://oasisrdapi.h3c.com/api/o2oportal/queryBulkAuthUsers";
    private URI serverURI = null;  
    String response = null;  
    
    @SuppressWarnings("deprecation")
	private DefaultHttpClient httpClient = new DefaultHttpClient();  
  
    /** 
     * constructor 
     */  
    @SuppressWarnings("deprecation")
	public HTTPDigestClient(String userName, String passWord) {  
        try {  
            serverURI = new URI(url);  
            Credentials creds = new UsernamePasswordCredentials(userName, passWord);  
  
            httpClient.getCredentialsProvider().setCredentials(  
                    new AuthScope(serverURI.getHost(), serverURI.getPort()), (Credentials) creds);  
  
            httpClient.getParams().setParameter(  
                    AuthPolicy.AUTH_SCHEME_PRIORITY, Collections.singleton(AuthPolicy.DIGEST));  
            httpClient.getAuthSchemes().register(AuthPolicy.DIGEST,  
                    new DigestSchemeFactory());  
  
        } catch (Exception e) {  
              
        }  
    }  
  
    /** 
     * send request to server 
     *  
     * @param httpClient 
     * @param httpUriRequest 
     * @return response HttpResponse 
     */  
    @SuppressWarnings({ "deprecation", "deprecation" })
	public String send(HttpUriRequest httpUriRequest) {  
        HttpResponse response = null; 
        String output = null;
        String headImgUrl = null;
        String img = null;
		String reg = "\"(http:[^\"]*)"; //Patter the headURL
		Pattern pattern = Pattern.compile(reg);
		
        try {  
            if (httpClient == null) {  
                  
                System.out.println("[HTTPDigestClient send Error] This httpClient is null");  
            }  
            response = httpClient.execute(httpUriRequest);
            BufferedReader bReader = new BufferedReader(new InputStreamReader((response.getEntity().getContent()), "UTF-8"));
            
            while((output = bReader.readLine()) != null){
            	System.out.println("[HTTPDigestClient send Info] This Server response is: " + output);
            	try {
                	JSONObject jObject = new JSONObject(output.toString());
                	JSONArray re_data = jObject.getJSONArray("datas");
                	//String re_hURL = jObject.getString("headimgurl");
                	System.out.println("[HTTPDigestClient send Info] the datas contents is " + re_data );

                	for(int i=0; i<re_data.length();i++){
                		 JSONObject jsonObject=(JSONObject)re_data.get(i);
                		 String nickname=jsonObject.getString("headimgurl");
						System.out.println("[HTTPDigestClient send Info] the re_uName is " + nickname + "length" + re_data.length());
                	}
                	//String re_uName = re_data.toString();
                	//System.out.println("[HTTPDigestClient send Info] the re_uName is " + re_uName );
				} catch (Exception e) {
					e.printStackTrace();
				}

            	
            	
            	Matcher m = pattern.matcher(output);
        		if(m.find()){
        			headImgUrl = m.group(1);
        		}else{
        			System.out.println("[HTTPDigestClient send Error] This response does not include headImgUrl");
        		}
            	//System.out.println("[HTTPDigestClient send Debug] This server response is: " + output);
            	
            }
            httpClient.getConnectionManager().shutdown();          
  
        } catch (Exception e) {  
              
        }  
        return headImgUrl;  
    }  
    
    /*
     * This function will download the weChat header Image from the Web*/
	private static void getPageImg(String hImgURL) {
		StringBuffer sBuffer = new StringBuffer();
		try{
			URL url = new URL(hImgURL);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(5*1000);
			InputStream iStream = conn.getInputStream();
			byte[] bs = new byte[10240];
			int len;
			
			File file = new File("c:\\sheep");
			if(!file.exists()){
				file.mkdirs();
			}
			
			OutputStream oStream = new FileOutputStream(file.getPath() + "\\" + "aa.jpg");
			while((len = iStream.read(bs)) != -1){
				oStream.write(bs, 0, len);
			}
			oStream.close();
			iStream.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
    
    public static void main(String args[]) throws IOException{
    	HTTPDigestClient hDigestClient = new HTTPDigestClient(userName, passWord);
    	HttpPost hPost = new HttpPost(url);
    	hPost.setHeader("Accept", "application/json");

        StringEntity stringEntity = new StringEntity("{\"macs\":[\"B0-9F-BA-DF-1C-DE\"],\"macType\":1}");
        stringEntity.setContentType("application/json");
        stringEntity.setContentEncoding("UTF-8");
    	hPost.setEntity(stringEntity);
    	String hImgURL = hDigestClient.send(hPost);
    	System.out.println("[HTTPDigestClient main Info] This headImgUrl is: " + hImgURL);
    	
    	//getPageImg(hImgURL);
    }


  
} 
