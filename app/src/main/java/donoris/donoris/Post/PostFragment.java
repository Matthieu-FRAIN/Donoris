package donoris.donoris.Post;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import donoris.donoris.Image_Post.ImagePostActivity;
import donoris.donoris.R;
import donoris.donoris.Text_Post.TextePostActivity;
import donoris.donoris.Video_Post.VideoPostActivity;

public class PostFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post,container,false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button btnDTexte = (Button) view.findViewById(R.id.dtexte_update_post_page);

        btnDTexte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), TextePostActivity.class));
            }
        });

        Button btnDImage = (Button) view.findViewById(R.id.dimage_update_post_page);

        btnDImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ImagePostActivity.class));
            }
        });

        Button btnDVideo = (Button) view.findViewById(R.id.dvideo_update_post_page);

        btnDVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), VideoPostActivity.class));
            }
        });
    }
}




