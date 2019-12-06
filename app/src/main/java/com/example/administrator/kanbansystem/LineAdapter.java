package com.example.administrator.kanbansystem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class LineAdapter extends RecyclerView.Adapter<LineAdapter.TextHolder> {

    private OnItemClickListener mClickListener;
    private Context mContext;
    private List<String> beans;
    private static Map<String,Integer> lineName;
    private static int h;
    private static int w;
    private static boolean isFirst = true;

   public LineAdapter(List<String> list, Context context){
           this.mContext = context;
           this.beans = list;
   }

    public void setSelection(Map<String,Integer> name){
        this.lineName = name;
    }

    @Override
    public TextHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.line_itme, parent, false);   //获取视图
        return new TextHolder(view,mClickListener,mContext);
    }

    @Override
    public void onBindViewHolder(TextHolder holder, int position) {
        String str = beans.get(position);
        holder.bindData(str);
        
    }

    @Override
    public int getItemCount() {
        //获取项目数量
        return beans == null ?0 : beans.size();
    }

    public static class TextHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView line;
        private LinearLayout ll;
        private View vvv;
        private OnItemClickListener mListener;// 声明自定义的接口


        public TextHolder(View view, OnItemClickListener mListener, Context mContext) {
            super(view);
            this.mListener = mListener;

            line = view.findViewById(R.id.tv_line);
            ll = view.findViewById(R.id.ll_line);
            vvv = view.findViewById(R.id.vvv);
            // 为View添加点击事件
            view.setOnClickListener(this);
        }

        public void bindData(String str){
            int sameNumber;
            for (String s:lineName.keySet()) {
                 if ("".equals(s) || null == s){
                     lineName.remove(s);
                 }
            }
//            Log.e("Tag","lineName="+lineName+"    str="+str);
            if (null != lineName && lineName.size() != 0) {
                sameNumber = lineName.get(str);
            }else {
                sameNumber = 0;
            }
//            Log.e("Tag","sameNumber="+sameNumber);
                line.setText(str);
                if (isFirst) {
                    isFirst = false;
                    h = ll.getLayoutParams().height;
                    w = vvv.getLayoutParams().height;
                }

                ViewGroup.LayoutParams params = line.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = sameNumber*h-w;
                line.setLayoutParams(params);

                ViewGroup.LayoutParams params1 = ll.getLayoutParams();
                params1.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params1.height =  sameNumber*h;
                ll.setLayoutParams(params1);
        }


        @Override
        public void onClick(View v) {
            // getpostion()为Viewholder自带的一个方法，用来获取RecyclerView当前的位置，将此作为参数，传出去
            mListener.onItemClick(v, getPosition());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mClickListener = listener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int postion);
    }
    public void setDataList(List<String> ls) {
        this.beans = ls;
        this.notifyDataSetChanged();
    }

    public void deleteItem(int p){
        this.beans.remove(p);
        this.notifyDataSetChanged();
    }


    public List<String> getDateList(){
        return this.beans;
    }
}
