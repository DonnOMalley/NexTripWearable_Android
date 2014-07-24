package com.omalleyland.nextripwearable;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
                    RoutesFragment.OnRouteSelectionListener,
                    FavoriteFragment.OnFavoriteSelectionListener,
                    Common.IPurchasesResult
{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private ProgressDialog ringProgressDialog;
    private Route ActiveRoute;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Common.CleanupInAppPurchasing();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.InitInAppPurchasing(getBaseContext(), this);
        ActiveRoute = new Route();

        Common.LoadPreferences();

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)getFragmentManager().findFragmentById(R.id.navigation_drawer);

        //Initialize Title to First Section
        mTitle = getString(R.string.title_section1);

        // Set up the drawer.

        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        if(Common.EnableFavorites || Common.PremiumMode) {
            mNavigationDrawerFragment.selectItem(2);
            onNavigationDrawerItemSelected(2);
            restoreActionBar();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();

        switch(position) {
            case 0:
                mTitle = getString(R.string.title_section1);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, RoutesFragment.newInstance(), Common.ROUTE_FRAGMENT_TAG)
                        .commit();
                break;
            case 1:
                mTitle = getString(R.string.title_section2);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, SearchStops.newInstance(), Common.SEARCH_STOP_FRAGMENT_TAG)
                        .commit();
                break;
            case 2:
                mTitle = getString(R.string.title_section3);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, FavoriteFragment.newInstance(), Common.FAVORITE_FRAGMENT_TAG)
                        .commit();
                break;
        }

    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(menu != null) {
            if (!mNavigationDrawerFragment.isDrawerOpen()) {
                // Only show items in the action bar relevant to this screen
                // if the drawer is not showing. Otherwise, let the drawer
                // decide what to show in the action bar.
                if (!Common.PremiumMode && (!Common.EnableFavorites || Common.ShowAds)) {
                    getMenuInflater().inflate(R.menu.main, menu);
                    restoreActionBar();
                }
                return true;
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            //Launch MyPurchases
            Intent i = new Intent(this, MyPurchases.class);
            startActivityForResult(i, Common.PURCHASES_REQUEST_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void ProcessPurchases() {
        mNavigationDrawerFragment.UpdateNavigation();

        //Refresh things
        Fragment routeFragment = getFragmentManager().findFragmentByTag(Common.ROUTE_FRAGMENT_TAG);
        Fragment stopFragment = getFragmentManager().findFragmentByTag(Common.SEARCH_STOP_FRAGMENT_TAG);

        if(routeFragment != null) {
            ((Common.IPurchasesResult)routeFragment).ProcessPurchases();
        }
        if(stopFragment != null) {
            ((Common.IPurchasesResult)stopFragment).ProcessPurchases();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Common.PURCHASES_REQUEST_CODE) {
            ProcessPurchases();
        }
    }

    public void onFavoriteSelection(String RouteID, String RouteText, String DirectionID) { }

    public void onRouteSelection(String RouteID, String RouteText) {//, String DirectionID) {
        ringProgressDialog = ProgressDialog.show(this, "Please wait ...", "Loading Directions...", true);
        ringProgressDialog.setCancelable(true);

        ActiveRoute.setRouteID(RouteID);
        ActiveRoute.setRouteDescription(RouteText);

        new DirectionRequest(this, ActiveRoute).execute("Directions/" + RouteID);
    }

    public class DirectionRequest extends AsyncTask<String, Void, String> {

        private Context ctx;
        private Route SelectedRoute;

        protected DirectionRequest(Context ctx, Route SelectedRoute) {
            this.ctx = ctx;
            this.SelectedRoute = SelectedRoute;
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

            String[] DirectionIDs;
            String[] DirectionTexts;

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                InputSource is = new InputSource(new StringReader(result));
                Document doc = dBuilder.parse(is);
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("TextValuePair");
                Log.d("NexTripWearable", Integer.toString(nList.getLength()));
                if(nList.getLength() == 2) {
                    DirectionIDs = new String[2];
                    DirectionTexts = new String[2];
                    for (int i = 0; i < nList.getLength(); i++) {
                        Node nNode = nList.item(i);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) nNode;
                            DirectionTexts[i] = eElement.getElementsByTagName("Text").item(0).getTextContent();
                            DirectionIDs[i] = eElement.getElementsByTagName("Value").item(0).getTextContent();
                        }
                    }
                    Log.d("NexTripWearable", "Creating List Adapter");

                    //Process Direction Results
                    //Load Intent accordingly
                    if(Integer.valueOf(SelectedRoute.getRouteID()) > 900) {
                        SelectedRoute.setImageID(R.drawable.lightrail);
                    }
                    else {
                        SelectedRoute.setImageID(R.drawable.bus);
                    }
                    Intent intent = SelectedRoute.BuildIntent(ctx, Direction.class);

                    intent.putExtra("DirectionTexts", DirectionTexts);
                    intent.putExtra("DirectionIDs", DirectionIDs);

                    startActivity(intent);
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
