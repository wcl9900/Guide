package com.wcl.guide.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wcl.guide.Guide;
import com.wcl.guide.model.GuideModel;

import java.util.List;

/**
 *<p>Describe:引导视图
 *<p>Author:王春龙
 *<p>CreateTime:2016/7/4
*/
public class GuideView extends FrameLayout
{
    private static final int DEFAULT_WIDTH_BLUR = 15;
    private static final int DEFAULT_RADIUS = 6;
    private static final PorterDuffXfermode MODE_DST_OUT = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

    private Bitmap mMaskBitmap;
    private Paint mPaint;
    private List<Guide.ViewPosInfo> mViewRects;
    private Guide mGuide;
    private LayoutInflater mInflater;
    private GuideModel mGuideModel;

    //some config
    private boolean isBlur = true;
    private int maskColor = 0xCC000000;


    public GuideView(Context context, Guide guide, GuideModel guideModel)
    {
        super(context);
        mGuideModel = guideModel;
        mGuide = guide;
        mInflater = LayoutInflater.from(context);
        mViewRects = mGuideModel.getmViewRects();
        this.maskColor = mGuideModel.getMaskColor();
        this.isBlur = mGuideModel.isShadow();
        setWillNotDraw(false);
        init();
    }

    private void init()
    {
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        if (isBlur)
            mPaint.setMaskFilter(new BlurMaskFilter(DEFAULT_WIDTH_BLUR, BlurMaskFilter.Blur.SOLID));
        mPaint.setStyle(Paint.Style.FILL);

        addViewForTip();
    }

    private void setAllClickListener(View view, OnClickListener clickListener){
        if (view == null || clickListener == null) return;
        view.setOnClickListener(clickListener);
        if(view instanceof ViewGroup){
            int childCount = ((ViewGroup)view).getChildCount();
            for (int i = 0; i < childCount; i++){
                setAllClickListener(((ViewGroup)view).getChildAt(i), clickListener);
            }
        }
    }

    private void addViewForTip()
    {
        for (Guide.ViewPosInfo viewPosInfo : mViewRects)
        {
            if(viewPosInfo.layoutId == -1) continue;

            View view;
            LayoutParams lp;

            view = mInflater.inflate(viewPosInfo.layoutId, this, false);
            lp = buildTipLayoutParams(view, viewPosInfo);

            if(viewPosInfo.type == Guide.ViewPosInfo.TYPE_CONTROL){
                setAllClickListener(view, viewPosInfo.onClickListener);
            }

            if (lp == null) continue;

            if(viewPosInfo.type == Guide.ViewPosInfo.TYPE_MARK){
                lp.width = viewPosInfo.layoutParams.width;
                lp.height = viewPosInfo.layoutParams.height;
            }

            lp.leftMargin = (int) viewPosInfo.layoutParams.leftMargin;
            lp.topMargin = (int) viewPosInfo.layoutParams.topMargin;
            lp.rightMargin = (int) viewPosInfo.layoutParams.rightMargin;
            lp.bottomMargin = (int) viewPosInfo.layoutParams.bottomMargin;

            fixGravity(lp);

            addView(view, lp);
        }
    }

    private void fixGravity(LayoutParams lp) {
        if(lp.rightMargin != 0){
            lp.gravity = Gravity.RIGHT;
        }else {
            lp.gravity = Gravity.LEFT;
        }

        if(lp.bottomMargin != 0){
            lp.gravity |= Gravity.BOTTOM;
        }else {
            lp.gravity |= Gravity.TOP;
        }
    }

    private void buildMask()
    {
        mMaskBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mMaskBitmap);
        canvas.drawColor(maskColor);
        mPaint.setXfermode(MODE_DST_OUT);
        mGuideModel.updateInfo();
        for (Guide.ViewPosInfo viewPosInfo : mViewRects)
        {
            if(viewPosInfo.type == Guide.ViewPosInfo.TYPE_TIP) {
                canvas.drawRoundRect(viewPosInfo.rectF, DEFAULT_RADIUS, DEFAULT_RADIUS, mPaint);
            }
            else if(viewPosInfo.type == Guide.ViewPosInfo.TYPE_MARK && viewPosInfo.markBitmap !=null){
                Rect rect = new Rect();
                rect.set((int)viewPosInfo.rectF.left, (int)viewPosInfo.rectF.top, (int)viewPosInfo.rectF.right, (int)viewPosInfo.rectF.bottom);
                canvas.drawBitmap(viewPosInfo.markBitmap, null, rect, null);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),//
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        if (changed)
        {
            buildMask();
            updateTipPos();
        }
    }

    private void updateTipPos()
    {
        for (int i = 0, n = getChildCount(); i < n; i++)
        {
            View view = getChildAt(i);
            Guide.ViewPosInfo viewPosInfo = mViewRects.get(i);

            LayoutParams lp = buildTipLayoutParams(view, viewPosInfo);
            if (lp == null) continue;
            view.setLayoutParams(lp);
        }
    }

    private LayoutParams buildTipLayoutParams(View view, Guide.ViewPosInfo viewPosInfo)
    {
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        if(lp == null) return null;

        if (lp.leftMargin == (int) viewPosInfo.layoutParams.leftMargin &&
                lp.topMargin == (int) viewPosInfo.layoutParams.topMargin &&
                lp.rightMargin == (int) viewPosInfo.layoutParams.rightMargin &&
                lp.bottomMargin == (int) viewPosInfo.layoutParams.bottomMargin) return lp;

        lp.leftMargin = (int) viewPosInfo.layoutParams.leftMargin;
        lp.topMargin = (int) viewPosInfo.layoutParams.topMargin;
        lp.rightMargin = (int) viewPosInfo.layoutParams.rightMargin;
        lp.bottomMargin = (int) viewPosInfo.layoutParams.bottomMargin;

        fixGravity(lp);
        return lp;
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawBitmap(mMaskBitmap, 0, 0, null);
        super.onDraw(canvas);
    }
}
