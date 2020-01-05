package com.uom.happycelebrate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.andremion.floatingnavigationview.FloatingNavigationView;
import com.squareup.picasso.Picasso;
import com.uom.happycelebrate.R;
import com.uom.happycelebrate.models.Card;
import java.util.ArrayList;

public class CustomVehicleAdapter extends ArrayAdapter<Card> implements View.OnClickListener{

    private ArrayList<Card> dataSet;
    Context mContext;
    private FloatingNavigationView mFloatingNavigationView;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtType;
//        TextView txtVersion;
        ImageView info;
    }

    public CustomVehicleAdapter(ArrayList<Card> data, Context context) {
        super(context, R.layout.card_row, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
//        DataModel dataModel=(DataModel)object;
//
//        switch (v.getId())
//        {
//            case R.id.item_info:
//                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
//                break;
//        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Card dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.card_row, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.text);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.sample);
//            viewHolder.txtVersion = (TextView) convertView.findViewById(R.id.version_number);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.photo);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        System.out.println(dataModel.getImage_url());

        Picasso.get().load(dataModel.getImage_url()).into(viewHolder.info);

        viewHolder.txtName.setText("CATAGORY : "+dataModel.getDesigner_id());
        viewHolder.txtType.setText(dataModel.getDescription());

//        viewHolder.txtVersion.setText(dataModel.getId());

        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}