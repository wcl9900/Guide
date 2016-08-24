package com.wcl.guide.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;

import com.wcl.guide.Guide;
import com.wcl.guide.Guide.LayoutParams;
import com.wcl.guide.Guide.OnPosCallback;
import com.wcl.guide.Guide.ViewPosInfo;
import com.wcl.guide.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Describe:引导创建模型,每一个模型创建一个引导页
 * <p>Author:王春龙
 * <p>CreateTime:2016/7/4
 */
public class GuideModel {
    private Guide mGuide;
    private View mAnchor;
    private List<ViewPosInfo> mViewRects;
    private Context mContext;

    private boolean shadow = true;
    private int maskColor = 0xCC000000;

    public GuideModel(Guide guide, View anchor, Context context){
        mGuide = guide;
        mAnchor = anchor;
        mContext = context;
        mViewRects = new ArrayList<ViewPosInfo>();
        maskColor(guide.getMaskColor());
    }

    public GuideModel shadow(boolean shadow)
    {
        this.shadow = shadow & mGuide.isShadow();
        return this;
    }

    public GuideModel maskColor(int maskColor)
    {
        this.maskColor = maskColor;
        return this;
    }

    public List<ViewPosInfo> getmViewRects() {
        return mViewRects;
    }

    public boolean isShadow() {
        return shadow;
    }

    public int getMaskColor() {
        return maskColor;
    }

    public void updateInfo()
    {
        ViewGroup parent = (ViewGroup) mAnchor;
        for (ViewPosInfo viewPosInfo : mViewRects)
        {

            RectF rect = null;
            if(viewPosInfo.type == ViewPosInfo.TYPE_TIP || viewPosInfo.type == ViewPosInfo.TYPE_MARK){
                rect = new RectF(ViewUtils.getLocationInView(parent, viewPosInfo.view));
            }
            else if (viewPosInfo.type == ViewPosInfo.TYPE_CONTROL){
                rect = viewPosInfo.rectF;
            }

            viewPosInfo.rectF = rect;
            viewPosInfo.onPosCallback.getPos(parent.getWidth() - rect.right, parent.getHeight() - rect.bottom, rect, viewPosInfo.layoutParams);

        }
    }

    /**
     * 加入高亮引导
     * @param viewId 需要引导的视图id
     * @param decorLayoutId 引导视图布局资源
     * @param onPosCallback 引导视图位置回调
     * @return
     */
    public GuideModel addHighLight(int viewId, int decorLayoutId, OnPosCallback onPosCallback)
    {
        ViewGroup parent = (ViewGroup) mAnchor;
        View view = parent.findViewById(viewId);
        addHighLight(view, decorLayoutId, onPosCallback);
        return this;
    }

    /**
     * 加入高亮引导
     * @param view 需要引导的视图
     * @param decorLayoutId 引导视图布局资源
     * @param onPosCallback 引导视图位置回调
     * @return
     */
    public GuideModel addHighLight(View view, int decorLayoutId, OnPosCallback onPosCallback)
    {
        ViewGroup parent = (ViewGroup) mAnchor;
        RectF rect = new RectF(ViewUtils.getLocationInView(parent, view));
        ViewPosInfo viewPosInfo = new ViewPosInfo();
        viewPosInfo.type = ViewPosInfo.TYPE_TIP;
        viewPosInfo.layoutId = decorLayoutId;
        viewPosInfo.rectF = rect;
        viewPosInfo.view = view;
        if (onPosCallback == null && decorLayoutId != -1)
        {
            throw new IllegalArgumentException("onPosCallback can not be null.");
        }
        LayoutParams layoutParams = new LayoutParams();
        onPosCallback.getPos(parent.getWidth() - rect.right, parent.getHeight() - rect.bottom, rect, layoutParams);
        viewPosInfo.layoutParams = layoutParams;
        viewPosInfo.onPosCallback = onPosCallback;
        mViewRects.add(viewPosInfo);

        return this;
    }

