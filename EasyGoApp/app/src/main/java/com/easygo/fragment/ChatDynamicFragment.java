package com.easygo.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.easygo.activity.R;
import com.easygo.adapter.ChatDynamicAdpter;
import com.easygo.beans.chat_comment.CommentData;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by PengHong on 2016/4/29.
 */
public class ChatDynamicFragment extends Fragment {
    //定义视图
    View mChatDynamicView;
    //定义适配器
    ChatDynamicAdpter mChatDynamicAdpter;

    List<CommentData> commentDataList;
    List<Integer> gridview_list;
    ListView commentListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mChatDynamicView=inflater.inflate(R.layout.chat_dynamic,null);
        initView();
        initData();
        initAdapter();

        return  mChatDynamicView;
    }

    private void initAdapter() {
        mChatDynamicAdpter=new ChatDynamicAdpter(commentDataList,ChatDynamicFragment.this);
        commentListView.setAdapter(mChatDynamicAdpter);
    }

    private void initView() {
        //初始化所有控件
        commentListView= (ListView) mChatDynamicView.findViewById(R.id.dynamic_listview);

    }
    private void initData() {
        commentDataList=new ArrayList<CommentData>();

        gridview_list=new ArrayList<>();
        //添加图片
        gridview_list.add(R.drawable.home_city_room1);
        gridview_list.add(R.drawable.home_city_room1);
        gridview_list.add(R.drawable.home_city_room1);
        gridview_list.add(R.drawable.home_city_room1);
        gridview_list.add(R.drawable.home_city_room1);
        gridview_list.add(R.drawable.home_city_room1);
        gridview_list.add(R.drawable.home_city_room1);
        gridview_list.add(R.drawable.home_city_room1);
        gridview_list.add(R.drawable.home_city_room1);


        //动态列表
        CommentData commentdata1=new CommentData(R.mipmap.ic_launcher,"rose","12:00","浏览：321次","今天天气真好",gridview_list,R.mipmap.ic_launcher,"赞",R.mipmap.ic_launcher,"评论",R.mipmap.ic_launcher,"转发",R.mipmap.ic_launcher,"ROSE","今天天气不错啊！","13:00");
        CommentData commentdata2=new CommentData(R.mipmap.ic_launcher,"jack","11:00","浏览：32次","想回学校",gridview_list,R.mipmap.ic_launcher,"赞",R.mipmap.ic_launcher,"评论",R.mipmap.ic_launcher,"转发",R.mipmap.ic_launcher,"ROSE","额！","12:00");
        CommentData commentdata3=new CommentData(R.mipmap.ic_launcher,"rose","12:20","浏览：54次","好想学习啊",gridview_list,R.mipmap.ic_launcher,"赞",R.mipmap.ic_launcher,"评论",R.mipmap.ic_launcher,"转发",R.mipmap.ic_launcher,"ROSE","饿？！","11:00");
        CommentData commentdata4=new CommentData(R.mipmap.ic_launcher,"rose","12:03","浏览：121次","真想学啊",gridview_list,R.mipmap.ic_launcher,"赞",R.mipmap.ic_launcher,"评论",R.mipmap.ic_launcher,"转发",R.mipmap.ic_launcher,"ROSE","呃呃呃！","13:20");
        CommentData commentdata5=new CommentData(R.mipmap.ic_launcher,"rose","11:00","浏览：51次","该睡觉了",gridview_list,R.mipmap.ic_launcher,"赞",R.mipmap.ic_launcher,"评论",R.mipmap.ic_launcher,"转发",R.mipmap.ic_launcher,"ROSE","(⊙o⊙)…！","13:31");

        //添加数据
        commentDataList.add(commentdata1);
        commentDataList.add(commentdata2);
        commentDataList.add(commentdata3);
        commentDataList.add(commentdata4);
        commentDataList.add(commentdata5);

    }
}
