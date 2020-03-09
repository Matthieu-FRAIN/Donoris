package donoris.donoris.Post;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import donoris.donoris.Image_Post.ImagePostActivity;
import donoris.donoris.MainActivity;
import donoris.donoris.R;
import donoris.donoris.Text_Post.TextePostActivity;
import donoris.donoris.Video_Post.VideoPostActivity;

public class PostActivity extends AppCompatActivity
{
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        mToolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar() .setDisplayHomeAsUpEnabled(true);
        getSupportActionBar() .setDisplayShowHomeEnabled(true);
        getSupportActionBar() .setTitle("Nouvelle publication");



        final Button dtextUpadtePostPage = (Button) findViewById(R.id.dtexte_update_post_page);
        dtextUpadtePostPage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostActivity.this, TextePostActivity.class);
                startActivity(intent);
            }
        });

        final Button dimage_update_post_page = (Button) findViewById(R.id.dimage_update_post_page);
        dimage_update_post_page.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostActivity.this, ImagePostActivity.class);
                startActivity(intent);
            }
        });

        final Button dvideo_update_post_page = (Button) findViewById(R.id.dvideo_update_post_page);
        dvideo_update_post_page.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostActivity.this, VideoPostActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == android.R.id.home)
        {
            SendUserToMainActiviy();
        }

        return super.onOptionsItemSelected(item);
    }




    private void SendUserToMainActiviy()
    {
        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }
}


