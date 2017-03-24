package wx.org.img;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.regex.Pattern;


import org.apache.commons.httpclient.NameValuePair;
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
import org.json.JSONException;


public class HTTPDigestClient { 
	
	//private static String userName = "";
	//private static String passWord = "";
	private static String url = "https://oasisapi.h3c.com/api/o2oportal/queryBulkAuthUsers";
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
        String nickname = null;
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
            	
            	try {
                	JSONObject jObject = new JSONObject(output.toString());
                	JSONArray re_data = jObject.getJSONArray("datas");
                	for(int i=0; i<re_data.length();i++){
                		JSONObject jsonObject = (JSONObject)re_data.get(i);
                		nickname = jsonObject.getString("nickname");
                		headImgUrl = jsonObject.getString("headimgurl");
                		String usrmac = jsonObject.getString("user_mac");
						System.out.println("[HTTPDigestClient send Info] the nickname is  " + nickname + " and the headerImage is " + headImgUrl + " and the userMac is " + usrmac);
						String selSQL = "select * from sheepwall_app_wifiuser where mac_addr = '" + usrmac + "'";
						String seldevIP = "select IP from mac_ip where Mac = '" + usrmac + "'";
						
						try {
							writeData wData = new writeData();
							wData.getConnection();
							ResultSet rs = wData.selectSQL(selSQL);
							ResultSet rsDevIP = wData.selectSQL(seldevIP);
							if ((!rs.next()) && (rsDevIP.next())){
								String os_type = "Unknown";
								String local_ip = rsDevIP.getString("IP");
								String inSQL = "insert into sheepwall_app_wifiuser (wechat_nickname, wechat_head_img, os_type, mac_addr, local_ip) values ('"+nickname+"','"+headImgUrl+"','"+os_type +"','"+usrmac +"','"+local_ip  +"')";
								boolean flag = wData.insertSQL(inSQL);
								if(flag){
									System.out.println("[HTTPDigestClient send Info] This record has insert into the database " + usrmac);
									wData.deconnSQL();
								}
							}else{
								System.out.println("[HTTPDigestClient send Error] This record has existed in the database " + usrmac);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}

						
						httpClient.getConnectionManager().shutdown();
                	}
				} catch (JSONException e) {
					e.printStackTrace();
				}
           	
            }
                      
  
        } catch (Exception e) {  
              
        }  
        return headImgUrl;  
    }  
    
  
} 
	