package org.altbeacon.beaconreference;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.content.Context;


import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;

import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends Activity {
    ImageView imageView;
    private Button btn1,btn2,btn3,btn4;
    private ImageButton setbtn,mapbtn,searchbtn;

    //original part
    private Context mContext;
    private ViewPager mViewPager;
    private int viewPagerItemSize = 0;
    private final int INTERVAL = 1000 * 3;
    private ArrayList<Integer> mArrayList;
    private final static int SET_VIEWPAGER_ITEM = 9527;
    private LauncherViewPagerAdapter mViewPagerAdapter;
    private  int[] pic = {R.drawable.light,R.drawable.redlight};
    private int picIndex = 0;
    private int maxIndex = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SET_VIEWPAGER_ITEM:
                    if (mViewPager != null && mViewPagerAdapter != null) {
                        int currentItemIndex = mViewPager.getCurrentItem();
                        int itemsCount = mViewPagerAdapter.getCount();
                        if ((currentItemIndex + 1) < itemsCount) {
                            mViewPager.setCurrentItem(currentItemIndex + 1, true);
                        } else {
                            mViewPager.setCurrentItem(0, false);
                        }
                    }
                    break;
            }
        }
    };

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Message message = mHandler.obtainMessage();
            message.what = SET_VIEWPAGER_ITEM;
            mHandler.sendMessage(message);
            mHandler.removeCallbacks(mRunnable);
            mHandler.postDelayed(this, INTERVAL);


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉状态栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        init();



        ///original
        searchbtn = (ImageButton) findViewById(R.id.searchView);
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"歡迎進入全美食區",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, FoodActivity.class);
                startActivity(intent);
            }
        });

        imageView = (ImageView) findViewById(R.id.imageView3);
        btn1 = (Button) findViewById(R.id.button);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"歡迎進入小吃區",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, FoodActivity.class);
                startActivity(intent);
                if(picIndex == maxIndex)
                {
                    picIndex =0;
                }
                else
                {
                    picIndex = picIndex +1;
                }
                new CountDownTimer(15000, 1000) { // 5000 = 5 sec

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        imageView.setImageResource(pic[picIndex]);
                    }
                }.start();
            }
        });

        btn2 = (Button) findViewById(R.id.button2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"歡迎進入飲品區",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, FoodActivity.class);
                startActivity(intent);
            }
        });

        btn3 = (Button) findViewById(R.id.button3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"歡迎進入吃到飽區",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, FoodActivity.class);
                startActivity(intent);
            }
        });

        btn4 = (Button) findViewById(R.id.button4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"歡迎進入甜食區",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, FoodActivity.class);
                startActivity(intent);
            }
        });

        setbtn = (ImageButton) findViewById(R.id.SetButton);
        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"設定介面",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this,SetActivity.class);
                startActivity(intent);
            }
        });

        mapbtn = (ImageButton) findViewById(R.id.MapButton);
        mapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"進入地圖導航介面",Toast.LENGTH_LONG).show();
                Uri uri=Uri.parse("https://www.google.com/maps/dir//111%E5%8F%B0%E5%8C%97%E5%B8%82%E5%A3%AB%E6%9E%97%E5%8D%80%E5%9F%BA%E6%B2%B3%E8%B7%AF101%E8%99%9F%E5%A3%AB%E6%9E%97%E5%A4%9C%E5%B8%82/@25.0919672,121.5207881,16z/data=!4m8!4m7!1m0!1m5!1m1!1s0x3442aeb1c4fdaf05:0xe7c26dbe86e7f929!2m2!1d121.5242024!2d25.0879869");
                Intent i=new Intent(Intent.ACTION_VIEW,uri);
                startActivity(i);
            }
        });



        ///original

    }

    //初始化，此处需要确保先赋值，后加载数据，否则为空
    private void init() {
        initData();
        if (viewPagerItemSize > 0) {
            initViewPager();
            setAutoChangeViewPager();
        }
    }

    //准备ViewPager将显示的数据
    private void initData() {
        mContext = this;
        mArrayList = new ArrayList<Integer>();
        mArrayList.add(R.drawable.image1);
        mArrayList.add(R.drawable.image2);
        mArrayList.add(R.drawable.image3);
        mArrayList.add(R.drawable.image4);
        viewPagerItemSize = mArrayList.size();
    }


    //初始化ViewPager
    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.guide_viewpager);
        mViewPagerAdapter = new LauncherViewPagerAdapter(mContext);
        mViewPager.setPageTransformer(false, new CustPagerTransformer(this));
        mViewPagerAdapter.setAdapterData(mArrayList);
        mViewPager.setAdapter(mViewPagerAdapter);
        int currentItem = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % viewPagerItemSize;
        mViewPager.setCurrentItem(currentItem);
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                mViewPager.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    cancelAutoScroll();
                } else if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    cancelAutoScroll();
                } else if (state == ViewPager.SCROLL_STATE_IDLE) {
                    startAutoScroll();
                }
            }
        });

    }

    private void startAutoScroll() {
        cancelAutoScroll();
        mHandler.postDelayed(mRunnable, 1000 * 3);
    }

    public void cancelAutoScroll() {
        mHandler.removeCallbacksAndMessages(null);
    }

    //开启ViewPager的自动轮播
    @SuppressWarnings("deprecation")
    private void setAutoChangeViewPager() {
        mHandler.postDelayed(mRunnable, INTERVAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mViewPager) {
            mViewPager.removeAllViews();
            mViewPager = null;
        }
    }

    class LauncherViewPagerAdapter extends PagerAdapter {
        private Context mContext;
        private ArrayList<Integer> pagesArrayList;
        private View itemView;

        public LauncherViewPagerAdapter(Context context) {
            this.mContext = context;
        }

        public void setAdapterData(ArrayList<Integer> arrayList) {
            pagesArrayList = arrayList;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (pagesArrayList.size() > 0) {
                itemView = LayoutInflater.from(mContext).inflate(R.layout.guide_pager_adapter, null);
                itemView.setFocusable(true);
                ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);

                imageView.setBackgroundResource(pagesArrayList.get(position % pagesArrayList.size()));
                container.addView(itemView);
                itemView.setClickable(true); //这里itemView需要设置，否则up事件无法响应
                itemView.setEnabled(true);
                itemView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                mHandler.removeCallbacksAndMessages(null);
                                break;
                            case MotionEvent.ACTION_MOVE:
                            case MotionEvent.ACTION_UP:
                                mHandler.removeCallbacksAndMessages(null);
                                setAutoChangeViewPager();
                                break;
                        }
                        return false;
                    }
                });
                return itemView;
            }
            return null;

        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }





}