package donoris.donoris.Video_Post;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import donoris.donoris.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FileVideoPostFragment extends Fragment {


    public FileVideoPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_file_video_post_layout, container, false);
    }

}
