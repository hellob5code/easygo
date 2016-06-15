package com.easygo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.easygo.activity.HomeCityActivity;
import com.easygo.activity.HouseDetailActivity;
import com.easygo.activity.R;
import com.easygo.utils.StringUtils;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Created by PengHong on 2016/4/29.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    CirclePageIndicator titleIndicator;

    //第一步：数据源
    //广告的图片
//http://easygo.b0.upaiyun.com/test/advert1.jpg
    private String[] advertImages = new String[]{
            "http://easygo.b0.upaiyun.com/advert/advert1.jpg",
            "http://easygo.b0.upaiyun.com/advert/advert2.jpg",
            "http://easygo.b0.upaiyun.com/advert/advert3.jpg",
            "http://easygo.b0.upaiyun.com/advert/advert4.jpg",
            "http://easygo.b0.upaiyun.com/advert/advert5.jpg"
    };
    //热门城市的图片
    private int[] cityImages = new int[]{
            R.drawable.city_shanghai1,
            R.drawable.city_suzhou1,
            R.drawable.city_hangzhou1,
    };
    private String[] title = new String[]{"上海", "苏州", "杭州"};
    //热门房源的图片
    private int[] hotImages = new int[]{
            R.drawable.hot_room1,
            R.drawable.hot_room2,
            R.drawable.hot_room3,
    };
    //热门房源的id
    private int[] hotRoomId = new int[]{1, 2, 3};

    //本地生活的图片
    private int[] localImages = new int[]{
            R.drawable.local_hot1,
            R.drawable.local_hot2,
            R.drawable.local_hot3,
    };
    //热门房源的id
    private int[] localRoomId = new int[]{4, 5, 6};
    //第二步：确定viewpager布局，这里直接在主界面声明viewpager即可
    ViewPager mCityViewPager;
    ViewPager mAdvertViewPager;
    ViewPager mHotViewPager;
    ViewPager mLocalViewPager;
    //广告、热门城市、热门房源的list
    List<ImageView> mHomePageCityList;
    List<ImageView> mHomePageAdvertList;
    List<ImageView> mHomePageHotList;
    List<ImageView> mHomePageLocalList;
    //    List<ImageView> mHomePageLocalList;
    //城市中的控件
    Button mCityLeft, mCityRight, mHomeHotLeft, mHomeHotRight, mHomeLocalLeft, mHomeLocalRight;
    TextView mCityText;

    //第三步：确定适配器，这里采用PagerAdapter

    //得到绑定的页面布局
    View mView;
    //自动轮播使用的服务
    private ScheduledExecutorService scheduledExecutorService;
    //自动播放时使用的变量
    private int currentItem;
    //定位
    private AMapLocationClient mLocationClient;
    private TextView mLocationTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.bottom_homepage, null);
        initViews();
        //广告适配器初始化
        initHomePagerAdvert();
        //城市适配器初始化
        initHomePagerCity();
        //热门房源适配器初始化
        initHomePagerHot();
        //本地生活适配器初始化
        initHomePagerLocal();

        addListener();
        //初始化位置
        initLocation();
        return mView;
    }

    private void addListener() {
        mCityLeft.setOnClickListener(this);
        mCityRight.setOnClickListener(this);
        mHomeHotLeft.setOnClickListener(this);
        mHomeHotRight.setOnClickListener(this);
        mHomeLocalLeft.setOnClickListener(this);
        mHomeLocalRight.setOnClickListener(this);
    }

    //监听事件按钮的实现
    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.left_city_btn:
                mCityViewPager.arrowScroll(1);
                break;
            case R.id.right_city_btn:
                mCityViewPager.arrowScroll(2);
                break;
            case R.id.left_home_hot_btn:
                mHotViewPager.arrowScroll(1);
                break;
            case R.id.right_home_hot_btn:
                mHotViewPager.arrowScroll(2);
                break;
            case R.id.left_home_local_btn:
                mLocalViewPager.arrowScroll(1);
                break;
            case R.id.right_home_local_btn:
                mLocalViewPager.arrowScroll(2);
                break;
        }
    }

    private void initViews() {
        titleIndicator = (CirclePageIndicator) mView.findViewById(R.id.homepage_advert_viewpagerindicator);
        //滑动屏幕控件初始化
        mAdvertViewPager = (ViewPager) mView.findViewById(R.id.homepage_advert_viewpager);
        mCityViewPager = (ViewPager) mView.findViewById(R.id.homepage_city_viewpager);
        mHotViewPager = (ViewPager) mView.findViewById(R.id.homepage_hot_viewpager);
        mLocalViewPager = (ViewPager) mView.findViewById(R.id.homepage_local_viewpager);
        //按钮，文本初始化
        mCityLeft = (Button) mView.findViewById(R.id.left_city_btn);
        mCityRight = (Button) mView.findViewById(R.id.right_city_btn);
        mHomeHotLeft = (Button) mView.findViewById(R.id.left_home_hot_btn);
        mHomeHotRight = (Button) mView.findViewById(R.id.right_home_hot_btn);
        mHomeLocalLeft = (Button) mView.findViewById(R.id.left_home_local_btn);
        mHomeLocalRight = (Button) mView.findViewById(R.id.right_home_local_btn);
        mCityText = (TextView) mView.findViewById(R.id.city_text);
        mLocationTextView = (TextView) mView.findViewById(R.id.location_my);
    }

    private void initHomePagerAdvert() {
        //广告轮播图
        mHomePageAdvertList = new ArrayList<>();
        for (int i = 0; i < advertImages.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(getActivity()).load(advertImages[i]).into(imageView);
            //imageView.setBackgroundResource(advertImages1[i]);
            mHomePageAdvertList.add(imageView);
        }

        //广告滑动图的适配器
        mAdvertViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mHomePageAdvertList.size();
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mHomePageAdvertList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {
                container.addView(mHomePageAdvertList.get(position));

                //监听
                mHomePageAdvertList.get(position).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                        /*Intent intent = new Intent(getActivity(), BookActivity.class);
                        startActivity(intent);*/

                            }
                        });

                return mHomePageAdvertList.get(position);
            }
        });
        titleIndicator.setViewPager(mAdvertViewPager);
    }

    //热门城市
    private void initHomePagerCity() {
        //显示的图片
        mHomePageCityList = new ArrayList<>();
        for (int i = 0; i < cityImages.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(cityImages[i]);
            mHomePageCityList.add(imageView);
        }

        //城市滑动图的适配器
        mCityViewPager.setAdapter(new PagerAdapter() {

            @Override
            public int getCount() {
                return mHomePageCityList.size();
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mHomePageCityList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {
                container.addView(mHomePageCityList.get(position));

                //监听
                mHomePageCityList.get(position).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), HomeCityActivity.class);
                        intent.putExtra("City", title[position] + "市");
                        startActivity(intent);
//                        Toast.makeText(getActivity(), title[position], Toast.LENGTH_SHORT).show();
                    }
                });
                return mHomePageCityList.get(position);
            }
        });
        //滑动监听
        mCityViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //滑动时设置监听
                mCityText.setText(title[position]);//滑动时更改名字

            }

            @Override
            public void onPageSelected(int position) {
                //选择时

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //更改滑动状态时
            }
        });
    }

    private void initHomePagerHot() {
        //显示的图片
        mHomePageHotList = new ArrayList<>();
        for (int i = 0; i < hotImages.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(hotImages[i]);
            mHomePageHotList.add(imageView);
        }

        //热门房源滑动图的适配器
        mHotViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mHomePageHotList.size();
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mHomePageHotList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {

                container.addView(mHomePageHotList.get(position));

                //跳转到具体房源页面
                mHomePageHotList.get(position).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), HouseDetailActivity.class);
                        intent.putExtra("houseid", hotRoomId[position]);
                        startActivity(intent);

                    }
                });

                return mHomePageHotList.get(position);
            }

        });

    }

    private void initHomePagerLocal() {
        //显示的图片
        mHomePageLocalList = new ArrayList<>();
        for (int i = 0; i < localImages.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(localImages[i]);
            mHomePageLocalList.add(imageView);
        }

        //本地热门滑动图的适配器
        mLocalViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mHomePageLocalList.size();
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mHomePageLocalList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {

                container.addView(mHomePageLocalList.get(position));

                //测试使用，跳转到具体房源页面
                mHomePageLocalList.get(position).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), HouseDetailActivity.class);
                        Log.e("666house","...."+localRoomId[position]);
                        intent.putExtra("houseid",localRoomId[position]);
                        startActivity(intent);
                    }
                });
                return mHomePageLocalList.get(position);
            }
        });
    }

    //首页定位初始化
    private void initLocation() {
        mLocationClient = new AMapLocationClient(getActivity());
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setOnceLocation(true);
        mLocationClient.setLocationOption(option);
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        String city = aMapLocation.getCity();
                        String district = aMapLocation.getDistrict();
                        String location = StringUtils.extractLocation(city, district);
                        mLocationTextView.setText(location);
                    }
                }
            }
        });
        mLocationClient.startLocation();

    }

    /**
     * 利用线程池定时执行动画轮播
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(
                new ViewPageTask(),
                6,
                6,
                TimeUnit.SECONDS);
    }


    private class ViewPageTask implements Runnable {
        @Override
        public void run() {
            currentItem = (currentItem + 1) % advertImages.length;
            mHandler.sendEmptyMessage(0);
        }
    }

    /**
     * 接收子线程传递过来的数据
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mAdvertViewPager.setCurrentItem(currentItem);
        }
    };

}
