package com.example.administrator.kanbansystem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KanbanDataAdapter extends RecyclerView.Adapter<KanbanDataAdapter.TextHolder> implements Filterable {

    private OnItemClickListener mClickListener;
    private Context mContext;
    private List<List<String>> beans;
    private static int selected = -1;
    private FilterListener listener = null;// 接口对象
    private MyFilter filter = null;// 创建MyFilter对象


   public KanbanDataAdapter(List<List<String>> list, Context context,FilterListener filterListener){
           this.mContext = context;
           this.beans = list;
           this.listener = filterListener;
   }

    public void setSelection(int position){
        this.selected = position;
    }


    @Override
    public TextHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.kanban_data_itme, parent, false);   //获取视图
        return new TextHolder(view,mClickListener,mContext);
    }

    @Override
    public void onBindViewHolder(TextHolder holder, int position) {
        List<String> str = beans.get(position);
        holder.bindData(str);
    }


    @Override
    public int getItemCount() {
        //获取项目数量
        return beans == null ?0 : beans.size();
    }


    @Override
    public Filter getFilter() {
        // 如果MyFilter对象为空，那么重写创建一个
        if (filter == null) {
            filter = new MyFilter(beans);
        }
        return filter;

    }


    class MyFilter extends Filter {
        // 创建集合保存原始数据
        private List<List<String>> original;

        public MyFilter(List<List<String>> list) {
            this.original = list;
        }

        /**
         * 该方法返回搜索过滤后的数据
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // 创建FilterResults对象
            FilterResults results = new FilterResults();

            /**
             * 没有搜索内容的话就还是给results赋值原始数据的值和大小
             * 执行了搜索的话，根据搜索的规则过滤即可，最后把过滤后的数据的值和大小赋值给results
             *
             */
            if (TextUtils.isEmpty(constraint) || constraint.equals("无")) {
                results.values = original;
                results.count = original.size();
//                Log.e("Tag","original="+original);
            } else {
                // 创建集合保存过滤后的数据
                List<List<String>> mList = new ArrayList<>();
//                Log.e("Tag","original111="+original+"   constraint="+constraint);
                // 遍历原始数据集合，根据搜索的规则过滤数据
                for (List<String> s : original) {
                    // 这里就是过滤规则的具体实现【规则有很多，大家可以自己决定怎么实现】
                        if (s.get(2).equals(constraint)) {
                            // 规则匹配的话就往集合中添加该数据
                           mList.add(s);
                        }
                }
                results.values = mList;
                results.count = mList.size();
            }

            // 返回FilterResults对象
            return results;

        }

        /**
         * 该方法用来刷新用户界面，根据过滤后的数据重新展示列表
         */

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // 获取过滤后的数据
            Gson gson = new Gson();
            List<List<String>> listList = gson.fromJson(String.valueOf(results.values),new TypeToken<List<List<String>>>(){
            }.getType());
//            Log.e("Tag","listList="+listList);
            List<String> list = new ArrayList<>();
            for (int i = 0; i < listList.size(); i++) {
                 list.add(listList.get(i).get(0));
            }
            //更新列表数据
            setDataList(listList);
            // 如果接口对象不为空，那么调用接口中的方法获取过滤后的数据，具体的实现在new这个接口的时候重写的方法里执行
            if (listener != null) {
                listener.getFilterData(listList);
            }
            // 刷新数据源显示
            notifyDataSetChanged();
        }
    }


    public static class TextHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView order, customer, type, model, order_number, production_number,
                production_input_number, production_output_number, cumulative_input_number,
                cumulative_output_number, owe, remark;
        private OnItemClickListener mListener;// 声明自定义的接口


        public TextHolder(View view, OnItemClickListener mListener, Context mContext) {
            super(view);
            this.mListener = mListener;
            order = view.findViewById(R.id.tv_order);
            customer = view.findViewById(R.id.tv_customer);
            type = view.findViewById(R.id.tv_type);
            model = view.findViewById(R.id.tv_model);
            order_number = view.findViewById(R.id.tv_order_number);
            production_number = view.findViewById(R.id.tv_production_number);
            production_input_number = view.findViewById(R.id.tv_production_input_number);
            production_output_number = view.findViewById(R.id.tv_production_output_number);
            cumulative_input_number = view.findViewById(R.id.tv_cumulative_input_number);
            cumulative_output_number = view.findViewById(R.id.tv_cumulative_output_number);
            owe = view.findViewById(R.id.tv_owe);
            remark = view.findViewById(R.id.tv_remark);

            // 为View添加点击事件
            view.setOnClickListener(this);
        }

        public void bindData(List<String> str) {
            order.setText(str.get(2));
            customer.setText(str.get(3));
            type.setText(str.get(4));
            model.setText(str.get(5));
            order_number.setText(str.get(6));
            production_number.setText(str.get(7));
            production_input_number.setText(str.get(8));
            production_output_number.setText(str.get(9));
            cumulative_input_number.setText(str.get(10));
            cumulative_output_number.setText(str.get(11));
            owe.setText(str.get(12));
            remark.setText(str.get(13));
        }


        @Override
        public void onClick(View v) {
            // getpostion()为Viewholder自带的一个方法，用来获取RecyclerView当前的位置，将此作为参数，传出去
            mListener.onItemClick(v, getPosition());
//            if (iv != null && getOldPosition() != getPosition()){
//                iv.setImageResource(R.drawable.button);
//            }
//            Log.e("TAG","selected="+selected+"     getPosition()="+getPosition());
//            if (getPosition() == selected){
//                bg.setImageResource(R.drawable.choose);
//            }
//            iv = bg;
        }

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mClickListener = listener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int postion);
    }
    public void setDataList(List<List<String>> ls) {
        this.beans = ls;
        this.notifyDataSetChanged();
    }

    public void deleteItem(int p){
        this.beans.remove(p);
        this.notifyDataSetChanged();
    }


    public List<List<String>> getDateList(){
        return this.beans;
    }


}
