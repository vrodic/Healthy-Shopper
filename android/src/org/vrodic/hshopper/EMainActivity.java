/*
 * This file is distributed under GPL v3 license 
 * http://www.gnu.org/licenses/gpl-3.0.html      
 *                                               
 * raven@acute-angle.net                         
 */


package org.vrodic.hshopper;

import org.vrodic.hshopper.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class EMainActivity extends ListActivity implements TextWatcher {
	private static final int MENU_HELP = 1;

	private EditText searchText;
	private ECodeList allECodes;
	private ECodeList selectedECodes;
	private ECodeAdapter adapter;

	//private AdView adView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		searchText = (EditText) findViewById(R.id.searchString);

		searchText.addTextChangedListener(this);

		allECodes = new ECodeList();
		allECodes.load(this);

		selectedECodes = new ECodeList();
		selectedECodes.addAll(allECodes);

		adapter = new ECodeAdapter(this, selectedECodes);
		this.setListAdapter(adapter);

		//installAdView();
	}

	private void searchForECodes() {
		String text = searchText.getText().toString().trim();
		String[] codes = text.split(" ");

		allECodes.filter(codes, selectedECodes);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		MenuItem help = menu.add(Menu.NONE, MENU_HELP, Menu.NONE,
				R.string.menu_help);
		help.setIcon(R.drawable.help);
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == MENU_HELP) {
			startActivity(new Intent(this, EHelpActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		ECode code = selectedECodes.get(position);
		Intent intent = new Intent(this, ECodeViewActivity.class);
		intent.putExtra("ecode", code);
		startActivity(intent);
	}

	@Override
	public void afterTextChanged(Editable s) {
		searchForECodes();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	/*private void installAdView() {
		LinearLayout layout = (LinearLayout) findViewById(R.id.mainlayout);			
		adView = new AdView(this);
		LayoutParams p = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		adView.setLayoutParams(p);
		if (adView != null) {
			layout.addView(adView);					
			adView.requestFreshAd();
		}

	}*/

}