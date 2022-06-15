package com.example.touchdemo;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.touchdemo.view.ITouchViewListener;
import com.example.touchdemo.view.TouchView;

public class MainActivity extends AppCompatActivity {
    private int screenHeight;
    private FrameLayout imageLayout;
    private View confirmView;
    private TextView deleteTipsView;
    private FrameLayout moveLayout;
    private View addEmojiView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        screenHeight = Utils.getScreenHeight(this);
        initView();
    }

    private void initView() {
        imageLayout = findViewById(R.id.image_layout);
        confirmView = findViewById(R.id.confirm_view);
        deleteTipsView = findViewById(R.id.delete_tips_view);
        moveLayout = findViewById(R.id.move_layout);
        addEmojiView = findViewById(R.id.add_emoji_view);
        addEmojiView.setOnClickListener(view -> addEmojiView());
        addEmojiView.performClick();
    }

    // 添加Emoji
    private void addEmojiView() {
        TouchView emojiView = new TouchView(this);
        emojiView.setImageResource(R.mipmap.post_good_2);
        emojiView.setTouchViewListener(new ITouchViewListener() {
            @Override
            public void onTouch(boolean touch) {
                confirmView.setVisibility(touch ? View.GONE : View.VISIBLE);
                deleteTipsView.setVisibility(touch ? View.VISIBLE : View.GONE);
                if (touch) {
                    ((ViewGroup) emojiView.getParent()).removeView(emojiView);
                    moveLayout.addView(emojiView);
                } else {
                    if (emojiView.getAlpha() < 1) {
                        ((ViewGroup) emojiView.getParent()).removeView(emojiView);
                    } else {
                        ((ViewGroup) emojiView.getParent()).removeView(emojiView);
                        imageLayout.addView(emojiView);
                        emojiView.resetMargins();
                    }
                }
            }

            @Override
            public void onMove(int offsetY) {
                if (screenHeight - offsetY > Utils.dp2px(50)) {
                    emojiView.setAlpha(1f);
                    deleteTipsView.setText("拖动到此处删除");
                } else {
                    emojiView.setAlpha(0.5f);
                    deleteTipsView.setText("松手即可删除");
                }
            }
        });

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) Utils.dp2px(100), (int) Utils.dp2px(100), Gravity.CENTER);
        imageLayout.addView(emojiView, lp);
    }
}