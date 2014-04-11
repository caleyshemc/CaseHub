package casehub;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import greenie.GreenieFragment;
import laundry.LaundryFragment;
import map.CampusMapFragment;
import schedule.ParseScheduleTask;
import schedule.ScheduleEvent;
import schedule.ScheduleFragment;
import schedule.LoginDialogFragment;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.casehub.R;

import dining.DiningFragment;

/**
 * MainActivity functions as app Controller, defines navigation drawer.
 */
public class MainActivity extends Activity implements
		LoginDialogFragment.OnLoginListener {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mDrawerTitles;

	public static Context c;
	public static CaseHubDbHelper mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		c = this;
		mDbHelper = new CaseHubDbHelper(c);

		mTitle = mDrawerTitle = getTitle();
		mDrawerTitles = getResources().getStringArray(R.array.titles_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.navigation_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mDrawerTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		/*
		 * boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		 * menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		 */
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		/*
		 * TODO // Handle action buttons switch(item.getItemId()) { case
		 * R.id.action_websearch: // create intent to perform web search for
		 * this planet Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		 * intent.putExtra(SearchManager.QUERY, getActionBar().getTitle()); //
		 * catch event that there's no activity to handle intent if
		 * (intent.resolveActivity(getPackageManager()) != null) {
		 * startActivity(intent); } else { //Toast.makeText(this,
		 * R.string.app_not_available, Toast.LENGTH_LONG).show(); } return true;
		 * default: return super.onOptionsItemSelected(item); }
		 */
		return super.onOptionsItemSelected(item);
	}

	/* The click listener for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	/**
	 * Update the main content by replacing fragments
	 */
	private void selectItem(int position) {

		FragmentManager fragmentManager = getFragmentManager();
		Fragment fragment = null;
		String tag = "";

		switch (position) {
		case 0:
			/*
			 * fragment = new GreenieFragment(); tag = "greenie_fragment";
			 * break;
			 */
		case 1:
			fragment = new LaundryFragment();
			tag = "laundry_fragment";
			break;
		case 2:
			fragment = new ScheduleFragment();
			tag = "schedule_fragment";
			break;
		case 3:
			fragment = new CampusMapFragment();
			tag = "campus_map_fragment";
			break;
		case 4:
			fragment = new DiningFragment();
			tag = "dining_fragment";
			break;

		default:
			break;
		}

		if (fragment != null) {

			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment, tag).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			setTitle(mDrawerTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);

		} else {
			// TODO error in creating fragment
			// Log.e("MainActivity", "Error in creating fragment");
		}

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * When user logs into Scheduler, pass data to ScheduleFragment.
	 */
	@Override
	public void onScheduleLogin(String html) {

		// Parse HTML
		ArrayList<ScheduleEvent> events = new ArrayList<ScheduleEvent>();
		try {
			events = new ParseScheduleTask().execute(html).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Find ScheduleFragment to send results to
		ScheduleFragment schedFrag = (ScheduleFragment) getFragmentManager()
				.findFragmentByTag("schedule_fragment");

		// Save and display each event
		for (ScheduleEvent event : events) {
			schedFrag.addEvent(event);
			schedFrag.displayEvent(event);
		}

	}

}