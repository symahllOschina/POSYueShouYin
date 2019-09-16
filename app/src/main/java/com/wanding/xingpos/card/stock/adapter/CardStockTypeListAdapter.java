package com.wanding.xingpos.card.stock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wanding.xingpos.R;
import com.wanding.xingpos.card.stock.bean.CardStockTypeResData;

import java.util.List;

public class CardStockTypeListAdapter extends BaseAdapter {

    private Context context;
    private List<CardStockTypeResData> list;
    private LayoutInflater inflater;

    public CardStockTypeListAdapter(Context context, List<CardStockTypeResData> list) {
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
        private TextView tvName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.card_stock_type_list_item, null);
            holder.tvName=(TextView) convertView.findViewById(R.id.card_stock_type_list_item_tvName);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        holder.tvName.setText(list.get(position).getTitle());
        return convertView;
    }
}
