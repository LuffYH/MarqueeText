package com.luffy.util.marqueetextview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author luffy
 */
@SuppressLint("AppCompatCustomView")
public class MarqueeTextView extends TextView {
    public static final int ALONE = 1;
    public static final int MULTIPLE = 2;
    public static final int VERTICAL = 1;
    public static final int HORIZONTAL = 2;
    /**
     * 获取的文字
     */
    private String text;
    /**
     * 需要绘制的文字
     */
    private String horizontalDrawText = "";
    /**
     * 需要绘制的文字
     */
    private List<String> verticalDrawText;
    private int verticalDrawTextSize;

    /**
     * 文本的颜色
     */
    private int textColor;
    /**
     * 文本的大小
     */
    private float textSize;
    /**
     * 移动的速度
     */
    private float textSpeed = 4f;
    /**
     * 控件宽
     */
    private int width;
    /**
     * 控件高
     */
    private int height;
    /**
     * 已经移动的像素
     */
    private float move = 0f;
    /**
     * 滚动模式
     */
    private int mode = ALONE;
    /**
     * 移动方向
     */
    private int direction;
    /**
     * 一段文字的宽度
     */
    private float moveMeasuredWidth;
    /**
     * 一段文字的长度
     */
    private float moveMeasuredHeight;
    /**
     * 每行字数
     */
    private int textNum = 10;
    /**
     * 滚动2时的间隔
     */
    private String horizontalStep = "     ";
    private List<String> verticalStep;
    /**
     * 炫彩特效
     */
    private int colorfulIndex;
    private ScheduledExecutorService ese;
    private Rect rect = new Rect();
    private Paint paint;
    private Context context;

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MarqueeTextView, 0, 0);
        height = typedArray.getInteger(R.styleable.MarqueeTextView_pixelHeight, -1);
        width = typedArray.getInteger(R.styleable.MarqueeTextView_pixelWidth, -1);
        direction = typedArray.getInteger(R.styleable.MarqueeTextView_direction, HORIZONTAL);

        textColor = getCurrentTextColor();
        textSize = getTextSize();

        paint = new Paint();
        paint.setTextSize(textSize);
        paint.setColor(textColor);

        text = getText().toString();
        setDrawText();

        paint.getTextBounds(horizontalDrawText, 0, horizontalDrawText.length(), rect);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (width == -1) {
            getDefaultWidth(widthMeasureSpec);
        }
        if (height == -1) {
            getDefaultHeight(heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
    }

    private void getDefaultWidth(int widthMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            float textWidth = rect.width();
            width = (int) (getPaddingLeft() + textWidth + getPaddingRight());
        }
    }

    private void getDefaultHeight(int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            float textHeight = rect.height();
            height = (int) (getPaddingTop() + textHeight + getPaddingBottom());
        }
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    public void setText(String marqueeText) {
        this.text = marqueeText;
        setDrawText();
    }

    public void setMode(int marqueeTextMode, int level) {
        this.mode = marqueeTextMode;
        if (mode == MULTIPLE) {
            StringBuilder stringBuilder = new StringBuilder();
            verticalStep = new ArrayList<>();
            for (int i = 0; i < level; i++) {
                stringBuilder.append("     ");
                verticalStep.add("");
            }
            horizontalStep = stringBuilder.toString();
        }
        setDrawText();
    }

    public void setSize(int marqueeTextSize) {
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marqueeTextSize, context.getResources().getDisplayMetrics());
        paint.setTextSize(textSize);
        setDrawText();
    }

    public void setColor(int marqueeTextColor) {
        textColor = marqueeTextColor;
        paint.setColor(marqueeTextColor);
    }

    public void startColorful(int[] color) {
        // 自定义颜色
        setColorful(color);
    }

    public void startColorful() {
        // 默认使用七彩
        int[] color = new int[]{Color.parseColor("#FF0000"),
                Color.parseColor("#FF6600"), Color.parseColor("#FFFF66"),
                Color.parseColor("#00CC00"), Color.parseColor("#669999"),
                Color.parseColor("#0066CC"), Color.parseColor("#990099"),
        };
        setColorful(color);
    }

    private void setColorful(final int[] color) {
        colorfulIndex = 0;
        ese = Executors.newSingleThreadScheduledExecutor();
        ese.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                paint.setColor(color[colorfulIndex]);
                colorfulIndex++;
                if (colorfulIndex == color.length) {
                    colorfulIndex = 0;
                }
            }
        }, 200, 200, TimeUnit.MILLISECONDS);
    }

    public void stopColorful() {
        if (ese != null) {
            ese.shutdown();
        }
        paint.setColor(textColor);
    }

    public void setFont(Typeface marqueeTextFont) {
        paint.setTypeface(marqueeTextFont);
        setDrawText();
    }

    public void setSpeed(float marqueeTextSpeed) {
        textSpeed = marqueeTextSpeed;
    }

    public void setTextNum(int num) {
        textNum = num;
        setDrawText();
    }

    private void setDrawText() {
        if (direction == VERTICAL) {
            List<String> verticalDrawTextCache = new ArrayList<>();
            int lenght = text.length();
            if (lenght > textNum) {
                int size;
                if (lenght % textNum > 0) {
                    size = lenght / textNum + 1;
                } else {
                    size = lenght / textNum;
                }
                for (int i = 0; i < size; i++) {
                    if (i == size - 1) {
                        verticalDrawTextCache.add(text.substring(i * textNum));
                    } else {
                        verticalDrawTextCache.add(text.substring(i * textNum, (i + 1) * textNum));
                    }
                }
            } else {
                verticalDrawTextCache.add(text);
            }
            paint.getTextBounds(text, 0, text.length(), rect);
            moveMeasuredWidth = paint.measureText(verticalDrawTextCache.get(0));
            moveMeasuredHeight = rect.height();
            verticalDrawText = new ArrayList<>();
            verticalDrawText.addAll(verticalDrawTextCache);
            if (mode == MULTIPLE) {
                verticalDrawTextCache.addAll(0, verticalStep);
                int stepNum = (int) (height / moveMeasuredHeight) + 1;
                for (int i = 0; i < stepNum; i++) {
                    verticalDrawText.addAll(verticalDrawTextCache);
                }
            }
            verticalDrawTextSize = verticalDrawTextCache.size();
        } else {
            if (mode == MULTIPLE) {
                String str = text + horizontalStep;
                moveMeasuredWidth = paint.measureText(str);
                int stepNum = (int) (width / moveMeasuredWidth) + 1;
                StringBuilder builder = new StringBuilder();
                builder.append(text);
                for (int i = 0; i < stepNum; i++) {
                    builder.append(horizontalStep).append(text);
                }
                horizontalDrawText = builder.toString();
            } else {
                horizontalDrawText = text;
            }
            paint.getTextBounds(horizontalDrawText, 0, horizontalDrawText.length(), rect);
        }
        System.gc();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (direction == VERTICAL) {
            float x = (getMeasuredWidth() - moveMeasuredWidth) / 2;
            int measuredHeight = getMeasuredHeight();
            for (int i = 0; i < verticalDrawText.size(); i++) {
                float y = measuredHeight + (i * moveMeasuredHeight) - move;
                canvas.drawText(verticalDrawText.get(i), x, y, paint);
            }
            move += textSpeed;
            if (mode == MULTIPLE) {
                if (move >= measuredHeight + moveMeasuredHeight * verticalDrawTextSize) {
                    move = measuredHeight;
                }
            } else {
                if (move >= measuredHeight + moveMeasuredHeight * verticalDrawTextSize) {
                    move = 0f;
                }
            }
        } else {
            int height = getHeight() / 2;
            int measuredWidth = getMeasuredWidth();
            float y = height + (-paint.ascent() + paint.descent()) / 2 - paint.descent();
            canvas.drawText(horizontalDrawText, measuredWidth - move, y, paint);
            move += textSpeed;
            if (mode == MULTIPLE) {
                if (move >= measuredWidth + moveMeasuredWidth) {
                    move = measuredWidth;
                }
            } else {
                if (move >= measuredWidth + rect.width()) {
                    move = 0f;
                }
            }
        }
        invalidate();
    }
}