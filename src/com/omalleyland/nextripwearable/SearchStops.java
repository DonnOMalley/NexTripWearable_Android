package com.omalleyland.nextripwearable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link //SearchStops.//OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchStops#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SearchStops extends Fragment
        implements Common.IPurchasesResult {

    private ProgressDialog ringProgressDialog;

    private ListView GlobalResults;
    private AdView adView;
    private Boolean AdViewVisible = false;

    public void ManageAdvertisements(View AView) {

        final View view;
        if(AView == null) {
            view = getView();
        }
        else {
            view = AView;
        }

        if(Common.ShowAds && !Common.PremiumMode) {
            adView = new AdView(view.getContext());
            adView.setAdUnitId("ca-app-pub-3393887135508959/1184582426");

            adView.setAdSize(AdSize.BANNER);
            // Create an ad request.
            AdRequest adRequest = new AdRequest.Builder().build();

            // Start loading the ad in the background.
            adView.loadAd(adRequest);

            adView.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    if (!AdViewVisible && !Common.PremiumMode && Common.ShowAds) {
                        ((LinearLayout) view.findViewById(R.id.adViewLayoutSearch)).addView(adView, 0);
                        AdViewVisible = true;
                    }
                }

                public void onAdLeftApplication() {
                    if (AdViewVisible) {
                        ((LinearLayout) view.findViewById(R.id.adViewLayoutSearch)).removeView(adView);
                        AdViewVisible = false;
                    }
                }
            });
        }
        else {
            if ((!Common.ShowAds || Common.PremiumMode) && AdViewVisible) {
                ((LinearLayout) view.findViewById(R.id.adViewLayoutSearch)).removeView(adView);
                AdViewVisible = false;
            }

        }
    }

    public void ProcessPurchases() {
        ManageAdvertisements(null);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchStops.
     */
    public static SearchStops newInstance() {
        return  new SearchStops();
    }

    public SearchStops() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_stops, container, false);
        ImageButton imgBtn = (ImageButton) view.findViewById(R.id.btnSearch);

        ManageAdvertisements(view);

        imgBtn.setOnClickListener(new SearchClickListener(view.getContext(), (EditText) view.findViewById(R.id.edtSearch), (ListView) view.findViewById(R.id.lvResults)));
        if(Common.EnableFavorites || Common.PremiumMode) {
            (view.findViewById(R.id.btnFavorite)).setOnClickListener(new FavoriteClickListener(view.getContext(), (EditText) view.findViewById(R.id.edtSearch)));
        }
        (view.findViewById(R.id.btnFavorite)).setVisibility(View.INVISIBLE);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public class FavoriteClickListener implements View.OnClickListener {

        final private EditText edtStopID;
        Context ctx;

        public FavoriteClickListener(Context ctx, EditText edtStopID) {
            this.ctx = ctx;
            this.edtStopID = edtStopID;
        }

        @Override
        public void onClick(View view) {
            //Add/Remove from favorites
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

            builder.setTitle("Favorites");
            builder.setMessage("Add Route To Favorites?");
            final EditText edtDescription = new EditText(ctx);
            edtDescription.setHint("Description");
            edtDescription.setTextSize(24);
            edtDescription.setPadding(5,5,5,5);

            builder.setView(edtDescription);
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    if(edtDescription.getText().toString().length() > 0) {
                        Route SelectedStop = new Route();
                        SelectedStop.setImageID(R.drawable.ic_launcher);
                        SelectedStop.setType(Common.STOP_TYPE_STOP_ID);
                        SelectedStop.setStopID(edtStopID.getText().toString());
                        SelectedStop.setRouteID(SelectedStop.getStopID());
                        SelectedStop.setStopDescription(edtDescription.getText().toString());

                        new FavoriteRouteDBInterface(ctx).addFavorite(SelectedStop);
                    }
                    else {
                        Toast.makeText(ctx, "Must Enter a Description to Save Favorite", Toast.LENGTH_LONG).show();
                    }
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
    }

    public class SearchClickListener implements View.OnClickListener {

        Context ctx;
        ListView Results;
        private EditText SearchText;

        public SearchClickListener(Context ctx, EditText SearchText, ListView Results){
            this.ctx = ctx;
            this.Results = Results;
            this.SearchText = SearchText;
        }

        @Override
        public void onClick(View v) {
            ringProgressDialog = ProgressDialog.show(ctx, "Please wait ...", "Loading Stop Info...", true);
            ringProgressDialog.setCancelable(true);

            new SearchRequest(this.ctx, this.Results).execute(this.SearchText.getText().toString());
        }
    }

    public class SearchRequest extends AsyncTask<String, Void, String> {

        private Context ctx;

        public SearchRequest(Context ctx, ListView Results) {
            this.ctx = ctx;
            GlobalResults = Results;
        }
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

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                InputSource is = new InputSource(new StringReader(result));
                Document doc = dBuilder.parse(is);
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("NexTripDeparture");
                Log.d("NexTripWearable", Integer.toString(nList.getLength()));
                if(nList.getLength() > 0) {
                    if(Common.PremiumMode || Common.EnableFavorites) {
                        (getView().findViewById(R.id.btnFavorite)).setVisibility(View.VISIBLE);
                    }
                    String[] routes = new String[nList.getLength()];
                    String[] departures = new String[nList.getLength()];
                    String[] directions = new String[nList.getLength()];
                    String[] descriptions = new String[nList.getLength()];
                    for (int i = 0; i < nList.getLength(); i++) {
                        Node nNode = nList.item(i);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) nNode;
                            routes[i] = eElement.getElementsByTagName("Route").item(0).getTextContent();
                            departures[i] = eElement.getElementsByTagName("DepartureText").item(0).getTextContent();
                            directions[i] = eElement.getElementsByTagName("RouteDirection").item(0).getTextContent();
                            descriptions[i] = eElement.getElementsByTagName("Description").item(0).getTextContent();
                        }
                    }
                    Log.d("NexTripWearable", "Creating List Adapter");
                    ResultsList adapter = new ResultsList(getActivity(), routes, departures, directions, descriptions);
                    GlobalResults.setAdapter(adapter);

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
