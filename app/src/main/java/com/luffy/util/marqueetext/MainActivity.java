package com.luffy.util.marqueetext;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    MarqueeTextView marqueeTextView;
    Button changeText;
    Button changeMode;
    Button changeSpeed;
    Button changeInterval;
    Button changeFont;
    Button changeColor;
    Button setColorful;

    int mode = 1;
    int font = 1;
    boolean isColorful;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        marqueeTextView = findViewById(R.id.mt);
        changeText = findViewById(R.id.changeText);
        changeMode = findViewById(R.id.changeMode);
        changeSpeed = findViewById(R.id.changeSpeed);
        changeInterval = findViewById(R.id.changeInterval);
        changeFont = findViewById(R.id.changeFont);
        changeColor = findViewById(R.id.changeColor);
        setColorful = findViewById(R.id.setColorful);
        changeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random random = new Random();
                int num = random.nextInt(30);
                marqueeTextView.setSize(num);
                marqueeTextView.setText("测试修改文本属性" + num);
            }
        });
        changeFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (font == 1) {
                    font = 2;
                    marqueeTextView.setFont(Typeface.createFromAsset(getAssets(), "fonts/huawen.ttf"));
                } else {
                    font = 1;
                    marqueeTextView.setFont(Typeface.create("宋体", Typeface.BOLD_ITALIC));
                }
            }
        });
        changeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random random = new Random();
                int num = random.nextInt(5);
                switch (num) {
                    case 0:
                        marqueeTextView.setColor(Color.WHITE);
                        break;
                    case 1:
                        marqueeTextView.setColor(Color.BLACK);
                        break;
                    case 2:
                        marqueeTextView.setColor(Color.BLUE);
                        break;
                    case 3:
                        marqueeTextView.setColor(Color.GREEN);
                        break;
                    case 4:
                        marqueeTextView.setColor(Color.YELLOW);
                        break;
                    case 5:
                        marqueeTextView.setColor(Color.GRAY);
                        break;
                    default:
                        break;
                }
            }
        });
        setColorful.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isColorful) {
                    marqueeTextView.startColorful();
                    isColorful = true;
                } else {
                    marqueeTextView.stopColorful();
                    isColorful = false;
                }
            }
        });
        changeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == 1) {
                    mode = 2;
                    marqueeTextView.setMode(mode);
                } else {
                    mode = 1;
                    marqueeTextView.setMode(mode);
                }
            }
        });
        changeSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random random = new Random();
                int num = random.nextInt(7);
                marqueeTextView.setSpeed(num);
            }
        });
        changeInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random random = new Random();
                int num = random.nextInt(5);
                marqueeTextView.setStep(num);
            }
        });
    }
}
