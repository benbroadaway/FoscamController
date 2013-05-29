package com.benbroadaway.foscamcontroller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class NetworkUtils {

	private String str_URL;
	private boolean doHttps;

	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	public NetworkUtils(String url, boolean doHttps) {
		this.str_URL = url;
		this.doHttps = doHttps;
	}

	public Document getXMLDocument() {

		Document xmlDoc = null;

		try {
			URL url = new URL(str_URL);
			HttpURLConnection con ;
			
			if (doHttps)
				con = httpsIt(url);
			else
				con = (HttpURLConnection) url.openConnection();

			Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");

			String ct = con.getContentType();
			Matcher m = p.matcher(ct);

			String charset = m.matches() ? m.group(1) : "ISO-8859-1";
			Reader r = new InputStreamReader(con.getInputStream(), charset);
			StringBuilder buf = new StringBuilder();
			while (true) {
				int ch = r.read();
				if (ch < 0) 
					break;
				buf.append((char) ch);
			}

			String str = buf.toString();

			System.out.println("Result:\n" + str);

			System.out.println(str_URL);
			System.out.println(str);


			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(str));

			xmlDoc = dBuilder.parse(is);
		} catch (MalformedURLException badURLEx) {
			badURLEx.printStackTrace();
			return null;
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

		return xmlDoc;
	}
	
	private HttpURLConnection httpsIt(URL url) throws Exception {
		trustAllHosts();
		HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
		https.setHostnameVerifier(DO_NOT_VERIFY);
		
		return https;
	}

	public Bitmap getBitmap() {
		Bitmap bitmap = null;

		try {
			URL url = new URL(str_URL);
			HttpURLConnection con ;
			
			if (doHttps)
				con = httpsIt(url);
			else
				con = (HttpURLConnection) url.openConnection();

			InputStream is = con.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			bitmap = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();			

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
			.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
