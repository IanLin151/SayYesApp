package org.altbeacon.beaconreference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable {
    private Context context;

    /**上方的arrayList為RecyclerView所綁定的ArrayList*/
    ArrayList<HashMap<String,String>> arrayList;
    /**儲存最原先ArrayList的狀態(也就是充當回複RecyclerView最原先狀態的陣列)*/
    ArrayList<HashMap<String,String>> arrayListFilter;
    FoodActivity mainActivity;
    public RecyclerViewAdapter(ArrayList<HashMap<String,String>> arrayList, FoodActivity mActivity) {
        mainActivity=mActivity;
        this.arrayList = arrayList;
        arrayListFilter = new ArrayList<HashMap<String,String>>();
        /**這裡把初始陣列複製進去了*/
        arrayListFilter.addAll(arrayList);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvPos,tvType,tvPrice,tvCar,tvDateTime;
        ImageView tvImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPos = itemView.findViewById(R.id.textView_pos);
            tvType = itemView.findViewById(R.id.textView_type);
            tvPrice = itemView.findViewById(R.id.textView_price);
            tvCar = itemView.findViewById(R.id.textView_car);
            tvDateTime = itemView.findViewById(R.id.textView_time);
            tvImageView = itemView.findViewById(R.id.imageView2);
            /**點擊事件*/
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create(); //Read Update
            alertDialog.setTitle("商家名稱:  " + arrayList.get(getAdapterPosition()).get("shop_name"));
            alertDialog.setMessage("商家地址:  " + arrayList.get(getAdapterPosition()).get("user_location") + "\n"
                    + "商家資訊:  " + arrayList.get(getAdapterPosition()).get("user_introduce") + "\n"
                    + "用餐方式: " + arrayList.get(getAdapterPosition()).get("meal") + "\n"
                    + "菜單:"+ arrayList.get(getAdapterPosition()).get("user_menu") );

            alertDialog.setButton("關閉", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // here you can add functions
                }
            });
            alertDialog.show();
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvPos.setText(arrayList.get(position).get("shop_name"));
        holder.tvType.setText("類型："+arrayList.get(position).get("user_label"));
        holder.tvPrice.setText("電話："+arrayList.get(position).get("user_number"));
        holder.tvCar.setText("開始營業時間："+arrayList.get(position).get("user_time_open"));
        holder.tvDateTime.setText("結束營業時間："+arrayList.get(position).get("user_time_close"));
        try {
            byte[] decodedString = Base64.decode(arrayList.get(position).get("picture"), Base64.DEFAULT);
            Bitmap imgBitMap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            //Glide.with(mCtx).load(imgBitMap).into(viewHolder.imageView);
            holder.tvImageView.setImageBitmap(imgBitMap);
            // viewHolder.imageView.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }
    /**使用Filter濾除方法*/
    Filter mFilter = new Filter() {
        /**此處為正在濾除字串時所做的事*/
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            /**先將完整陣列複製過去*/
            ArrayList<HashMap<String,String>> filteredList = new ArrayList<HashMap<String,String>>();
            /**如果沒有輸入，則將原本的陣列帶入*/
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(arrayListFilter);
            } else {
                /**如果有輸入，則照順序濾除相關字串
                 * 如果你有更好的搜尋演算法，就是寫在這邊*/

                for (int i = 0; i < arrayListFilter.size(); i++) {

                    HashMap<String,String> content = (HashMap<String,String>) arrayListFilter.get(i);
                    //無須打全名就能出現相關資料
                    for(HashMap.Entry<String, String> entry : content.entrySet())
                    {

                        if(entry.getValue().contains(constraint))
                        {
                            filteredList.add(content);
                            break;
                        }
                    }



                    //字形完全符合才出現
                    //    if (content.containsValue(constraint)) {
                //        filteredList.add(content);
                //    }

                }
            }
            /**回傳濾除結果*/
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }
        /**將濾除結果推給原先RecyclerView綁定的陣列，並通知RecyclerView刷新*/
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList.clear();
            arrayList.addAll((Collection<? extends  HashMap<String,String>>) results.values);
            notifyDataSetChanged();
        }
    };
}
