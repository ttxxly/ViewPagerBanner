package top.ttxxly.viewpagerbanner;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PicActivity extends AppCompatActivity {

    private ViewPager viewPager;
    /**
     * 装点点的ImageView数组
     */

    private ImageView[] tips;
    /**
     * 装ImageView数组
     */
    private ImageView[] mImageViews;
    /**
     * 图片资源id
     */
    private int[] imgIdArray;
    private static final int UPDATE_VIEWPAGER = 0;
    private int autoCurrIndex;
    /**
     * //标志当前页面是否可见
     */
    private int IS_ON_PAUSE = 0;

    /**
     * 定时轮播图片，需要在主线程里面修改 UI
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VIEWPAGER:
                    if (msg.arg1 % mImageViews.length != 0) {
                        viewPager.setCurrentItem(msg.arg1);
                    } else {
                        //false 当从末页调到首页是，不显示翻页动画效果，
                        viewPager.setCurrentItem(msg.arg1, false);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 设置自动轮播图片，3s后执行
     */
    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                Log.i("定时任务线程", "运行中");
                try {
                    Thread.sleep(3000);// 线程暂停3秒，单位毫秒
                    if(IS_ON_PAUSE == 1) return;//当前页面不可见时，线程执行结束
                    Message message = new Message();
                    message.what = UPDATE_VIEWPAGER;
                    message.arg1 = autoCurrIndex + 1;
                    mHandler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic);

        init();

        //设置Adapter
        viewPager.setAdapter(new MyAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            /**
             * 当当前页面被滚动时，该方法将被调用，要么作为程序启动的平滑滚动的一部分，要么是用户发起的触摸滚动。
             * @param position
             * @param positionOffset
             * @param positionOffsetPixels
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            /**
             * 当一个新页面被选中时，这个方法将被调用。动画不一定是完整的。
             * @param position 页面索引值
             */
            @Override
            public void onPageSelected(int position) {
                setImageBackground(position % mImageViews.length);
                autoCurrIndex = position;
            }

            /**
             * 用户拖动时调用
             * @param state
             */
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * Activity在这个阶段已经出现在前台并且可见了
     */
    @Override
    protected void onResume() {
        super.onResume();
        thread.start();//启动定时任务线程.
        IS_ON_PAUSE = 0;
    }

    /**
     * 当启动其他activity时调用此方法,，通常用于提交未保存的数据、停止动画/视频和处理其他占用CPU资源的程序
     */
    @Override
    protected void onPause() {
        super.onPause();
        IS_ON_PAUSE = 1;
    }

    private void init() {

        ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        //载入图片资源ID
        imgIdArray = new int[]{R.mipmap.nijian, R.mipmap.fffff, R.mipmap.scbanner, R.mipmap.minngzhu};

        //将点点加入到ViewGroup中
        tips = new ImageView[imgIdArray.length];
        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(this);
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.mipmap.black_circle);
            } else {
                tips[i].setBackgroundResource(R.mipmap.grey_circle);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(10, 10));
            layoutParams.setMargins(5, 0, 5, 10);
            group.addView(imageView, layoutParams);
        }


        //将图片装载到数组中
        mImageViews = new ImageView[imgIdArray.length];
        for (int i = 0; i < mImageViews.length; i++) {
            ImageView imageView = new ImageView(this);
            mImageViews[i] = imageView;
            imageView.setBackgroundResource(imgIdArray[i]);
        }

    }

    /**
     * 设置选中的tip的背景
     *
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < tips.length; i++) {
            if (i == selectItems) {
                tips[i].setBackgroundResource(R.mipmap.black_circle);
            } else {
                tips[i].setBackgroundResource(R.mipmap.grey_circle);
            }
        }
    }

    public class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(mImageViews[position % mImageViews.length]);

        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(mImageViews[position % mImageViews.length], 0);
            return mImageViews[position % mImageViews.length];
        }
    }

}
