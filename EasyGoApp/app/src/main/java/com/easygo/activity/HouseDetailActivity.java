package com.easygo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.easygo.adapter.HouseDetailAdpter;
import com.easygo.application.MyApplication;
import com.easygo.beans.gson.GsonAboutHouseDetail;
import com.easygo.beans.house.House;
import com.easygo.beans.house.HouseEquipmentName;
import com.easygo.beans.house.HousePhoto;
import com.easygo.beans.user.User;
import com.easygo.fragment.HouseDetailAssessFragment;
import com.easygo.fragment.HouseDetailInfoFragment;
import com.easygo.fragment.HouseDetailOwnerFragment;
import com.easygo.fragment.HouseDetailRuleFragment;
import com.easygo.view.WaitDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RequestQueue;
import com.yolanda.nohttp.Response;
import com.yolanda.nohttp.error.ClientError;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.NotFoundCacheError;
import com.yolanda.nohttp.error.ServerError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 具体房源页面
 */
public class HouseDetailActivity extends AppCompatActivity implements View.OnClickListener {
    //自定义一个dialog
    private WaitDialog mDialog;
    public static final String TYPE = "type";
    public static final int GET_HOUSE_DETAIL_WHAT = 1;
    public static final int HOUSE_ADD_COOLECT_WHAT = 2;
    public static final int HOUSE_DEL_COOLECT_WHAT = 3;
    ViewPager mPhotoViewPager;
    //图片资源数组
    private int[] houseImages;
    private List<ImageView> mImageViewList;

    RadioGroup mRadioGroup;
    List<Fragment> mHouseInfoList;
    //四个fragment
    HouseDetailInfoFragment mInfoFragment;
    HouseDetailRuleFragment mRuleFragment;
    HouseDetailOwnerFragment mOwnerFragment;
    HouseDetailAssessFragment mAssessFragment;
    //碎片
    FragmentManager mFragmentManager;
    HouseDetailAdpter mHouseDetailAdpter;

    ViewPager mHouseinfoViewPager;

    //房源信息用到的控件mHouseDescribeTextview未使用
    TextView mHousePriceTextview, mHousePhotoSizeTextView;
    ImageView mHouseCollectionImageView, mHouseShareImageView, mBackView;//是否收藏
    Button mBookButton;
    //一键分享使用
    private OnekeyShare mOnekeyShare = null;
    //网络请求队列
    private RequestQueue requestQueue;
    Request<String> request;
    String mPath;
    public int houseid;//从前面一个点击事件传过来
    int userid;//偏好设置中
    SharedPreferences mSharedPreferences;
    //用于接收数据
    public House mHouse;
    List<HousePhoto> mHousePhotoList;
    boolean isCollected;//是否被收藏
    List<HouseEquipmentName> mHouseEquipmentList;//房屋设施表
    public User mUser;//房东信息
    GsonAboutHouseDetail mGsonAboutHouseDetail;
    Bundle mBundle; //往Fragment发送信息
    PagerAdapter mPagerAdapter;//图片是适配器
    RadioButton rbInfo, rbMap, rbOwner, rbAssess;

