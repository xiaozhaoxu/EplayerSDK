package cn.nodemedia.ibrightech.eplayersdkproject.widget.spinner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.nodemedia.ibrightech.eplayersdkproject.R;


/**
 * @author angelo.marchesin
 */

@SuppressWarnings("unused")
public abstract class NiceSpinnerBaseAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected int mSelectedIndex;
    protected int mTextColor;
    protected int mBackgroundSelector;

    public NiceSpinnerBaseAdapter(Context context, int textColor, int backgroundSelector) {
        mContext = context;
        mTextColor = textColor;
        mBackgroundSelector = backgroundSelector;
    }

    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        ImageView img_select_icon;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.sdk_spinner_list_item, null);
            textView = (TextView) convertView.findViewById(R.id.tv_tinted_spinner);
            img_select_icon= (ImageView) convertView.findViewById(R.id.img_select_icon);


            convertView.setTag(new ViewHolder(textView,img_select_icon));
        } else {
            textView = ((ViewHolder) convertView.getTag()).textView;
            img_select_icon= ((ViewHolder) convertView.getTag()).img_select_icon;
        }

        textView.setText(getItem(position).toString());
        textView.setTextColor(mTextColor);

        if(position==mSelectedIndex){
            img_select_icon.setVisibility(View.VISIBLE);
        }else{
            img_select_icon.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void notifyItemSelected(int index) {
        mSelectedIndex = index;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract T getItem(int position);

    @Override
    public abstract int getCount();

    public abstract T getItemInDataset(int position);

    protected static class ViewHolder {

        public TextView textView;
        public ImageView img_select_icon;

        public ViewHolder(TextView textView,ImageView img_select_icon) {
            this.textView = textView;
            this.img_select_icon=img_select_icon;
        }
    }
}
