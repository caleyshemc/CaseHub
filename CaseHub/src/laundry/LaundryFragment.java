package laundry;

import java.util.concurrent.ExecutionException;

import com.casehub.R;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LaundryFragment extends Fragment {
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_laundry, container, false);
    }
    
    /*
	 * TODO 
	 * -- Query for list of houses when first opened 
	 * -- Open to last-viewed house 
	 * -- Query for specific house only when refresh button hit or new
	 * house selected in drop-down
	 */
    
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
	
		/*
		String esudsHTML = "";
		
		try {
			esudsHTML = new FetchLaundryTask().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.d("LAUNDRY", esudsHTML);
		*/
	}
    
}

