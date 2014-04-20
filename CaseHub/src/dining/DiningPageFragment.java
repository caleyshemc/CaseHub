package dining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import casehub.MainActivity;

import com.casehub.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class DiningPageFragment extends Fragment {
	private static View mView;
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader = new ArrayList<String>();
    LinkedHashMap<String, List<String>> listDataChild = new LinkedHashMap<String, List<String>>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView != null) {
			ViewGroup parent = (ViewGroup) mView.getParent();
			if (parent != null)
				parent.removeView(mView);
		}
		try {
			mView = inflater.inflate(R.layout.fragment_dining_page, container, false);
		} catch (InflateException e) {
			/* is already there, just return view as it is */
		} finally {
			expListView = (ExpandableListView) mView.findViewById(R.id.pageList);
	        //prepareListData();
			Bundle args = getArguments();
			loadFeed(args.getInt("ID"));
			listDataHeader.addAll(listDataChild.keySet());
	        listAdapter = new ExpandableListAdapter(MainActivity.c, listDataHeader, listDataChild);
	        expListView.setAdapter(listAdapter);
		}
		return mView;
	}
	
	public void loadFeed(int id){
		try {
			listDataChild = new RSSParseTask().execute("http://legacy.cafebonappetit.com/rss/menu/" + id).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
