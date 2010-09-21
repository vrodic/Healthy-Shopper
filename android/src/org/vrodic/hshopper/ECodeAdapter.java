/*
 * This file is distributed under GPL v3 license 
 * http://www.gnu.org/licenses/gpl-3.0.html      
 *                                               
 * raven@acute-angle.net                         
 */

package org.vrodic.hshopper;

import java.util.ArrayList;
import java.util.List;

import org.vrodic.hshopper.R;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class ECodeAdapter implements ListAdapter, ECodeListObserver {
	
	private ECodeList list;
	private List<DataSetObserver> observers;
	private LayoutInflater inflater;
	private Context context;

	public ECodeAdapter(Context context, ECodeList list) {
		this.list = list;
		this.context = context;
		this.observers = new ArrayList<DataSetObserver>();
		inflater = LayoutInflater.from(context);
		
		this.list.setObserver(this);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		if (convertView == null)
			v = inflater.inflate(R.layout.ecode, null);
		else
			v = convertView;
		
		ECode code = list.get(position);		
		
		TextView tvcode = (TextView) v.findViewById(R.id.ecode);
		TextView tvname = (TextView) v.findViewById(R.id.name);
		TextView tvpurpose = (TextView) v.findViewById(R.id.purpose);
		TextView tvstatus = (TextView) v.findViewById(R.id.status);
		ImageView ivegan = (ImageView) v.findViewById(R.id.veganIcon);
		ImageView ichild = (ImageView) v.findViewById(R.id.childIcon);
		ImageView iallerg = (ImageView) v.findViewById(R.id.allergyIcon);
		ImageView iextra = (ImageView) v.findViewById(R.id.iIcon);
		
		
		tvcode.setBackgroundColor(code.getColor());
		tvcode.setText("E" + code.eCode);
		tvname.setText(code.name);
		tvpurpose.setText(code.purpose);
		tvstatus.setText(getCaption(code));

		ivegan.setImageResource(code.vegan == 0 ? R.drawable.v : 
			(code.vegan == 2 ? R.drawable.v_y : R.drawable.v_r));
		ichild.setImageResource(code.children == 0 ? R.drawable.ch : R.drawable.ch_r);
		iallerg.setImageResource(code.allergic ? R.drawable.al_r : R.drawable.al);
		
		/*ivegan.setVisibility(code.safeForVegans() ? View.VISIBLE: View.GONE);
		ichild.setVisibility(code.safeForChildren() ? View.VISIBLE: View.GONE);
		iallerg.setVisibility(code.safeForAllergic() ? View.VISIBLE: View.GONE);*/
		iextra.setVisibility(code.hasExtra() ? View.VISIBLE: View.GONE);		
		
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
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
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
		return list.size() == 0;
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
