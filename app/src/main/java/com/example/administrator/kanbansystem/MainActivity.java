package com.example.administrator.kanbansystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements NetTool.OnNetListener {

    private KanbanDataAdapter kAdapter;
    private LineAdapter lAdapter;
    private NetTool netTool;

    //看板数据的集合
    private List<List<String>> kList = new ArrayList<>();
    //生产线的集合
    private List<String> lList = new ArrayList<>();
    private String ordir = null;
    private boolean isFirst = false;

    private TextView date,time;
    private RecyclerView rvLine,rvData;
    private Spinner spinner;

    private TimeHandler timeHandler;
    private String soft_name = "kanbansystem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        date = findViewById(R.id.tv_day_data);
        time = findViewById(R.id.tv_time_data);
        rvLine = findViewById(R.id.rv_line);
        rvData = findViewById(R.id.rv_data);
        spinner = findViewById(R.id.spinner);

        timeHandler = new TimeHandler(MainActivity.this);
        timeHandler.setOnTimeDateListener(new TimeHandler.OnTimeDateListener() {
            @Override
            public void onTimeDate(String times, String dates) {
//                  time.setText(times);
//                  date.setText(dates);
            }
        });

        netTool = new NetTool(MainActivity.this);
        netTool.setOnNetListener(this);

//        thread.start();
//        checkVersion();

        /**
         * 选择订单号
         */
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String orderid = (String) spinner.getSelectedItem();
                //过滤
                ordir = orderid;
                if (kAdapter != null && !orderid.equals("无")){
                    kAdapter.getFilter().filter(orderid);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    Thread thread = new Thread(){
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    getBean();
                    sleep(1000 * 30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        timeHandler.regTimeReceiver();
        netTool.registerNetReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        timeHandler.unRegTimeReceiver();
        netTool.unRegisterNetReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeHandler.release();
        netTool.release();
        thread = null;
        netTool.setOnNetListener(null);
        netTool = null;
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        /**
         * 产线
         */
        //设置RecyclerView管理器
        rvLine.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //初始化适配器
        lAdapter = new LineAdapter(lList,this);
        //设置添加或删除item时的动画，这里使用默认动画
        rvLine.setItemAnimator(new DefaultItemAnimator());
        //设置适配器
        rvLine.setAdapter(lAdapter);
        lAdapter.setDataList(lList);
        lAdapter.setOnItemClickListener(new LineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {

            }
        });
        /**
         * 看板数据
         */
        //设置RecyclerView管理器
        rvData.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //初始化适配器
        kAdapter = new KanbanDataAdapter(kList, this, new FilterListener() {
            @Override
            public void getFilterData(List<List<String>> list) {
                  Log.e("Tag","list====="+list);
                  if (lAdapter != null){
                      Map<String,Integer> map = new HashMap<>();
                          for (List<String> str:list) {
                              int s = 1;
                              if (map.get(str.get(0)) != null) {
                                  s = map.get(str.get(0)) + 1;
                              }
                              map.put(str.get(0), s);
                          }
                          lAdapter.setSelection(map);
                          lAdapter.notifyDataSetChanged();
                  }
            }
        });
        //设置添加或删除item时的动画，这里使用默认动画
        rvData.setItemAnimator(new DefaultItemAnimator());
        //设置适配器
        rvData.setAdapter(kAdapter);
        kAdapter.setDataList(kList);
        kAdapter.setOnItemClickListener(new KanbanDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {

            }
        });
    }

    boolean isSame = false;
    List<String> SList = new ArrayList<>();
    private void getBean(){
        HttpUtils.getInstance().getOkHttpClient().newCall(new Request.Builder().url(Constants.url_kanban).build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("tag","Bean连接服务失败");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"连接服务失败",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    final String res = response.body().string();
                    Log.d("tag", "onResponse:= " + res);
                    final CheckBarcodeResult rBean = new Gson().fromJson(res, CheckBarcodeResult.class);
                    if (rBean.getState() == 0){
                        final List<CheckBarcodeResult.beanData> data = rBean.getDate();
                        final String t = rBean.getDate1();
                        if (t != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String dates = t.split("&")[0];
                                    String shi = t.split("&")[1];
                                    String times = shi.substring(0,shi.lastIndexOf(":"));
                                    time.setText(times);
                                    date.setText(dates);
                                }
                            });
                        }

                        if (lList != null && lList.size() != 0){
                            lList.clear();
                        }
                        if (kList != null && kList.size() != 0){
                            kList.clear();
                        }

                        if (0 != data.size() && data != null){
                            List<String> sList = new ArrayList<>();
                            sList.add("无");
                            for (int i = 0; i < data.size(); i++) {
                                String lStr = data.get(i).getLine();
                                lList.add(lStr);
                                List<CheckBarcodeResult.boardsData> boardsData = data.get(i).getProductBoards();
                                for (int j = 0; j < boardsData.size(); j++) {
                                    String productxt = boardsData.get(j).getProductxt();
                                    String orderid = boardsData.get(j).getOrderid();
                                    String customername = boardsData.get(j).getCustomername();
                                    String model = boardsData.get(j).getModel();
                                    String motherbtype = boardsData.get(j).getMotherbtype();
                                    String ordernumber = String.valueOf(boardsData.get(j).getOrdernumber());
                                    String prodnumber = boardsData.get(j).getProdnumber();
                                    String prodnumber_in = boardsData.get(j).getProdnumber_in();
                                    String prod_Prodnumber = boardsData.get(j).getProd_Prodnumber();
                                    String cumulat_number = String.valueOf(boardsData.get(j).getCumulat_number());
                                    String cumulat_Prodnumber = String.valueOf(boardsData.get(j).getCumulat_Prodnumber());
                                    String order_shortage = String.valueOf(boardsData.get(j).getOrder_shortage());
                                    String remarks = boardsData.get(j).getRemarks();

                                    sList.add(orderid);

                                    List<String> list = new ArrayList<>();
                                    list.add(lStr);
                                    list.add(productxt);
                                    list.add(orderid);
                                    list.add(customername);
                                    list.add(model);
                                    list.add(motherbtype);
                                    list.add(ordernumber);
                                    list.add(prodnumber);
                                    list.add(prodnumber_in);
                                    list.add(prod_Prodnumber);
                                    list.add(cumulat_number);
                                    list.add(cumulat_Prodnumber);
                                    list.add(order_shortage);
                                    list.add(remarks);
                                    kList.add(list);
                                }
                            }
                            //每一条产线相同的数量
                            final Map<String,Integer> map = new HashMap<>();
                            for (String str:lList) {
                                int s = 1;
                                if (map.get(str) != null){
                                    s = map.get(str) + 1;
                                }
                                map.put(str,s);
                            }
                            //产线去重
                            lList = removeDuplicate(lList);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        initAdapter();
//                                        Log.e("Tag", "ordir=" + ordir);
                                        lAdapter.setSelection(map);
                                        if (ordir != null){
                                            kAdapter.getFilter().filter(ordir);
                                        }
                                }
                            });
                            //订单号去重
                            SList = removeDuplicate(SList);
                            sList = removeDuplicate(sList);

                            //判断获取到的订单号是否一致
                            isSame = true;
                            if (SList.size() != 0 && SList.size() == sList.size()) {
                                for (int i = 0; i < SList.size(); i++) {
                                    if (SList.get(i).equals(sList.get(i))){

                                    }else {
                                        isSame = false;
                                    }
                                }
                            }else {
                                isSame = false;
                                SList = sList;
                            }

                             if (!isSame) {
                                 runOnUiThread(new Runnable() {
                                     @Override
                                     public void run() {
                                         ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_item, SList);
                                         dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                         spinner.setAdapter(dataAdapter);
                                     }
                                 });
                             }

                        }
                    }
                }
            }
        });
    }

    /**
     * 对产线集合去重
     * @param list
     * @return
     */
    public static List<String> removeDuplicate(List<String> list) {
        Set set = new LinkedHashSet<String>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }


    /**
     * 检查更新版本
     */
    private void checkVersion() {
        isFirst = true;
        String url = Constants.url_version_updating + "?soft_name=" + soft_name + "&soft_version=" + BuildConfig.VERSION_NAME;
        HttpUtils.getInstance().getOkHttpClient().newCall(new Request.Builder()
                .url(url).build()).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("scanner", "网络访问失败!");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String res = response.body().string();
                            if (res != null && response.isSuccessful()) {
//                                Log.e(TAG, "onResponse:version= " + response + "   res=" + res);
                                final RecommendversionBean versionBean = new Gson().fromJson(res, RecommendversionBean.class);
                                if (versionBean.getCode() == 1) {
                                    final RecommendversionBean.DataBean data = versionBean.getData();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new AlertDialog.Builder(MainActivity.this)
                                                    .setTitle("版本更新")
                                                    .setMessage(versionBean.getMsg())
                                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();
                                                        }
                                                    })
                                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            Toast.makeText(MainActivity.this, "后台下载中...", Toast.LENGTH_SHORT).show();
                                                            downloadApk(data.getSoft_url());
                                                        }
                                                    }).show();
                                        }
                                    });
                                }
                            }
                        }
                });
    }

    /**
     * 普通下载apk安装
     *
     * @param url
     */
    private void downloadApk(final String url) {

        HttpUtils.getInstance().getOkHttpClient().newCall(new Request.Builder()
                .url(url).build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this, "下载失败!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                if (res != null && response.isSuccessful()) {
                    Log.e(TAG, "onResponse:version= " + response + "   res=" + res);
                        InputStream inputStream = response.body().byteStream();

                        final String filePath = AppManager.getAppDir() + url.substring(url.lastIndexOf("/") + 1);
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(filePath);
                            int len = 0;
                            byte[] buffer = new byte[1024 * 10];
                            while ((len = inputStream.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
                            }
                            fos.flush();
                            fos.close();
                            inputStream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //安装
                                AppManager.install(MainActivity.this, filePath);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "下载失败!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
            }
        });
    }

    @Override
    public void onNetState(boolean isConnected, int type) {
//         boolean b = NetTool.isNetworkOK();
        Log.e("Tag","isConnected="+isConnected+"    type="+type+"       "+Utils.getDevID().toUpperCase());
         if (isConnected){
             checkMac(Utils.getDevID().toUpperCase());
         }
    }

    @Override
    public void wifiLevel(int level) {

    }


    /**
     * 检查mac是否可用
     */
    private void checkMac(String mac) {
        String url = Constants.url_mac_verify+"?mac1="+mac;
        HttpUtils.getInstance().getOkHttpClient().newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("tag", "访问失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    String res = response.body().string();
                    Log.e("Tag", "res=" + res);
                    final CheckMacBean checkMacBean = new Gson().fromJson(res, CheckMacBean.class);
                    if (checkMacBean.getState() != 0) {
                        //设备不可用
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle(R.string.hint)
                                        .setMessage(checkMacBean.getMsg()+"\nMAC:  "+Utils.getDevID().toUpperCase())
                                        .setCancelable(false)
                                        .show();
                            }
                        });
                    } else {//设备可用时加载数据
                        thread.start();
                    }
                    if (!isFirst) {
                        checkVersion();
                    }
                }

            }
        });
    }

}