    //接收到的数据
    //House house = new House("房屋标题", "我是房屋描述", "我是房屋类型", "交通信息", 5, 120, 20, "不限", 3, 3, 4, false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_detail);
        initViews();
        initListener();
//        initData();
        loadData();
    }


    //初始化视图
    private void initViews() {
        mBackView = (ImageView) findViewById(R.id.house_detail_back);
        mDialog = new WaitDialog(this);//提示框
        mPhotoViewPager = (ViewPager) findViewById(R.id.house_detail_photo_viewpager);
        mRadioGroup = (RadioGroup) findViewById(R.id.house_radiogroup);
        mHouseinfoViewPager = (ViewPager) findViewById(R.id.house_detail_infomation_viewpager);

//        mHouseDescribeTextview = (TextView) findViewById(R.id.house_describe);//图片上的，被删除
        mHousePriceTextview = (TextView) findViewById(R.id.house_price);
        mHousePhotoSizeTextView = (TextView) findViewById(R.id.house_photo_size);
        mHouseCollectionImageView = (ImageView) findViewById(R.id.house_collection);
        mHouseShareImageView = (ImageView) findViewById(R.id.house_share);
        mBookButton = (Button) findViewById(R.id.book_house_btn);
        rbInfo = (RadioButton) findViewById(R.id.house_info);
        rbMap = (RadioButton) findViewById(R.id.house_rule);
        rbOwner = (RadioButton) findViewById(R.id.house_owner_info);
        rbAssess = (RadioButton) findViewById(R.id.house_assess);
    }


    //初始化
    private void initListener() {
        mBackView.setOnClickListener(this);
        //图片滑动时改变图片张数的显示
//        mPhotoViewPager.setOffscreenPageLimit(4);
        mPhotoViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
//                Toast.makeText(HouseDetailActivity.this, "第" + position + "张图片", Toast.LENGTH_SHORT).show();
                mHousePhotoSizeTextView.setText((position + 1) + "/" + mHousePhotoList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                resetViewPager(checkedId);
            }
        });
        //滑动时改变按钮
        mHouseinfoViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //根据当前位置设置默认选中单选按钮
                resetRadio();
                resetRadioButton(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //单击收藏
        mHouseCollectionImageView.setOnClickListener(this);
        //单击分享
        mHouseShareImageView.setOnClickListener(this);
        //申请预定
        mBookButton.setOnClickListener(this);
    }

    public void resetRadio() {
        rbInfo.setTextColor(getResources().getColor(R.color.black));
        rbMap.setTextColor(getResources().getColor(R.color.black));
        rbOwner.setTextColor(getResources().getColor(R.color.black));
        rbAssess.setTextColor(getResources().getColor(R.color.black));
    }

    private void resetRadioButton(int position) {
        //获取position位置处对应的单选按钮

        RadioButton radioButton = (RadioButton) mRadioGroup.getChildAt(position);
        //设置当前单选按钮默认选中
        radioButton.setChecked(true);
        radioButton.setTextColor(getResources().getColor(R.color.textDown));
    }

    private void resetViewPager(int checkedId) {
        switch (checkedId) {
            case R.id.house_info:
                mHouseinfoViewPager.setCurrentItem(0);
                break;
            case R.id.house_rule:
                mHouseinfoViewPager.setCurrentItem(1);
                break;
            case R.id.house_owner_info:
                mHouseinfoViewPager.setCurrentItem(2);
                break;
            case R.id.house_assess:
                mHouseinfoViewPager.setCurrentItem(3);
                break;
        }
    }

    //从服务器获取数据
    private void loadData() {

        mSharedPreferences = getSharedPreferences(TYPE, Context.MODE_PRIVATE);
        //type = mSharedPreferences.getInt("type", 0);
        userid = mSharedPreferences.getInt("user_id", 0);//整个页面要用
        //得到前一个页面传递过来的值
        Intent intent = getIntent();
        houseid = intent.getIntExtra("houseid", 1);
        //初始化
        MyApplication myApplication = (MyApplication) this.getApplication();
        mPath = myApplication.getUrl();
        // 创建请求队列, 默认并发3个请求,传入你想要的数字可以改变默认并发数, 例如NoHttp.newRequestQueue(1);
        requestQueue = NoHttp.newRequestQueue();
        // 创建请求对象
        request = NoHttp.createStringRequest(mPath, RequestMethod.POST);
        // 添加请求参数
        request.add("methods", "getHouseDetailByID");
        request.add("userid", userid);
        request.add("houseid", houseid);
        /*
         * what: 当多个请求同时使用同一个OnResponseListener时用来区分请求, 类似handler的what一样
		 * request: 请求对象
		 * onResponseListener 回调对象，接受请求结果
		 */
        requestQueue.add(GET_HOUSE_DETAIL_WHAT, request, onResponseListener);
    }


    //初始化数据源
    private void initData() {
        //用于图片展示
        mImageViewList = new ArrayList<>();
        for (int i = 0; i < mHousePhotoList.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(this)
                    .load(mHousePhotoList.get(i).getHouse_photo_path())
                    .into(imageView);
            mImageViewList.add(imageView);
        }

        mHouseInfoList = new ArrayList<>();
        mInfoFragment = new HouseDetailInfoFragment();
        mRuleFragment = new HouseDetailRuleFragment();
        mOwnerFragment = new HouseDetailOwnerFragment();
        mAssessFragment = new HouseDetailAssessFragment();
        mHouseInfoList.add(mInfoFragment);
        mHouseInfoList.add(mRuleFragment);
        mHouseInfoList.add(mOwnerFragment);
        mHouseInfoList.add(mAssessFragment);

        //初始化适配器
        mFragmentManager = getSupportFragmentManager();
        mHouseDetailAdpter = new HouseDetailAdpter(mFragmentManager, mHouseInfoList);
        mHouseinfoViewPager.setAdapter(mHouseDetailAdpter);

        //绑定适配器
        mPagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return mImageViewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mImageViewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(mImageViewList.get(position));
                return mImageViewList.get(position);
            }
        };
        mPhotoViewPager.setAdapter(mPagerAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.house_collection:
                if (isCollected) {
                    //已经收藏，取消收藏
                    mHouseCollectionImageView
                            .setImageResource(R.mipmap.icon_collect_blue);
                    isCollected = false;
                    // 添加请求参数
                    request.add("methods", "deleteHouseCollectById");
                    request.add("userid", userid);
                    request.add("houseid", houseid);
                    requestQueue.add(HOUSE_DEL_COOLECT_WHAT, request, onResponseListener);
                } else {
                    //未收藏，点击收藏
                    mHouseCollectionImageView
                            .setImageResource(R.mipmap.icon_collect_on);
                    isCollected = true;
                    // 添加请求参数
                    request.add("methods", "addHouseCollect");
                    request.add("userid", userid);
                    request.add("houseid", houseid);
                    requestQueue.add(HOUSE_ADD_COOLECT_WHAT, request, onResponseListener);

                }
//                Toast.makeText(HouseDetailActivity.this, "单击了收藏按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.house_share:
                mOnekeyShare = new OnekeyShare();
                mOnekeyShare.setTitle("一键分享");
                mOnekeyShare.setText("一键分享测试");
                mOnekeyShare.setImageUrl("http://easygo.b0.upaiyun.com/advert/easygo_icon.png");
                // comment是我对这条分享的评论，仅在人人网和QQ空间使用
                mOnekeyShare.setComment("欢迎下载轻松住APP");
                // site是分享此内容的网站名称，仅在QQ空间使用
                mOnekeyShare.setSite(getString(R.string.app_name));
                // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//                mOnekeyShare.setSiteUrl("http://www.baidu.com");
                mOnekeyShare.setTitle("http://www.baidu.com");
                mOnekeyShare.setTitleUrl("http://www.baidu.com");
                //这话必须放最后
                mOnekeyShare.show(HouseDetailActivity.this);
                break;
            case R.id.book_house_btn:
                if (userid == 0) {
                    //用户还未登录
                    Intent mintent = new Intent();
                    Toast.makeText(HouseDetailActivity.this, "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
                    mintent.setClass(HouseDetailActivity.this, LogintestActivity.class);
                    startActivity(mintent);
                } else {

                    Intent intent = new Intent();
                    intent.setClass(HouseDetailActivity.this, BookActivity.class);
                    intent.putExtra("house", mHouse);
                    intent.putExtra("userid", userid);
                    startActivity(intent);
                }
                break;
            case R.id.house_detail_back:
                //结束当前页面
                finish();
                break;

        }
    }

    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @SuppressWarnings("unused")
        @Override
        public void onSucceed(int what, Response<String> response) {
            switch (what) {
                case GET_HOUSE_DETAIL_WHAT:
                    //获取房源信息
                    String result = response.get();// 响应结果
                    //把JSON格式的字符串改为Student对象
                    Gson gson = new Gson();
                    Type type = new TypeToken<GsonAboutHouseDetail>() {
                    }.getType();
//                Log.e("gson22", result);
                    mGsonAboutHouseDetail = gson.fromJson(result, type);
                    mHouse = mGsonAboutHouseDetail.getHouse();
                    mHousePhotoList = mGsonAboutHouseDetail.getHousePhotoList();
                    isCollected = mGsonAboutHouseDetail.isCollected();
                    mHouseEquipmentList = mGsonAboutHouseDetail.getHouseEquipmentNameList();
                    mUser = mGsonAboutHouseDetail.getUser();

                    //网络请求后获取图片(viewpage中)
//                    mPagerAdapter.notifyDataSetChanged();
                    initData();
                    mHousePhotoSizeTextView.setText(1 + "/" + mHousePhotoList.size());

                    if (isCollected) {
                        mHouseCollectionImageView
                                .setImageResource(R.mipmap.icon_collect_on);
                    }
//                    Log.e("iscollected",isCollected+"");
                    mHousePriceTextview.setText(mHouse.getHouse_one_price() + "元/晚");
                    //初始化Fragment的值
                    mInfoFragment.initInfoData(mHouse, mHouseEquipmentList);
                    if (userid == mHouse.getUser_id()) {
                        //房东自己的房子
                        mBookButton.setVisibility(View.GONE);
                    } else {
                        mBookButton.setVisibility(View.VISIBLE);
                    }
                    break;
                case HOUSE_ADD_COOLECT_WHAT:
                    Toast.makeText(HouseDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                    break;
                case HOUSE_DEL_COOLECT_WHAT:
                    Toast.makeText(HouseDetailActivity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onStart(int what) {
            // 请求开始，这里可以显示一个dialog
            mDialog.show();

        }

        @Override
        public void onFinish(int what) {
            mDialog.dismiss();
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            if (exception instanceof ClientError) {// 客户端错误
                Toast.makeText(HouseDetailActivity.this, "客户端发生错误", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof ServerError) {// 服务器错误
                Toast.makeText(HouseDetailActivity.this, "服务器发生错误", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof NetworkError) {// 网络不好
                Toast.makeText(HouseDetailActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof TimeoutError) {// 请求超时
                Toast.makeText(HouseDetailActivity.this, "请求超时，网络不好或者服务器不稳定", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof UnKnownHostError) {// 找不到服务器
                Toast.makeText(HouseDetailActivity.this, "未发现指定服务器", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof URLError) {// URL是错的
                Toast.makeText(HouseDetailActivity.this, "URL错误", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof NotFoundCacheError) {
                Toast.makeText(HouseDetailActivity.this, "没有发现缓存", Toast.LENGTH_SHORT).show();
                // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
            } else {
                Toast.makeText(HouseDetailActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
