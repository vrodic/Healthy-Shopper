/*
 * This file is distributed under GPL v3 license 
 * http://www.gnu.org/licenses/gpl-3.0.html      
 *                                               
 * vrodic@gmail.com                         
 */

package org.vrodic.hshopper;

import java.util.ArrayList;
import java.util.List;

import org.vrodic.hshopper.R;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class ECodeProductAdapter implements ListAdapter, ECodeListObserver {
	
	private List<DataSetObserver> observers;
	private LayoutInflater inflater;
	private Context context;
	private Cursor mCursor; 
	private ECodeList elist;

	public ECodeProductAdapter(Context context, ECodeList list, 	Cursor mCursor) {
		this.mCursor = mCursor;
		this.context = context;
		this.elist = list;
		
		this.observers = new ArrayList<DataSetObserver>();
		inflater = LayoutInflater.from(context);
		
	//	this.list.setObserver(this);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		if (convertView == null)
			v = inflater.inflate(R.layout.eitem, null);
		else
			v = convertView;
		
		mCursor.moveToPosition(position);
		
		String code = mCursor.getString(1);
		//System.out.println("Position: " +position + " code " + code);
		ECode ecode = elist.getEcode(code);
				
		
		TextView tvcode = (TextView) v.findViewById(R.id.text1);
	if (tvcode != null && ecode != null) {
		
		
		tvcode.setBackgroundColor(ecode.getColor());
		tvcode.setText("E" + ecode.eCode);
	}
			
		return v;
	}
	
	public String getCaption(ECode code) {
		switch (code.getDanger()) {
		case ECode.PERMITTED:
			return context.getString(R.string.cap_permitted);
		case ECode.UNPERMITTED:
			return context.getString(R.string.cap_unpermitted);
		case ECode.MOSTLY_SAFE:
			return context.getString(R.string.cap_forbidden);
		case ECode.DANGEROUS:
			return context.getString(R.string.cap_dangerous);
		}
		return context.getString(R.string.cap_unknown);
	}	

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}

	@Override
	public int getCount() {
		return mCursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		mCursor.moveToPosition(position);
		return mCursor.getString(1);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return mCursor.getCount() == 0;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		observers.add(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		int idx = observers.indexOf(observer);
		if (idx >=0)
			observers.remove(idx);
	}

	@Override
	public void dataChanged() {
		for (DataSetObserver observer: observers)
			observer.onChanged();
	}
	
	
}
