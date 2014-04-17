package dining;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import casehub.MainActivity;

import com.casehub.R;

public class DiningFragment extends Fragment implements OnItemClickListener {

	private static View mView;
	private ListView list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mView != null) {
			ViewGroup parent = (ViewGroup) mView.getParent();
			if (parent != null)
				parent.removeView(mView);
		}
		try {
			mView = inflater.inflate(R.layout.fragment_dining, container, false);
		} catch (InflateException e) {
			/* is already there, just return view as it is */
		} finally {
			list = (ListView) mView.findViewById(R.id.diningListView);
			String[] diningList = getResources().getStringArray(R.array.dining_array);
			ArrayAdapter<String> adapter;
			adapter = new ArrayAdapter<String>(MainActivity.c ,android.R.layout.simple_list_item_1, diningList);
			list.setAdapter(adapter);
			list.setOnItemClickListener(this);
		}
		return mView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int num = 0;
		switch(position){
		case 0:
			num = 45;
			break;
		case 1:
			num = 43;
			break;
		case 2:
			num = 44;
			break;
		case 3:
			num = 42;
			break;
		case 4:
			num = 47;
			break;
		}
		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		args.putInt("ID", num);
		DiningPageFragment fragment = new DiningPageFragment();
		fragment.setArguments(args);
		ft.replace(R.id.content_frame, fragment);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.addToBackStack(null);
		ft.commit(); 
	}
}

