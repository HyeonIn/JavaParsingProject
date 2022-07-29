package rent.model.dto;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

import javax.swing.ImageIcon;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import rent.view.HomeInfoView;

public class NaverMap{
	
	HomeInfoView naverMap;
	public NaverMap(HomeInfoView naverMap) {
		this.naverMap = naverMap;
	}
	
	public void showMap() {
		// TODO Auto-generated method stub
		String clientId = "api 토큰 아이디";
		String clientSecret = "api 토큰 비밀번호";
		AddressInfo ai = null;		
		
		try {
			String address = "서울시 종로구" +  naverMap.getCurHome().getDong() + naverMap.getCurHome().getJibun();
			
			String addr = URLEncoder.encode(address, "UTF-8");
			String apiURL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + addr;
			URL url = new URL(apiURL);
			
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
			con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
			
			int responseCode = con.getResponseCode();
			BufferedReader br;
			if (responseCode == 200) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			} else {
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			br.close();
			
			JSONTokener tokener = new JSONTokener(response.toString());
			JSONObject object = new JSONObject(tokener);
			System.out.println(object);
			
			JSONArray arr = object.getJSONArray("addresses");
			for (int i = 0; i < arr.length(); i++) {
				JSONObject temp = (JSONObject) arr.get(i);
				ai = new AddressInfo();
				ai.setRoadAddress((String) temp.get("roadAddress"));
				ai.setJibunAddress((String)temp.get("jibunAddress"));
				ai.setX((String)temp.get("x"));
				ai.setY((String)temp.get("y"));
				System.out.println(ai);
			}
						
			map_service(ai); 
			
		}catch (Exception err) {
			System.out.println(err);
		}
		
	}
	
	public void map_service(AddressInfo vo) {
		String URL_STATICMAP = "https://naveropenapi.apigw.ntruss.com/map-static/v2/raster?";		
		try {
			String pos = URLEncoder.encode(vo.getX() + " " + vo.getY(), "UTF-8");
			URL_STATICMAP += "center=" + vo.getX() + "," + vo.getY();
			URL_STATICMAP += "&level=16&w=700&h=500";
			URL_STATICMAP += "&markers=type:t|size:mid|pos:" + pos + "|label:" + URLEncoder.encode(vo.getRoadAddress(), "UTF-8");
			
			URL url = new URL(URL_STATICMAP);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "x7hfe5pbt3");
			con.setRequestProperty("X-NCP-APIGW-API-KEY", "BgegspgMDNjYy0ZMKLyesFUgWxA1tqDJDo1viq9x");
			
			int responseCode = con.getResponseCode();
			BufferedReader br;
						
			System.out.println(responseCode);
			// 정상호출인 경우.
			if (responseCode == 200) {				
				InputStream is = con.getInputStream();
				
				int read = 0;
				byte[] bytes = new byte[1024];
				
				// 랜덤 파일명으로 파일 생성
				String tempName = Long.valueOf(new Date().getTime()).toString();
				File file = new File(tempName + ".jpg");	// 파일 생성.
				
				file.createNewFile();
				
				OutputStream out = new FileOutputStream(file);
				
				while ((read = is.read(bytes)) != -1) {
					out.write(bytes, 0, read);	// 파일 작성
				}
				
				is.close();
				ImageIcon img = new ImageIcon(file.getName());				
				naverMap.setMapL(img);
//				naverMap.imageLabel.setIcon(img);
//				naverMap.resAddress.setText(vo.getRoadAddress());
//				naverMap.jibunAddress.setText(vo.getJibunAddress());
//				naverMap.resX.setText(vo.getX());
//				naverMap.resY.setText(vo.getY());
				
			} else {
				System.out.println(responseCode);
			}
			
		} catch(Exception e) {
			System.out.println(e);
		}
		
	}
	
	
}
