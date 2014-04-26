package map;

import map.CaseMap.Point;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.casehub.R;

//Displays details of a point
public class DetailViewFragment extends Fragment implements OnClickListener {

	private View mView;
	private Point point;

	public void setPoint(Point point) {
		this.point = point;
	}

	public DetailViewFragment() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView != null) {
			ViewGroup parent = (ViewGroup) mView.getParent();
			if (parent != null)
				parent.removeView(mView);
		}
		try {
			mView = inflater.inflate(R.layout.fragment_point_detail, container, false);
		} catch (InflateException e) {
			/* is already there, just return view as it is */
		} finally {
			TextView name = (TextView) mView.findViewById(R.id.name);
			name.setText(point.getName());
			TextView address = (TextView) mView.findViewById(R.id.address);
			address.setText(point.getAddress());
			TextView extranames = (TextView) mView.findViewById(R.id.extra_names);
			if(point.getExtraNames() != null){
				extranames.setText(point.getExtraNames().toString().replace("[", "").replace("]", ""));
			}else{
				extranames.setText("None");
			}
			TextView entities = (TextView) mView.findViewById(R.id.entities);
			if(point.getEntities().size() == 0){
				entities.setText(point.getEntities().toString().replace("[", "").replace("]", ""));
			}else{
				entities.setText("N/A");
			}
			TextView category = (TextView) mView.findViewById(R.id.type);
			String[] categories = getResources().getStringArray(R.array.maptypearray);
			category.setText(categories[point.getTypeId()]);
			Button getdir = (Button) mView.findViewById(R.id.getdir);
			getdir.setOnClickListener(this);
		}
		return mView;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.getdir){
			String uri = "https://maps.google.com/maps?f=d&daddr=" + point.getCoord().latitude + "," + point.getCoord().longitude;        
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			startActivity(i);
		}
	}
}
