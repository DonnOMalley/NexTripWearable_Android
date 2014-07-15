package com.omalleyland.nextripwearable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class Direction extends Activity {

    private Route SelectedRoute;
    private Context ctx;
    private String[] DirectionIDs;
    private ProgressDialog ringProgressDialog;
    private Boolean DepartureTimeVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        ctx = this;
        Intent intent = getIntent();
        SelectedRoute = new Route();
        SelectedRoute.LoadFromIntent(intent);

        DirectionIDs = intent.getStringArrayExtra("DirectionIDs");
        final String[] DirectionTexts = intent.getStringArrayExtra("DirectionTexts");

        ((ImageView)findViewById(R.id.imgDirectionIcon)).setImageResource(SelectedRoute.getImageID());
        ((TextView)findViewById(R.id.tvDirectionRoute)).setText(SelectedRoute.getRouteID() + " - " + SelectedRoute.getRouteDescription().replace("\n", " "));
        ((RadioButton)findViewById(R.id.rdoDirection1)).setText(DirectionTexts[0]);
        ((RadioButton)findViewById(R.id.rdoDirection2)).setText(DirectionTexts[1]);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rdoGrpDirections);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if(checkedId == R.id.rdoDirection1) {
                    //Load Stops for Direction1
                    SelectedRoute.setDirectionID(DirectionIDs[0]);
                    SelectedRoute.setDirectionText(DirectionTexts[0]);
                }
                else {
                    //Load Stops for Direction2
                    SelectedRoute.setDirectionID(DirectionIDs[1]);
                    SelectedRoute.setDirectionText(DirectionTexts[1]);
                }
                LoadStops();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(DepartureTimeVisible){
            LoadStops();
        }else{
            super.onBackPressed();
        }

    }

    private void LoadStops() {
        findViewById(R.id.lvDirectionStops).setVisibility(View.INVISIBLE);
        DepartureTimeVisible = false;
        ringProgressDialog = ProgressDialog.show(ctx, "Please wait ...", "Loading Stops...", true);
        ringProgressDialog.setCancelable(true);

        new StopRequest().execute("Stops/" + SelectedRoute.getRouteID() + "/" + SelectedRoute.getDirectionID());
    }

    public class StopRequest extends AsyncTask<String, Void, String> {

        public StopRequest() {}
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

            final String[] StopIDs;
            final String[] StopTexts;
            StopList adapter;

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                InputSource is = new InputSource(new StringReader(result));
                Document doc = dBuilder.parse(is);
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("TextValuePair");
                Log.d("NexTripWearable", Integer.toString(nList.getLength()));
                if(nList.getLength() > 0) {
                    StopIDs = new String[nList.getLength()];
                    StopTexts = new String[nList.getLength()];
                    for (int i = 0; i < nList.getLength(); i++) {
                        Node nNode = nList.item(i);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) nNode;
                            StopTexts[i] = eElement.getElementsByTagName("Text").item(0).getTextContent();
                            StopIDs[i] = eElement.getElementsByTagName("Value").item(0).getTextContent();
                        }
                    }
                    Log.d("NexTripWearable", "Creating List Adapter");

                    //Process Direction Results

                    adapter = new StopList((Activity)ctx, StopIDs, StopTexts);
                    ((ListView)findViewById(R.id.lvDirectionStops)).setAdapter(adapter);
                    findViewById(R.id.lvDirectionStops).setVisibility(View.VISIBLE);
                    ((ListView)findViewById(R.id.lvDirectionStops)).setOnItemLongClickListener(
                        new AdapterView.OnItemLongClickListener() {
                            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                                if(!DepartureTimeVisible) {
                                    Log.v("long clicked", "pos: " + pos);
                                    final int PosID = pos;

                                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

                                    builder.setTitle("Favorites");
                                    builder.setMessage("Add Route To Favorites?");

                                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            SelectedRoute.setStopID(StopIDs[PosID]);
                                            SelectedRoute.setStopDescription(StopTexts[PosID]);

                                            new FavoriteRouteDBInterface(ctx).addFavorite(SelectedRoute);
                                        }

                                    });

                                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Do nothing
                                            dialog.dismiss();
                                        }
                                    });

                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                                return true;
                            }
                    });

                    ((ListView)findViewById(R.id.lvDirectionStops)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(!DepartureTimeVisible) {
                                SelectedRoute.setStopID(StopIDs[position]);
                                SelectedRoute.setStopDescription(StopTexts[position]);
                                LoadStopDepartures();
                            }
                        }
                    });
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

    private void LoadStopDepartures() {
        findViewById(R.id.lvDirectionStops).setVisibility(View.INVISIBLE);
        ringProgressDialog = ProgressDialog.show(ctx, "Please wait ...", "Loading Departures...", true);
        ringProgressDialog.setCancelable(true);

        new DeparturesRequest().execute(SelectedRoute.getRouteID() + "/" + SelectedRoute.getDirectionID() + "/" + SelectedRoute.getStopID());
    }

    public class DeparturesRequest extends AsyncTask<String, Void, String> {

        public DeparturesRequest() {}
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
                    for (int i = 0; i < nList.getLength(); i++) { //Only get the First Departure Time
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
                    ((ListView)findViewById(R.id.lvDirectionStops)).setAdapter(adapter);
                    findViewById(R.id.lvDirectionStops).setVisibility(View.VISIBLE);
                    DepartureTimeVisible = true;
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
        getMenuInflater().inflate(R.menu.direction, menu);
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
