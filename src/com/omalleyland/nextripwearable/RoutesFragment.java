package com.omalleyland.nextripwearable;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

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
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link //Callbacks}
 * interface.
 */
public class RoutesFragment extends Fragment
    implements Common.IPurchasesResult {

    private OnRouteSelectionListener mListener;
    private AdView adView;
    private Boolean AdViewVisible = false;
    private ListView RouteListView;

    public void ManageAdvertisements(View AView) {

        final View view;
        if(AView == null) {
            view = getView();
        }
        else {
            view = AView;
        }

        if(Common.ShowAds && !Common.PremiumMode && (AView != null)) {
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
                        ((LinearLayout) view.findViewById(R.id.adViewLayoutRoutes)).addView(adView, 0);
                        AdViewVisible = true;
                    }
                }

                public void onAdLeftApplication() {
                    if (AdViewVisible) {
                        ((LinearLayout) view.findViewById(R.id.adViewLayoutRoutes)).removeView(adView);
                        AdViewVisible = false;
                    }
                }
            });
        }
        else {
            if ((!Common.ShowAds || Common.PremiumMode) && AdViewVisible) {
                ((LinearLayout) view.findViewById(R.id.adViewLayoutRoutes)).removeView(adView);
                AdViewVisible = false;
            }

        }
    }

    public void ProcessPurchases() {
        ManageAdvertisements(null);
    }

    public static RoutesFragment newInstance() {
        return new RoutesFragment();
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RoutesFragment() {
    }

    String[] routeId;
    String[] routes;
    Integer[] imageId;
    ImageList adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_routes, container, false);

        RouteListView = ((ListView)view.findViewById(R.id.lvRouteList));
        RouteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onRouteSelection(routeId[position], routes[position]);
                }
            }
        });

        ManageAdvertisements(view);


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnRouteSelectionListener) activity;
            new RouteAPIRequest(getActivity().getApplicationContext()).execute(new String[]{"Routes"});




        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void processResult(String result) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(result));
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("NexTripRoute");
            Log.d("NexTripWearable", Integer.toString(nList.getLength()));
            if(nList.getLength() > 0) {
                routeId = new String[nList.getLength()];
                routes = new String[nList.getLength()];
                imageId = new Integer[nList.getLength()];

                for (int i = 0; i < nList.getLength(); i++) {
                    Node nNode = nList.item(i);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        routeId[i] = eElement.getElementsByTagName("Route").item(0).getTextContent();
                        routes[i] = eElement.getElementsByTagName("Description").item(0).getTextContent().replace(" - ", "\n").replace("-", "\n").replace(eElement.getElementsByTagName("Route").item(0).getTextContent() + "\n", "");
                        if(Integer.valueOf(eElement.getElementsByTagName("Route").item(0).getTextContent()) > 900) {
                            imageId[i] = R.drawable.lightrail;
                            Log.d("NexTripWearable", "Wrote LightRail Route : " + routes[i]);
                        }
                        else {
                            imageId[i] = R.drawable.bus;
                            Log.d("NexTripWearable", "Wrote Bus Route : " + routes[i]);
                        }
                    }
                }
                Log.d("NexTripWearable", "Creating List Adapter");
                if(getActivity() != null) {
                    adapter = new ImageList(getActivity(), routeId, routes, imageId);
                }
                RouteListView.setAdapter(adapter);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRouteSelectionListener {
        public void onRouteSelection(String RouteID, String RouteText);
    }

    public class RouteAPIRequest extends APIRequest {

        public RouteAPIRequest(Context ctx) {
            super(ctx);
        }
        @Override
        public void onPostExecute(String result) {
            processResult(result);
        }

    }

}
