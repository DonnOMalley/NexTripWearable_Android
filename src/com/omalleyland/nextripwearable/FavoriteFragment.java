package com.omalleyland.nextripwearable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.List;

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
public class FavoriteFragment extends ListFragment {

    private OnFavoriteSelectionListener mListener;
    private List<Route> FavoriteRoutes = null;

    public static FavoriteFragment newInstance() {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FavoriteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new FavoriteRequest(getActivity().getApplicationContext()).execute();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFavoriteSelectionListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Route FavoriteRoute = FavoriteRoutes.get(position);

                //Add/Remove from favorites
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setTitle("Delete");
                builder.setMessage("Delete Route From Favorites?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        new FavoriteRouteDBInterface(getActivity().getApplicationContext()).deleteFavorite(FavoriteRoute);
                        new FavoriteRequest(getActivity().getApplicationContext()).execute();
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
                return true;
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = FavoriteRoutes.get(position).BuildIntent(getActivity().getApplicationContext(), FavoriteDeparture.class);
        startActivity(intent);
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
    public interface OnFavoriteSelectionListener {
        public void onFavoriteSelection(String RouteID, String RouteText, String DirectionID);
    }

    public class FavoriteRequest extends AsyncTask<Void, Void, List<Route>> {

        private Context ctx;

        protected FavoriteRequest(Context ctx) {
            this.ctx = ctx;
        }
        @Override
        protected List<Route> doInBackground(Void... params) {

            List<Route> Favorites = null;

            Favorites = new FavoriteRouteDBInterface(this.ctx).getAllFavorites();

            return Favorites;
        }

        @Override
        public void onPostExecute(List<Route> Favorites) {
            FavoriteList adapter = null;
            Integer[] ImageID;
            String[] RouteID;
            String[] RouteDescription;
            String[] DirectionText;
            FavoriteRoutes = Favorites;

            if(FavoriteRoutes.size() > 0) {
                ImageID = new Integer[FavoriteRoutes.size()];
                RouteID = new String[FavoriteRoutes.size()];
                RouteDescription = new String[FavoriteRoutes.size()];
                DirectionText = new String[FavoriteRoutes.size()];
                for (int i = 0; i < FavoriteRoutes.size(); i++) {
                    DirectionText[i] = FavoriteRoutes.get(i).getDirectionText();
                    ImageID[i] = FavoriteRoutes.get(i).getImageID();
                    RouteDescription[i] = FavoriteRoutes.get(i).getStopDescription();
                    RouteID[i] = FavoriteRoutes.get(i).getRouteID();

                    if(getActivity() != null) {
                        adapter = new FavoriteList(getActivity(), ImageID, RouteID, RouteDescription, DirectionText);
                    }
                }
            }
            setListAdapter(adapter);
        }
    }

}
