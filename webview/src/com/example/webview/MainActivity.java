package com.example.webview;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

	private final String webUrl = "http://t.co/eRSN3xsQ";//http://en.wikipedia.org/wiki/Lisbon_Appointment";
	private String webName;
	private static final String TAG = "webView";
	private static final String WEB_PATH = "test";
	boolean isSDAvail = false;
	boolean isSDWritable = false;
	Button downloadAndSaveWeb;
	Button readWeb;
	WebView mWebView;
	WebView mWebView1;
	Bitmap bm;
	Uri webUri;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		checkSDStuff();
		webName = "1234" + ".xml";
		downloadAndSaveWeb = (Button) findViewById(R.id.downloadWeb);
		downloadAndSaveWeb.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(isSDWritable && isSDAvail){
					
					webUri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(WEB_PATH), webName));
					//mWebView.loadUrl(webUrl);
				}
			}
		});
		readWeb = (Button) findViewById(R.id.readWeb);
		//readWeb.setEnabled(false);
		readWeb.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(isSDWritable && isSDAvail){
					try {
			            FileInputStream is = new FileInputStream(webUri.getPath());
			            WebArchiveReader wr = new WebArchiveReader() {
			                void onFinished(WebView v) {
			                    // we are notified here when the page is fully loaded.
			                    Log.d(TAG, "load finished");
			                    continueWhenLoaded(v);
			                }
			            };
			            // To read from a file instead of an asset, use:
			            // FileInputStream is = new FileInputStream(fileName);
			            if (wr.readWebArchive(is)) {
			                wr.loadToWebView(mWebView1);
			            }
			        } catch (IOException e) {
			            e.printStackTrace();
			        }
				}
			}
		});

		
		mWebView = new WebView(this);
		mWebView1 = (WebView) findViewById(R.id.web_view);
		mWebView1.getSettings().setJavaScriptEnabled(true); 
		mWebView1.getSettings().setBuiltInZoomControls(true);
		mWebView1.getSettings().setDomStorageEnabled(true);
		mWebView.setWebViewClient(new WebClientDownload());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setDomStorageEnabled(true);
	
		
	}
	private void continueWhenLoaded(WebView webView) {
        Log.d(TAG, "Page from WebArchive fully loaded.");
        // If you need to set your own WebViewClient, do it here,
        // after the WebArchive was fully loaded:
        webView.setWebViewClient(new WebClientView());
        // Any other code we need to execute after loading a page from a WebArchive...
    }
	
	private class WebClientDownload extends WebViewClient {

		private boolean loadingFailed = false;
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			Log.d(TAG, "on page started");	
			super.onPageStarted(view, url, favicon);
		}


		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO Auto-generated method stub
			Log.d(TAG, "on received error");
			loadingFailed = true;
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			view.saveWebArchive(webUri.getPath());
			readWeb.setEnabled(true);
			if(loadingFailed){
				Log.d(TAG, "loading failed");
			}
			else{
				Log.d(TAG, "onPageFinished and downloaded:" + url);
			}
			
		}
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
			return true;
		}

	}
    
	private class WebClientView extends WebViewClient {

		@Override
		public void onLoadResource(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onLoadResource(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			Log.d(TAG, "onPageFinished without download");
			super.onPageFinished(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			Log.d(TAG, "onPageStarted");
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stubsuper.shouldOverrideUrlLoading(view, url);
			return true;
		}
		
		
	}
	
	
	private void checkSDStuff(){
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)){
			isSDAvail = true;
			isSDWritable = true;
			Log.d("check", "check success");
		}
		else if(Environment.MEDIA_MOUNTED_READ_ONLY.endsWith(state)){
			isSDAvail = true;
			isSDWritable = true;
			Log.d("check", "check failed");
		}
		else{
			isSDAvail = false;
			isSDWritable = false;
			Log.d("check", "check failed");
		}
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
