package com.orivon.mob.h3cmanager;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orivon.mob.h3cmanager.bean.MyQueuesBean;
import com.orivon.mob.h3cmanager.bean.Parent;
import com.orivon.mob.h3cmanager.bean.ParentBean;
import com.orivon.mob.h3cmanager.callback.DataCallBack;
import com.orivon.mob.library.view.SuperListView;
import com.squareup.okhttp.Headers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaseInfoListActivity extends AppCompatActivity implements View.OnClickListener {

    SuperListView caseInfoList;
    List<MyQueuesBean> queuesBeans=new ArrayList<>();

    Map<Integer, Integer> queuesBeanMap = new HashMap<>();

    CaseAdapter caseAdapter;

    ImageView imgBack, imgSet;
    TextView textCount, textNum;
    Button btnPrev, btnNext;

    H3CApplication application;
    String caseURL = "http://newcms.h3c.com/hpcms/case/queueCaseList?column=&sort=&selectAllCheckBox=on&queueIds=a552af6e5c214996970713b448dd3cb4&queueIds=c368478d38ac4e5aafb709bcc3ea87b1&queueIds=3286d93e2e1a43b68e135c7a1aff3ceb&queueIds=911e08605d564655bf2b69e7af3db71b&queueIds=d0645ed9b1424a3cbb73286b6d2b4296&queueIds=94571150ce1041d09ebd76923c9f8ee2&queueIds=61a51ccd069348d3b653bb319c103960";

    String h3cCookie;
    HttpConnect http = new HttpConnect();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_info_list);
        caseAdapter = new CaseAdapter();
        application = (H3CApplication) getApplication();
        h3cCookie = application.getH3cCookie();
        if (h3cCookie.isEmpty()) {
            Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show();
            return;
        }



        initView();

        caseInfoList.postDelayed(new Runnable() {
            @Override
            public void run() {
                parseHtml(caseURL, h3cCookie);
            }
        }, 1000);


        Parent parent = new Parent();

        parent.setEmail("");
        try {
            application.getDbManager().save(parent);
        } catch (DbException e) {
            e.printStackTrace();
        }


    }

    private void initView() {
        caseInfoList = (SuperListView) findViewById(R.id.caseInfoList);


        caseInfoList.setSuperListViewRefreshListener(new SuperListView.RefreshListener() {
            @Override
            public void onRefresh() {
                parseHtml(caseURL, h3cCookie);
            }

            @Override
            public void onLoadMore() {

            }
        });




        caseInfoList.setAdapter(caseAdapter);


        caseInfoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                queuesBeanMap.put(queuesBeans.get(position-1).getID(), queuesBeans.get(position-1).getID());

            }
        });

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);


        imgSet = (ImageView) findViewById(R.id.imgSet);
        imgSet.setOnClickListener(this);

        textCount = (TextView) findViewById(R.id.textCount);

        textNum = (TextView) findViewById(R.id.textNum);

        btnPrev = (Button) findViewById(R.id.btnPrev);
        btnPrev.setOnClickListener(this);

        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.imgSet:

                break;
            case R.id.btnPrev:

                break;
            case R.id.btnNext:

                break;

        }


    }


    private class CaseAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return queuesBeans.size();
        }

        @Override
        public Object getItem(int position) {
            return queuesBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_case_info, null);
                holder.textId = (TextView) convertView.findViewById(R.id.textId);
                holder.textName = (TextView) convertView.findViewById(R.id.textName);
                holder.textServerity = (TextView) convertView.findViewById(R.id.textServerity);
                holder.textTitle = (TextView) convertView.findViewById(R.id.textTitle);
                holder.textProductDes = (TextView) convertView.findViewById(R.id.textProductDes);
                holder.textAge = (TextView) convertView.findViewById(R.id.textAge);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.textId.setText(String.valueOf(queuesBeans.get(position).getID()));
            holder.textName.setText(queuesBeans.get(position).getCompany());
            holder.textServerity.setText(queuesBeans.get(position).getSeverity());
            holder.textTitle.setText(queuesBeans.get(position).getTitle());
            holder.textProductDes.setText(queuesBeans.get(position).getProductDes());
            holder.textAge.setText(queuesBeans.get(position).getAge());


            if (queuesBeans.get(position).getSeverity().equals("3-Normal")) {
                holder.textServerity.setSelected(false);

            } else {
                holder.textServerity.setSelected(true);
            }


            if (queuesBeanMap.containsKey(queuesBeans.get(position).getID())) {

                convertView.setBackgroundColor(Color.parseColor("#f5f5f5"));
            } else {
                convertView.setBackgroundColor(Color.parseColor("#fdcd44"));
            }



            return convertView;
        }


        public class ViewHolder {

            TextView textId, textName, textServerity, textTitle, textProductDes, textAge;

        }


    }


    public String getCaseHtml() {
        AssetManager manager = getAssets();
        StringBuffer sb = null;
        try {
            InputStream is = manager.open("case.txt");
            InputStreamReader reader = new InputStreamReader(is);
            sb = new StringBuffer();
            char[] c = new char[1024];
            int len = reader.read(c);
            while (len > 0) {
                len = reader.read(c);
                sb.append(new String(c));
            }
            is.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    public List<MyQueuesBean> parseHtml(String content) {
        Document document = Jsoup.parse(content);
        Elements bodyElements = document.getElementsByTag("tbody");
        Element element = bodyElements.get(2);
        Elements comElements = element.getElementsByTag("tr");
        List<MyQueuesBean> myQueuesBeans = new ArrayList<>();
        MyQueuesBean.Builder builder = new MyQueuesBean.Builder();


        Elements countElements = document.getElementsByClass("blue");

        builder.setRecordCount(countElements.get(0).text().trim());
        builder.setPagerCount(countElements.get(2).text().trim());


        for (int i = 0; i < comElements.size(); i++) {
            Elements infos = comElements.get(i).getElementsByTag("td");


            builder.setID(Integer.parseInt(infos.get(0).text()))
                    .setCompany(infos.get(1).text())
                    .setTitle(infos.get(2).text())
                    .setProductDes(infos.get(3).text())
                    .setSLA(infos.get(4).text())
                    .setQueue(infos.get(5).text())
                    .setSeverity(infos.get(6).text())
                    .setProvince(infos.get(7).text())
                    .setAge(infos.get(8).text())
                    .setUrl("");
            MyQueuesBean myQueuesBean = builder.create();


            myQueuesBeans.add(myQueuesBean);
        }


        return myQueuesBeans;
    }


    public void parseHtml(String url, String Cookie) {
        Headers.Builder builder = new Headers.Builder();
        builder.add("Cookie", Cookie);

        http.doGet(url, builder.build(), new DataCallBack() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFailure(int code) {

            }

            @Override
            public void onSuccessful(String response) {


                queuesBeans = parseHtml(response);


                if (queuesBeans.size() > 0) {
                    textNum.setText(queuesBeans.get(0).getRecordCount());
                    textCount.setText("1/" + queuesBeans.get(0).getPagerCount());
                }
                caseInfoList.refreshCompleted();
                caseAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFinish() {

            }
        });


    }


}
