package com.omalleyland.nextripwearable;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;

public class AccessoryProviderService extends SAAgent {
	public static final String TAG = "AccessoryProviderService";

	public static final int SERVICE_CONNECTION_RESULT_OK = 0;

	public static final int ACCESSORY_CHANNEL_ID = 100;

	HashMap<Integer, AccessoryProviderConnection> mConnectionsMap = null;

	private final IBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		public AccessoryProviderService getService() {
			return AccessoryProviderService.this;
		}
	}

	public AccessoryProviderService() {
		super(TAG, AccessoryProviderConnection.class);
	}

	public class AccessoryProviderConnection extends SASocket {
		private int mConnectionId;

		public AccessoryProviderConnection() {
			super(AccessoryProviderConnection.class.getName());
		}

		@Override
		public void onError(int channelId, String errorString, int error) {
			Log.e(TAG, "Connection is not alive ERROR: " + errorString + "  "
					+ error);
		}

		@Override
		public void onReceive(int channelId, byte[] data) {
			Log.d(TAG, "onReceive");

			String CommandString = new String(data);
			if(CommandString.equals("getFavorites")) {
				//Make Favorite Reqest
				new FavoriteRequest(mConnectionId).execute();
			}
			else {
				//Command is a Route/Direction/stop
				new DeparturesRequest(mConnectionId).execute(new String(data));
			}
		}

		@Override
		protected void onServiceConnectionLost(int errorCode) {
			Log.e(TAG, "onServiceConectionLost  for peer = " + mConnectionId
					+ "error code =" + errorCode);

			if (mConnectionsMap != null) {
				mConnectionsMap.remove(mConnectionId);
			}
		}
	}

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate of smart view Provider Service");
        
        SA mAccessory = new SA();
        try {
        	mAccessory.initialize(this);
        } catch (SsdkUnsupportedException e) {
        	// Error Handling
        } catch (Exception e1) {
            Log.e(TAG, "Cannot initialize Accessory package.");
            e1.printStackTrace();
			/*
			 * Your application can not use Accessory package of Samsung
			 * Mobile SDK. You application should work smoothly without using
			 * this SDK, or you may want to notify user and close your app
			 * gracefully (release resources, stop Service threads, close UI
			 * thread, etc.)
			 */
            stopSelf();
        }

    }	

    @Override 
    protected void onServiceConnectionRequested(SAPeerAgent peerAgent) { 
        acceptServiceConnectionRequest(peerAgent); 
    } 
    
	@Override
	protected void onFindPeerAgentResponse(SAPeerAgent arg0, int arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onFindPeerAgentResponse  arg1 =" + arg1);		
	}

	@Override
	protected void onServiceConnectionResponse(SASocket thisConnection, int result) {
		if (result == CONNECTION_SUCCESS) {
			if (thisConnection != null) {
				AccessoryProviderConnection myConnection = (AccessoryProviderConnection) thisConnection;

				if (mConnectionsMap == null) {
					mConnectionsMap = new HashMap<Integer, AccessoryProviderConnection>();
				}

				myConnection.mConnectionId = (int) (System.currentTimeMillis() & 255);

				Log.d(TAG, "onServiceConnection connectionID = "
						+ myConnection.mConnectionId);

				mConnectionsMap.put(myConnection.mConnectionId, myConnection);
			} else {
				Log.e(TAG, "SASocket object is null");
			}
		} else if (result == CONNECTION_ALREADY_EXIST) {
			Log.e(TAG, "onServiceConnectionResponse, CONNECTION_ALREADY_EXIST");
		} else {
			Log.e(TAG, "onServiceConnectionResponse result error =" + result);
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	public class FavoriteRequest extends AsyncTask<Void, Void, List<Route>> {

		int ConnID;
		
        protected FavoriteRequest(int ConnID) {this.ConnID = ConnID;}
        @Override
        protected List<Route> doInBackground(Void... params) {

            List<Route> Favorites = null;

            Favorites = new FavoriteRouteDBInterface(getApplicationContext()).getAllFavorites();

            return Favorites;
        }

        @Override
        public void onPostExecute(List<Route> Favorites) {
            FavoriteList adapter = null;
            Integer[] ImageID;
            String[] RouteID;
            String[] RouteDescription;
            String[] DirectionText;
            List<Route> FavoriteRoutes = Favorites;
            String XMLString = "";

            if(FavoriteRoutes.size() > 0) {
            	XMLString = "<Favorites>";
                ImageID = new Integer[FavoriteRoutes.size()];
                RouteID = new String[FavoriteRoutes.size()];
                RouteDescription = new String[FavoriteRoutes.size()];
                DirectionText = new String[FavoriteRoutes.size()];
                for (int i = 0; i < FavoriteRoutes.size(); i++) {
                    ImageID[i] = FavoriteRoutes.get(i).getImageID();
                    RouteID[i] = FavoriteRoutes.get(i).getRouteID();
                    RouteDescription[i] = FavoriteRoutes.get(i).getStopDescription();
                    DirectionText[i] = FavoriteRoutes.get(i).getDirectionText();
                    XMLString = XMLString + "<Route><RouteDescription>" + RouteID[i] + " " + RouteDescription[i] + " " + DirectionText[i] + 
                    						"</RouteDescription><Departure>" + 
                    						RouteID[i] + "/" + FavoriteRoutes.get(i).getDirectionID() + "/" + FavoriteRoutes.get(i).getStopID() + 
                    						"</Departure></Route>";
                }
                XMLString = XMLString + "</Favorites>";
                
                
                final String txt=XMLString;

				final AccessoryProviderConnection uHandler = mConnectionsMap.get(Integer.parseInt(String.valueOf(ConnID)));
				if(uHandler == null){
					Log.e(TAG,"Error, can not get AccessoryProviderConnection handler");
					return;
				}
				new Thread(new Runnable() {
					public void run() {
						try {
							uHandler.send(ACCESSORY_CHANNEL_ID, txt.getBytes());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();
                
            }
        }
    }
	
	public class DeparturesRequest extends AsyncTask<String, Void, String> {

        private int ConnID;

        public DeparturesRequest(int ConnID) {this.ConnID = ConnID;}
        @Override
        protected String doInBackground(String... params) {

            String XMLResult = "";
            String WebAddress;

            //Execute HTTP Request against API and return XML result
            WebAddress = getApplicationContext().getString(R.string.API_WEB_ADDRESS);
            Log.d("NexTripWearable", WebAddress + params[0]);
            HttpGet httpGet = new HttpGet(WebAddress + params[0]);
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpEntity httpEntity = httpClient.execute(httpGet).getEntity();
                XMLResult = EntityUtils.toString(httpEntity);
                Log.d("NexTripWearable", XMLResult);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return XMLResult;
        }

        @Override
        public void onPostExecute(String result) {

            String[] DepartureText;
            String[] StopDescription;
            DeparturesList adapter;

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                InputSource is = new InputSource(new StringReader(result));
                Document doc = dBuilder.parse(is);
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("NexTripDeparture");
                Log.d("NexTripWearable", Integer.toString(nList.getLength()));
                if(nList.getLength() > 0) {
                    DepartureText = new String[nList.getLength()];
                    StopDescription = new String[nList.getLength()];

                    String txt="<NextDeparture>";
                    for (int i = 0; i < nList.getLength(); i++) { //Only get the First Departure Time
                        Node nNode = nList.item(i);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) nNode;
                            DepartureText[i] = eElement.getElementsByTagName("DepartureText").item(0).getTextContent();

                    		txt = txt + "<DepartureTime>" + DepartureText[i] + "</DepartureTime>";
                        }
                    }
                    
                    txt = txt + "</NextDeparture>";

                    final String resultXML = txt;
    				final AccessoryProviderConnection uHandler = mConnectionsMap.get(Integer.parseInt(String.valueOf(ConnID)));
    				if(uHandler == null){
    					Log.e(TAG,"Error, can not get AccessoryProviderConnection handler");
    					return;
    				}
    				new Thread(new Runnable() {
    					public void run() {
    						try {
    							uHandler.send(ACCESSORY_CHANNEL_ID, resultXML.getBytes());
    						} catch (IOException e) {
    							e.printStackTrace();
    						}
    					}
    				}).start();
                    
                    
                }

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
	
}