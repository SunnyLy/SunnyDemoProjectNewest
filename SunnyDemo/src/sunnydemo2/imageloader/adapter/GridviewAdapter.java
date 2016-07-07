package sunnydemo2.imageloader.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.smartbracelet.sunny.sunnydemo2.R;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import sunnydemo2.utils.LogUtils;

/**
 * Created by sunny on 2015/12/21.
 * Annotion:
 */
public class GridviewAdapter extends BaseAdapter {
    private List<String> mImageLists = new ArrayList<>();
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;

    public GridviewAdapter(Context context, List<String> imageList, View.OnClickListener clickListener) {
        mInflater = LayoutInflater.from(context);
        this.mOnClickListener = clickListener;
        if (imageList != null && imageList.size() > 0) {
            mImageLists.clear();
            mImageLists.addAll(imageList);
        }

    }

    @Override
    public int getCount() {
        return mImageLists.size();
    }

    @Override
    public String getItem(int position) {
        return mImageLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_choose_picture_toshow, null);
            holder = new ViewHold();
            holder.mPicture = (SimpleDraweeView) convertView.findViewById(R.id.picture_show);
            holder.mPictureInfo = (TextView) convertView.findViewById(R.id.picture_title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHold) convertView.getTag();
        }

        if (position == 0) {
            holder.mPictureInfo.setVisibility(View.VISIBLE);
            holder.mPicture.setImageURI(Uri.parse("res://drawable/" + R.drawable.takephoto));
        } else {
            //  holder.mPicture.setImageURI(Uri.parse("file://" + mImageLists.get(position)));
            LogUtils.e("position:" + position + ",url:content://" + mImageLists.get(position));
            holder.mPictureInfo.setVisibility(View.GONE);

            showImage(mImageLists, position, holder.mPicture);

        }
        holder.mPicture.setOnClickListener(mOnClickListener);
        return convertView;

    }

    /**
     * 显示图片
     *
     * @param mPicture
     */
    private void showImage(List<String> mImageLists, int position, final SimpleDraweeView mPicture) {
        Observable.from(new String[]{mImageLists.get(position)})
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String url) {
                        return !url.startsWith("http://")||!url.startsWith("https://");
                    }
                })
                .map(new Func1<String , Uri>() {//map()是一对一的转换，flatMap()是多对一的转换，就是替换for()循环
                    @Override
                    public Uri call(String s) {
                        return Uri.parse("file://"+s);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Uri>() {
                    @Override
                    public void call(Uri uri) {
                        mPicture.setImageURI(uri);
                    }
                });

        //下面是Java8上lambda的用法，代码要简洁很多，
        //但不知道是java8的bug还是IDE的bug,报编译错误，
      /*  Observable.from(new String[]{mImageLists.get(position)})
                .filter(url -> !url.startsWith("http://") || !url.startsWith("https://"))
                .map(s -> Uri.parse("file://" + s))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(uri -> mPicture.setImageURI(uri));*/

    }


    private class ViewHold {

        SimpleDraweeView mPicture;
        TextView mPictureInfo;
    }
}
