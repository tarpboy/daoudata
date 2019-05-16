package com.devcrane.payfun.daou.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.devcrane.payfun.daou.R;
import com.devcrane.payfun.daou.caller.ReqPara;
import com.devcrane.payfun.daou.entity.ReceiptEntity;
import com.devcrane.payfun.daou.manager.ReceiptManager;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.Helper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 6/2/17.
 */

public class CancelListDialog extends Dialog implements View.OnClickListener{
    final String TAG = getClass().getSimpleName();
    private Context mContext;
    private CancelListDialogListener onCancelListDialogListener;
    private ReqPara reqPara;
    private String cardNo;
    private ReceiptEntity selectedItem;
    private ArrayList<ReceiptEntity> mList1 = new ArrayList<ReceiptEntity>();
    private CancelAdapter mAdapter1;
    private ListView mListView1;
    public CancelListDialog(Context ctx, String cardNo, CancelListDialogListener listener){
        super(ctx);
        mContext = ctx;
        onCancelListDialogListener = listener;
        this.cardNo = cardNo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_cancel_list);
        getData();
    }
    void initView(){
        Button btOk = (Button) findViewById(R.id.bt_ok);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedItem==null){
                    BHelper.showToast(R.string.msg_select_at_least_one_item);
                    return;
                }
                onCancelListDialogListener.CancelListDialogEvent(selectedItem);
                dismiss();
            }
        });
        Button btCancel = (Button) findViewById(R.id.bt_cancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mListView1 = (ListView) findViewById(R.id.lv_cancel);
        mAdapter1 = new CancelAdapter(mContext, mList1);
        mListView1.setAdapter(mAdapter1);
        mAdapter1.notifyDataSetChanged();
        mListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                mAdapter1.notifyDataSetChanged();
                ReceiptEntity entity = mList1.get(position);
                selectedItem = entity;
                BHelper.db("selected item:"+ selectedItem.toString());
            }
        });
    }

    void getData(){
        mList1.clear();
        List<ReceiptEntity> list = ReceiptManager.getCancelListByCardNo(cardNo);
        if(list!=null){
            int size = list.size();
            for(int i=0;i< size;i++){
                ReceiptEntity receiptEntity = list.get(i);
                BHelper.db("receiptEntity:"+ receiptEntity.toString());
                mList1.add(receiptEntity);
            }
        }

        initView();
    }

    @Override
    public void onClick(View v) {

    }
    class CancelAdapter extends BaseAdapter{
        final String TAG = getClass().getSimpleName();
        private Context mContext;
        private ArrayList<ReceiptEntity> mList;

        public CancelAdapter(Context context, ArrayList<ReceiptEntity> list) {
            mContext = context;
            mList = list;
        }


        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_cancel_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.tvApprovalNo = (TextView)convertView.findViewById(R.id.tv_cancel_approval_no);
                viewHolder.tvCardNo = (TextView)convertView.findViewById(R.id.tv_cancel_card_no);
                viewHolder.tvAmount = (TextView)convertView.findViewById(R.id.tv_cancel_amount);
                viewHolder.tvDate = (TextView)convertView.findViewById(R.id.tv_cancel_date);
                viewHolder.tvTime = (TextView)convertView.findViewById(R.id.tv_cancel_time);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvApprovalNo.setText("승인번호: "+mList.get(position).getF_ApprovalCode());
            viewHolder.tvCardNo.setText(mList.get(position).getF_CardNo());
            String amount = Helper.formatNumberExcel(mList.get(position).getF_TotalAmount())+"원";
            viewHolder.tvAmount.setText(amount);
            String[] dates = mList.get(position).getF_RequestDate().split(" ");
            viewHolder.tvDate.setText(dates[0]);
            viewHolder.tvTime.setText(dates[1]);
            return convertView;
        }


        class ViewHolder {
            TextView tvApprovalNo;
            TextView tvCardNo;
            TextView tvAmount;
            TextView tvDate;
            TextView tvTime;
        }
    }
}
