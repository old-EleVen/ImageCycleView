package com.example.sy.mycycleview;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by sy on 2016/5/24.
 */
public class ImageCycleView extends LinearLayout{
    /*
    ImageCycleView回调接口
     */
    private OnImageCycleListener onImageCycleListener;

    /*
    context上下文
     */
    private Context context;

    /*
    cycleviewpager 实例
     */
    private CycleViewPager cycleViewPager;

    /*
    imagecycleview adapter
     */
    private ImageCycleAdapter adapter;

    /*
    图片资源数组
     */
    private int imagesCount;

    /*
    显示当前图片位置按钮Layout
     */
    private LinearLayout viewGroup;

    /*
    图片位置image
     */
    private ImageView[] selectedIcons;

    /*
    存储当前显示的image
     */
    private int nowImage = 1;

    /*
    存储上一个显示的图片位置
     */
    private int pre_image = 0;

    public ImageCycleView(Context context) {
        super(context);
    }

    public ImageCycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.imagecycleview,this);
        cycleViewPager = (CycleViewPager)view.findViewById(R.id.cycleviewpager);
        adapter = new ImageCycleAdapter();
        cycleViewPager.setOnPageChangeListener(new ImagePageChangeListener());
        viewGroup = (LinearLayout)findViewById(R.id.viewGroup);
    }

    /*
    初始化图片位置imageview
     */
    private void initIconPositionView() {
        selectedIcons = new ImageView[imagesCount];
        for (int i=0;i<selectedIcons.length;i++){
            selectedIcons[i] = new ImageView(context);
            selectedIcons[i].setBackgroundResource(R.mipmap.icon_point);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            selectedIcons[i].setLayoutParams(params);
            viewGroup.addView(selectedIcons[i]);
        }
    }

    /*
    传入图片数量信息
     */
    public void setImagesCount(int imagesCount){
        //每一次设置之前先将位置图片移除
        viewGroup.removeAllViews();
        this.imagesCount = imagesCount;
        initIconPositionView();
        cycleViewPager.setAdapter(adapter);
        cycleViewPager.setCurrentItem(1);
    }

    /*
    图片自动滚动
     */
    private Handler mHandler = new Handler();
    private Runnable mImageTask = new Runnable() {
        @Override
        public void run() {
                if ((++nowImage) == imagesCount + 1)
                    nowImage = 1;
                cycleViewPager.setCurrentItem(nowImage);
        }
    };

    /*
    开始滚动
     */
    public void startImageTimerTask(){
        stopImageTimerTask();
        mHandler.postDelayed(mImageTask,3000);
    }

    /*
    停止滚动
     */
    public void stopImageTimerTask(){
        mHandler.removeCallbacks(mImageTask);
    }


    private class ImagePageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position<=imagesCount&&position>0) {
                selectedIcons[pre_image].setImageResource(R.mipmap.icon_point);
                nowImage = position;
                pre_image = position-1;
                selectedIcons[nowImage-1].setImageResource(R.mipmap.icon_point_pre);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            //图片变化 继续轮播
            stopImageTimerTask();
            startImageTimerTask();
        }
    }

    private class ImageCycleAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return imagesCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final ImageView imageView = new ImageView(context);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.
                    LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(layoutParams);
            container.addView(imageView);
            onImageCycleListener.showImageView(imageView,position);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onImageCycleListener.OnImageClick(imageView,position);
                }
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView view = (ImageView) object;
            container.removeView(view);
        }
    }

    /*
    添加监听回调
     */
    public void setOnImageCycleListener(OnImageCycleListener onImageCycleListener){
        this.onImageCycleListener = onImageCycleListener;
    }

    /**
     *
     *回调接口
     */
    public interface OnImageCycleListener{
        void OnImageClick(ImageView view,int position);

        void showImageView(ImageView view,int position);
    }
}
