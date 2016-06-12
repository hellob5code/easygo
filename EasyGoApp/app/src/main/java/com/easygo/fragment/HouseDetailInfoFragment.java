package com.easygo.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentHostCallback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.easygo.activity.HouseDetailActivity;
import com.easygo.activity.R;
import com.easygo.beans.house.House;
import com.easygo.beans.house.HouseEquipmentName;

import java.util.List;

/**
 * Created by zzia on 2016/5/20.
 */
public class HouseDetailInfoFragment extends Fragment {
    ListView mEquipmentListView;
    View mHouseContentView;
    private TextView houseDetailDescribe, houseDetailStyle, houseDetailSex,
            houseDetailStayTime, houseDetailMostNum, houseDetailAddPrice,
            mEquipTextView1, mEquipTextView2, mEquipTextView3, mEquipTextView4,
            mShowmoreTextView;
    //得到房源
    House mHouse;
    Bundle mBundle;
    //根据房源编号获取设施
    List<String> mEquipmentList;

    //显示数据需要使用到的控件
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mHouseContentView = inflater.inflate(R.layout.fragment_info_scroll, null);
        initViews();
//        initData();
//        initHouseinfoView();
//        initListener();
        return mHouseContentView;
    }

    private void initViews() {
        houseDetailDescribe = (TextView) mHouseContentView.findViewById(R.id.house_detail_describe);
        houseDetailStyle = (TextView) mHouseContentView.findViewById(R.id.house_detail_style);
        houseDetailSex = (TextView) mHouseContentView.findViewById(R.id.house_detail_sex);
        houseDetailStayTime = (TextView) mHouseContentView.findViewById(R.id.house_detail_stay_time);
        houseDetailMostNum = (TextView) mHouseContentView.findViewById(R.id.house_detail_most_num);
        houseDetailAddPrice = (TextView) mHouseContentView.findViewById(R.id.house_detail_add_price);

        mEquipTextView1 = (TextView) mHouseContentView.findViewById(R.id.house_detail_equipment1);
        mEquipTextView2 = (TextView) mHouseContentView.findViewById(R.id.house_detail_equipment2);
        mEquipTextView3 = (TextView) mHouseContentView.findViewById(R.id.house_detail_equipment3);
        mEquipTextView4 = (TextView) mHouseContentView.findViewById(R.id.house_detail_equipment4);
        mShowmoreTextView = (TextView) mHouseContentView.findViewById(R.id.house_detail_showmore);
    }

    public void initInfoData(House house, List<HouseEquipmentName> hemList) {
//        mBundle = getArguments();
//        mHouse = (House) mBundle.getSerializable("HouseDetail");
       /* HouseDetailActivity houseDetailActivity = (HouseDetailActivity) getActivity();
        mHouse = houseDetailActivity.mHouse;*/

//        Log.e("dd",mHouse.getHouse_describe()+"666");
//        this.mHouse = house;
        /*houseDetailDescribe.setText(mHouse.getHouse_describe());
        houseDetailStyle.setText(mHouse.getHouse_style());
        houseDetailSex.setText(mHouse.getHouse_limit_sex());
        houseDetailStayTime.setText(mHouse.getHouse_stay_time());
        houseDetailMostNum.setText(mHouse.getHouse_most_num());
        houseDetailAddPrice.setText(mHouse.getHouse_add_price() + "");*/
        houseDetailDescribe.setText(house.getHouse_describe());
//        Log.e("111",house.getHouse_describe()+"00000");
        houseDetailStyle.setText(house.getHouse_style());
        houseDetailSex.setText(house.getHouse_limit_sex());
        houseDetailStayTime.setText(house.getHouse_stay_time() + "");
        houseDetailMostNum.setText(house.getHouse_most_num() + "");
        houseDetailAddPrice.setText(house.getHouse_add_price() + "");

        if (hemList.size() == 1) {
            mEquipTextView1.setText(hemList.get(0).getEquipment_name());
        }
        if (hemList.size() == 2) {
            mEquipTextView2.setText(hemList.get(1).getEquipment_name());
        }
        if (hemList.size() == 3) {
            mEquipTextView3.setText(hemList.get(2).getEquipment_name());
        }
        if (hemList.size() == 4) {
            mEquipTextView4.setText(hemList.get(3).getEquipment_name());
        }

    }


    private void initHouseinfoView() {
//        mHouseLocationView=mHouseContentView.findViewById(R.id.house_location);
        //  mEquipmentListView= (ListView) mHouseContentView.findViewById(R.id.house_equipment_listview);
//        mEquipmentListView.setAdapter(new HouseEquipmentAdpter(mEquipmentList,getActivity()));

    }

//    private void initListener() {
//
//    }
}
