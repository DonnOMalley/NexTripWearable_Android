package com.omalleyland.nextripwearable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
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

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class FavoriteDeparture extends Activity {

    private Route SelectedRoute;
    private ProgressDialog ringProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.next_departure);
        SelectedRoute = new Route();

        SelectedRoute.LoadFromIntent(getIntent());
        LoadStopDepartures();
    }

    private void LoadStopDepartures() {
        ringProgressDialog = ProgressDialog.show(this, "Please wait ...", "Loading Departures...", true);
        ringProgressDialog.setCancelable(true);

        new DeparturesRequest(this).execute(SelectedRoute.getRouteID() + "/" + SelectedRoute.getDirectionID() + "/" + SelectedRoute.getStopID());
    }
    public class DeparturesRequest extends AsyncTask<String, Void, String> {

        private Context ctx;

        public DeparturesRequest(Context ctx) {this.ctx = ctx;}
        @Override
        protected String doInBackground(String... params) {

            String XMLResult = "";
            String WebAddress;

            //Execute HTTP Request against API and return XML result
            WebAddress = ctx.getString(R.string.API_WEB_ADDRESS);
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
            ringProgressDialog.dismiss();

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
                    for (int i = 0; i < nList.getLength() - 1; i++) { //Only get the First Departure Time
                        Node nNode = nList.item(i);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) nNode;
                            DepartureText[i] = eElement.getElementsByTagName("DepartureText").item(0).getTextContent();
                            StopDescription[i] = SelectedRoute.getStopDescription() + "\n" + eElement.getElementsByTagName("Description").item(0).getTextContent();
                        }
                    }
                    Log.d("NexTripWearable", "Creating List Adapter for " + SelectedRoute.getStopDescription());


                    //Process Direction Results
                    adapter = new DeparturesList((Activity)ctx, DepartureText, StopDescription);
                    ((ListView)findViewById(R.id.lvAdditionalDepartures)).setAdapter(adapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.favorite_departure, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
