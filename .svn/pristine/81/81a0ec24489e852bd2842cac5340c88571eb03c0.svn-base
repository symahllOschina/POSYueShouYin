package com.wanding.xingpos.card.stock.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wanding.xingpos.R;
import com.wanding.xingpos.card.stock.bean.CheckCardStockRecodeDetailResData;

import java.util.List;

public class CheckCardStockRecodeListAdapter extends BaseAdapter {

    private Context context;
    private List<CheckCardStockRecodeDetailResData> list;
    private LayoutInflater inflater;

    public CheckCardStockRecodeListAdapter(Context context, List<CheckCardStockRecodeDetailResData> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        LinearLayout layout;
        TextView tvName;//卡劵名称
        TextView tvCode;//核销代码
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.check_card_stock_recode_list_item, null);
            holder.layout=(LinearLayout) convertView.findViewById(R.id.write_off_recode_item_layout);
            holder.tvName=(TextView) convertView.findViewById(R.id.check_card_stock_recode_item_tvName);
            holder.tvCode=(TextView) convertView.findViewById(R.id.check_card_stock_recode_item_tvCode);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        if(position%2!=0){
            holder.layout.setBackgroundColor(ContextCompat.getColor(context,R.color.white_f8f8f8));
        }else{
            holder.layout.setBackgroundColor(ContextCompat.getColor(context,R.color.white_ffffff));
        }
        holder.tvName.setText(list.get(position).getTitle());
        holder.tvCode.setText(list.get(position).getCode());
        return convertView;
    }
}