    /**
     * 加入引导控制视图
     * @param layOutId
     * @param onPosCallback
     * @param onClickListener
     * @return
     */
    public GuideModel addControlView(int layOutId, OnPosCallback onPosCallback, View.OnClickListener onClickListener){
        ViewGroup parent = (ViewGroup) mAnchor;
        ViewPosInfo viewPosInfo = new ViewPosInfo();
        viewPosInfo.type = ViewPosInfo.TYPE_CONTROL;
        viewPosInfo.layoutId = -1;
        RectF rect = new RectF();
        viewPosInfo.rectF = rect;
        viewPosInfo.view = null;
        viewPosInfo.layoutId = layOutId;
        if (onPosCallback == null)
        {
            throw new IllegalArgumentException("onPosCallback can not be null.");
        }
        LayoutParams layoutParams = new LayoutParams();
        onPosCallback.getPos(parent.getWidth(), parent.getHeight(), rect, layoutParams);
        viewPosInfo.layoutParams = layoutParams;
        viewPosInfo.onPosCallback = onPosCallback;
        viewPosInfo.onClickListener = onClickListener;
        mViewRects.add(viewPosInfo);

        return this;
    }

    /**
     * 加入标记视图
     * @param viewId
     * @param markLayoutId
     * @param onPosCallback
     * @return
     */
    public GuideModel addMarkView(int viewId, int markLayoutId, OnPosCallback onPosCallback){
        ViewGroup parent = (ViewGroup) mAnchor;
        View view = parent.findViewById(viewId);
        addMarkView(view, markLayoutId, onPosCallback);
        return this;
    }
    /**
     * 加入标记视图
     * @param view
     * @param markLayoutId
     * @param onPosCallback
     * @return
     */
    public GuideModel addMarkView(View view, int markLayoutId, OnPosCallback onPosCallback){
        ViewGroup parent = (ViewGroup) mAnchor;
        RectF rect = new RectF(ViewUtils.getLocationInView(parent, view));
        ViewPosInfo viewPosInfo = new ViewPosInfo();
        viewPosInfo.type = ViewPosInfo.TYPE_MARK;
        viewPosInfo.layoutId = markLayoutId;
        viewPosInfo.rectF = rect;
        viewPosInfo.view = view;
        if (onPosCallback == null && markLayoutId != -1)
        {
            throw new IllegalArgumentException("onPosCallback can not be null.");
        }
        LayoutParams layoutParams = new LayoutParams();
        layoutParams.width = (int)rect.width();
        layoutParams.height = (int)rect.height();
        onPosCallback.getPos(parent.getWidth() - rect.right, parent.getHeight() - rect.bottom, rect, layoutParams);
        viewPosInfo.layoutParams = layoutParams;
        viewPosInfo.onPosCallback = onPosCallback;
        mViewRects.add(viewPosInfo);

        return this;
    }

    /**
     * 加入标记视图
     * @param viewId
     * @param markBitmap
     * @param onPosCallback
     * @return
     */
    public GuideModel addMarkView(int viewId, Bitmap markBitmap, OnPosCallback onPosCallback){
        ViewGroup parent = (ViewGroup) mAnchor;
        View view = parent.findViewById(viewId);
        addMarkView(view, markBitmap, onPosCallback);
        return this;
    }
    /**
     * 加入标记视图
     * @param view
     * @param markBitmap 标记图片
     * @param onPosCallback
     * @return
     */
    public GuideModel addMarkView(View view, Bitmap markBitmap, OnPosCallback onPosCallback){
        ViewGroup parent = (ViewGroup) mAnchor;
        RectF rect = new RectF(ViewUtils.getLocationInView(parent, view));
        ViewPosInfo viewPosInfo = new ViewPosInfo();
        viewPosInfo.type = ViewPosInfo.TYPE_MARK;
        viewPosInfo.markBitmap = markBitmap;
        viewPosInfo.rectF = rect;
        viewPosInfo.view = view;
        if (onPosCallback == null)
        {
            throw new IllegalArgumentException("onPosCallback can not be null.");
        }
        if (markBitmap == null)
        {
            throw new IllegalArgumentException("markBitmap can not be null.");
        }
        LayoutParams layoutParams = new LayoutParams();
        layoutParams.width = (int)rect.width();
        layoutParams.height = (int)rect.height();
        onPosCallback.getPos(parent.getWidth() - rect.right, parent.getHeight() - rect.bottom, rect, layoutParams);
        viewPosInfo.layoutParams = layoutParams;
        viewPosInfo.onPosCallback = onPosCallback;
        mViewRects.add(viewPosInfo);

        return this;
    }
    /**
     * 创建下一个引导
     */
    public GuideModel next(){
        return mGuide.next();
    }

    /**
     * 结束引导创建
     * @return
     */
    public Guide end(){
        return mGuide.end();
    }
}
