package gppmds.wikilegis.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import gppmds.wikilegis.R;
import gppmds.wikilegis.model.Segment;

public class ViewSegmentFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_view_segment, container, false);
    }

    public Segment findSegment(List<Segment> segmentList, int idSegment , int billIndex){
        Segment segment =null;
        for(int index = 0 ; index <segmentList.size(); index++) {
            if(segmentList.get(index).getBill()==billIndex && segmentList.get(index).getId()==idSegment){

                segment = segmentList.get(index);

            }
        }
        return segment;
    }
}
