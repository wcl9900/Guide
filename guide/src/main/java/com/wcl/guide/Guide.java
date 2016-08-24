package com.wcl.guide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.wcl.guide.model.GuideModel;
import com.wcl.guide.view.GuideView;

import java.util.ArrayList;
import java.util.List;

/**
 *<p>Describe:引导视图创建
 *<p>Author:王春龙
 *<p>CreateTime:2016/7/4
*/
public class Guide
{
    public static class ViewPosInfo
    {
        public static int TYPE_TIP = 1;//指示类型
        public static int TYPE_CONTROL = 2;//控制类型
        public static int TYPE_MARK = 3;//标记类型

        public int type;
        public int layoutId = -1;
        public RectF rectF;
        public LayoutParams layoutParams;
        public View view;
        public OnPosCallback onPosCallback;
        public View.OnClickListener onClickListener;
        public Bitmap markBitmap;
    }

    public static class LayoutParams
    {
        public int width;
        public int height;

        public float topMargin;
        public float leftMargin;
        public float rightMargin;
        public float bottomMargin;
    }

    public interface OnPosCallback
    {
        void getPos(float rightMargin, float bottomMargin, RectF rectF, LayoutParams layoutParams);
    }

    public interface OnClickCallback
    {
        void onClick();
    }

    private View mAnchor;
    private List<ViewPosInfo> mViewRects;
    private List<GuideModel> mGuideModelList;
    private Context mContext;
    private ViewFlipper mHighLightViewFlipper;
    private OnClickCallback clickCallback = null;

    private Animation animEnter;
    private Animation animExit;

    private boolean intercept = true;
    private boolean shadow = true;
    private int maskColor = 0xAA000000;

    private boolean buildEnd = false;

    public Guide(Context context)
    {
        mContext = context;
        mGuideModelList = new ArrayList<GuideModel>();
        mViewRects = new ArrayList<ViewPosInfo>();
        mAnchor = ((Activity) mContext).findViewById(android.R.id.content);
    }

    /**
     * 创建一页引导
     * @return
     */
    public GuideModel next(){
        if(buildEnd) {
            throw new IllegalArgumentException("引导创建已终止,不能再创建!");
        }
        buildEnd = false;
        GuideModel guideModel = new GuideModel(this, mAnchor, mContext);
        mGuideModelList.add(guideModel);
        return guideModel;
    }

    /**
     * 结束引导创建
     * @return
     */
    public Guide end(){
        this.buildEnd = true;
        return this;
    }

    public Guide anchor(View anchor)
    {
        mAnchor = anchor;
        return this;
    }

    public Guide intercept(boolean intercept)
    {
        this.intercept = intercept;
        return this;
    }

    public Guide shadow(boolean shadow)
    {
        this.shadow = shadow;
        return this;
    }

    public boolean isShadow() {
        return shadow;
    }

    public Guide maskColor(int maskColor)
    {
        this.maskColor = maskColor;
        return this;
    }

    public int getMaskColor() {
        return maskColor;
    }

    public Guide animIn(Animation animation){
        animEnter = animation;
        return this;
    }
    public Guide animIn(int resourceId){
        animEnter = AnimationUtils.loadAnimation(mContext, resourceId);
        return this;
    }

    public Guide animOut(Animation animation){
        animExit = animation;
        return this;
    }
    public Guide animOut(int resourceId){
        animExit = AnimationUtils.loadAnimation(mContext, resourceId);
        return this;
    }

    /**
     * 显示下一页引导
     */
    public void showNext(){
        int display = mHighLightViewFlipper.getDisplayedChild();
        if(mHighLightViewFlipper.getChildCount() - 1 > display){
            mHighLightViewFlipper.setDisplayedChild(display + 1);
        }
    }
    /**
     * 显示上一页引导
     */
    public void showPre(){
        int display = mHighLightViewFlipper.getDisplayedChild();
        if(display > 0){
            mHighLightViewFlipper.setDisplayedChild(display - 1);
        }
    }

    public void show()
    {
        if (mHighLightViewFlipper != null) return;

        ViewFlipper highLightViewFlipper = new ViewFlipper(mContext);
        if(animEnter != null){
            highLightViewFlipper.setInAnimation(animEnter);
        }
        if(animExit != null){
            highLightViewFlipper.setOutAnimation(animExit);
        }
        ViewFlipper.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        for (GuideModel guideModel : mGuideModelList){
            GuideView guideView = new GuideView(mContext, this, guideModel);
            highLightViewFlipper.addView(guideView, layoutParams);
        }
        if (mAnchor.getClass().getSimpleName().equals("FrameLayout"))
        {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ((ViewGroup) mAnchor).addView(highLightViewFlipper, ((ViewGroup) mAnchor).getChildCount(), lp);

        } else
        {
            FrameLayout frameLayout = new FrameLayout(mContext);
            ViewGroup parent = (ViewGroup) mAnchor.getParent();
            parent.removeView(mAnchor);
            parent.addView(frameLayout, mAnchor.getLayoutParams());
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            frameLayout.addView(mAnchor, lp);

            frameLayout.addView(highLightViewFlipper);
        }

        highLightViewFlipper.setClickable(true);
        if (intercept)
        {
            highLightViewFlipper.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    remove();
                    if(clickCallback != null){
                        clickCallback.onClick();
                    }
                }
            });
        }

        mHighLightViewFlipper = highLightViewFlipper;
    }

    public void remove()
    {
        if (mHighLightViewFlipper == null) return;
        ViewGroup parent = (ViewGroup) mHighLightViewFlipper.getParent();
        if (parent instanceof RelativeLayout || parent instanceof FrameLayout)
        {
            parent.removeView(mHighLightViewFlipper);
        } else
        {
            parent.removeView(mHighLightViewFlipper);
            View origin = parent.getChildAt(0);
            ViewGroup graParent = (ViewGroup) parent.getParent();
            graParent.removeView(parent);
            graParent.addView(origin, parent.getLayoutParams());
        }
        mHighLightViewFlipper = null;
    }
}
