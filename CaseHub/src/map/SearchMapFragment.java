package map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import casehub.MainActivity;
import casehub.CaseHubContract.CampusMapPoint;

import com.casehub.R;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SearchMapFragment extends Fragment implements OnClickListener, OnItemClickListener {

	private static View mView;
	
	public SearchMapFragment(){
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView != null) {
			ViewGroup parent = (ViewGroup) mView.getParent();
			if (parent != null)
				parent.removeView(mView);
		}
		try {
			mView = inflater.inflate(R.layout.fragment_searchmap, container, false);
		} catch (InflateException e) {
			/* is already there, just return view as it is */
		} finally {
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			EditText searchBox = (EditText) mView.findViewById(R.id.searchBox);
			Button searchButton = (Button) mView.findViewById(R.id.searchButton);
			String query = getArguments().getString("query");
			if(query != null){
				searchBox.setText(query);
				onClick(searchButton);
				getArguments().remove("query");
			}
			searchButton.setOnClickListener(this);
		}
		return mView;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.searchButton){
			EditText searchBox = (EditText) mView.findViewById(R.id.searchBox);
			String[] searchText = searchBox.getText().toString().split(" ");
			SQLiteDatabase db = MainActivity.mDbHelper.getWritableDatabase();
			String[] pProjection = {CampusMapPoint.COL_ADDRESS, 
					CampusMapPoint.COL_ENTITIES,
					CampusMapPoint.COL_EXTRA_NAMES,
					CampusMapPoint.COL_IMAGE,
					CampusMapPoint.COL_LAT,
					CampusMapPoint.COL_LNG,
					CampusMapPoint.COL_LDAP,
					CampusMapPoint.COL_NAME,
					CampusMapPoint.COL_NUM,
					CampusMapPoint.COL_SIS,
					CampusMapPoint.COL_TYPE_ID,
					CampusMapPoint.COL_URL,
					CampusMapPoint.COL_ZONE
			};
			StringBuilder queryString = new StringBuilder();
			for(int i = 0; i < searchText.length; i++){
				queryString.append(CampusMapPoint.COL_ADDRESS + " LIKE '%" + searchText[i] + "%' OR ");
				queryString.append(CampusMapPoint.COL_EXTRA_NAMES + " LIKE '%" + searchText[i] + "%' OR ");
				queryString.append(CampusMapPoint.COL_LDAP + " LIKE '%" + searchText[i] + "%' OR ");
				queryString.append(CampusMapPoint.COL_NAME + " LIKE '%" + searchText[i] + "%' OR ");
				queryString.append(CampusMapPoint.COL_SIS + " LIKE '%" + searchText[i] + "%'");
				if((searchText.length > 1) && (i != searchText.length - 1)){
					queryString.append(" OR ");
				}
			}
			Cursor c = db.query(CampusMapPoint.TABLE_NAME, pProjection, queryString.toString(), null, null, null, CampusMapPoint.COL_NAME);
			
			if(!c.moveToFirst()){
				//cursor is empty
			}
			
			int name_index = c.getColumnIndexOrThrow(CampusMapPoint.COL_NAME);
			int addr_index = c.getColumnIndexOrThrow(CampusMapPoint.COL_ADDRESS);
			
			List<Map<String, String>> data = new ArrayList<Map<String, String>>();
			do{
				Map<String, String> point = new HashMap<String, String>(2);
				point.put("name", c.getString(name_index));
				point.put("address", c.getString(addr_index));
				data.add(point);
			}while(c.moveToNext());
			
			SimpleAdapter adapter = new SimpleAdapter(MainActivity.c, data,
                    android.R.layout.simple_list_item_2,
                    new String[] {"name", "address"},
                    new int[] {android.R.id.text1,
                               android.R.id.text2});
			ListView resultsList = (ListView) mView.findViewById(R.id.searchResults);
			resultsList.setOnItemClickListener(this);
			resultsList.setAdapter(adapter);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(parent.getId() == R.id.searchResults){
			Map<String, String> selectedItem = (Map<String, String>) parent.getItemAtPosition(position);
			String selected = selectedItem.get("name");
			FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
			CampusMapFragment cmFragment = (CampusMapFragment) getActivity().getFragmentManager().findFragmentByTag("campus_map_fragment");
			cmFragment.setIncoming(selected);
			ft.replace(R.id.content_frame, cmFragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.addToBackStack(null);
			InputMethodManager inputManager = (InputMethodManager) MainActivity.c.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(mView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			ft.commit();
		}
		
	}

}
