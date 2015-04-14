/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.shyam.popi;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Demonstrates a "screen-slide" animation using a {@link android.support.v4.view.ViewPager}. Because {@link android.support.v4.view.ViewPager}
 * automatically plays such an animation when calling {@link android.support.v4.view.ViewPager#setCurrentItem(int)}, there
 * isn't any animation-specific code in this sample.
 *
 * <p>This sample shows a "next" button that advances the user to the next step in a wizard,
 * animating the current screen out (to the left) and the next screen in (from the right). The
 * reverse animation is played when the user presses the "previous" button.</p>
 *
 * @see ScreenSlidePageFragment
 */
public class LearnEmotions extends FragmentActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 4;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
    String[] emotions;
    int[] smiley;
    int[] audio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_screen_slide);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        emotions = new String[]{"Smile","Sad","Angry","Tired"};
        smiley  = new int[]{R.drawable.smile,R.drawable.sad,R.drawable.angry,R.drawable.tired};
        audio = new int[]{R.raw.laugh,R.raw.sad_effect,R.raw.angry_effect,R.raw.tired_effect};
        mPagerAdapter = new ScreenSlidePagerAdapter(getApplicationContext(),emotions,smiley,audio);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        getMenuInflater().inflate(R.menu.activity_screen_slide, menu);

        menu.findItem(R.id.action_previous).setEnabled(mPager.getCurrentItem() > 0);

        // Add either a "next" or "finish" button to the action bar, depending on which page
        // is currently selected.
        MenuItem item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE,
                (mPager.getCurrentItem() == mPagerAdapter.getCount() - 1)
                        ? R.string.action_finish
                        : R.string.action_next);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;

            case R.id.action_previous:
                // Go to the previous step in the wizard. If there is no previous step,
                // setCurrentItem will do nothing.
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                return true;

            case R.id.action_next:
                // Advance to the next step in the wizard. If there is no next step, setCurrentItem
                // will do nothing.
                if(item.getTitle()==getResources().getString(R.string.action_finish)){
                    Toast.makeText(getApplicationContext(),"You are finished",Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            NavUtils.navigateUpTo(LearnEmotions.this, new Intent(LearnEmotions.this, MainActivity.class));
                        }
                    }, 3000);
                }
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment} objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends PagerAdapter {
        String[] emotions;
        int[] smiley;
        int[] audio;
        Context ctx;
        LayoutInflater inflater;
        MediaPlayer mp;
        public ScreenSlidePagerAdapter(Context context,String[] emotions,int[] smiley,int[] audio) {
            //super(fm);
            this.ctx = context;
            this.emotions = emotions;
            this.smiley = smiley;
            this.audio = audio;
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            // Declare Variables
            TextView emotion;
            ImageView imgsmiley;
            Button playEmotions;
            inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.fragment_screen_slide_page, container,
                    false);

            // Locate the TextViews in viewpager_item.xml
            emotion = (TextView) itemView.findViewById(R.id.emotion);

            // Capture position and set to the TextViews
            emotion.setText(emotions[position]);


            // Locate the ImageView in viewpager_item.xml
            imgsmiley = (ImageView) itemView.findViewById(R.id.smiley);
            // Capture position and set to the ImageView
            imgsmiley.setImageResource(smiley[position]);

            playEmotions = (Button) itemView.findViewById(R.id.play);
            if(audio[position]==0){
                playEmotions.setVisibility(View.INVISIBLE);
            }else {
                playEmotions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        if(currentVolume==0)
                            Toast.makeText(getApplicationContext(),R.string.turnvolume,Toast.LENGTH_LONG).show();

                        try {
                        mp = MediaPlayer.create(getApplicationContext(), audio[position]);
                        if(mp.isPlaying())
                            mp.stop();
                        mp.start();

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
            // Add viewpager_item.xml to ViewPager
            ((ViewPager) container).addView(itemView);

            return itemView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // Remove viewpager_item.xml from ViewPager
            ((ViewPager) container).removeView((LinearLayout) object);

        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
