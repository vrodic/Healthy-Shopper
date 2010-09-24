package org.vrodic.hshopper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class HProductAdditivesActivity extends ListActivity implements
		TextWatcher {

	private EditText searchText;
	private Button button;
	private EditText nameText;
	// private ECodeProductAdapter ep;

	private HShopperDbAdapter mDbHelper;
	private Cursor mCursor;
	private String productId = "00000000";

	private ECodeList allECodes;
	private ECodeList selectedECodes;
	private ECodeAdapter adapter;

	private boolean searchingCodes = false;

	private static final int ACTIVITY_ADDNEW = 1;
	public static final int NEW_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int MANUAL_BARCODE_ID = Menu.FIRST + 2;
	private static final int EXPORT_DB_TO_SD_ID = Menu.FIRST + 3;
	private static final int EXPORT_DBXML_TO_SD_ID = Menu.FIRST + 4;

	private void barcodeInputDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Manual entry");
		alert.setMessage("Barcode");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setMaxLines(1);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();

				// Do something with value!
				System.out.println(value);
				productId = value;
				initProduct(productId);
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		alert.show();
	}

	private void initProduct(String productId) {
		setTitle(productId);
		nameText.setText(mDbHelper.getProductName(productId));
		fillData(productId);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			// TODO
			// mDbHelper.deleteNote(info.id);

			// String code = (String)ep.getItem((int)info.id);
			// deleteECode(code);
			// System.out.println("DELETE: " + code);
			ECode code = selectedECodes.get((int) info.id);
			deleteECode(code.eCode);
			// System.out.println ("info:" + code.eCode);

			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void deleteECode(String code) {
		mDbHelper.deleteECodeFromProduct(productId, code);
		fillData(productId);

	}

	private void barcodeIntent() {
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		try {
			// if (intent.) {
			intent.putExtra("SCAN_MODE", "BARCODE_MODE");
			startActivityForResult(intent, 0);
			// }
		} catch (Exception e) {
			// TODO Message box
			barcodeInputDialog();
		}

	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		barcodeIntent();
		setContentView(R.layout.productadditives);

		mDbHelper = new HShopperDbAdapter(this);
		mDbHelper.open();

		searchText = (EditText) findViewById(R.id.addenumber);

		searchText.addTextChangedListener(this);

		nameText = (EditText) findViewById(R.id.productname);
		/*
		 * searchText.setOnEditorActionListener(new OnEditorActionListener() {
		 * 
		 * @Override public boolean onEditorAction(TextView v, int actionId,
		 * KeyEvent event) { // TODO Auto-genegetCount()rated method stub
		 * System.out.println("actionId" + actionId + " event "+event); return
		 * false; } });
		 */

		nameText.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}
		});
		registerForContextMenu(getListView());

		allECodes = new ECodeList();
		allECodes.load(this);

		selectedECodes = new ECodeList();
		selectedECodes.addAll(allECodes);

		button = (Button) findViewById(R.id.saveproduct);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				insertOrUpdateProduct();
				barcodeIntent();

			}
		});
		initProduct(productId);
	}

	public void onStop() {
		super.onStop();
		System.out.println("onStop");
		insertOrUpdateProduct();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		ECode code = selectedECodes.get(position);
		if (searchingCodes) {
			addENumber(code.eCode);
		} else {
			viewECodeDetails(code);
		}

	}

	private void insertOrUpdateProduct() {
		if (nameText.getText().toString().length() > 0) {
			mDbHelper.insertOrUpdateProduct(productId, nameText.getText()
					.toString());
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		System.out.println("requestCode:" + requestCode + "resultCode:"
				+ resultCode);
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				productId = intent.getStringExtra("SCAN_RESULT");
				System.out.println("requestCode:" + requestCode + "resultCode:"
						+ resultCode);
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				initProduct(productId);
				// Handle successful scan
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, NEW_ID, 0, R.string.allenumbers);
		menu.add(0, MANUAL_BARCODE_ID, 0, R.string.manualbarcode);
		menu.add(0, EXPORT_DB_TO_SD_ID, 0, R.string.exportdbsd);
		menu.add(0, EXPORT_DBXML_TO_SD_ID, 0, R.string.exportdbxmlsd);
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case NEW_ID:
			showAllENumbers();
			return true;
		case MANUAL_BARCODE_ID:
			barcodeInputDialog();
			return true;
		case EXPORT_DB_TO_SD_ID:
			if (this.isExternalStorageAvail()) {

				new ExportDatabaseFileTask().execute();

			} else {

				Toast
						.makeText(
								HProductAdditivesActivity.this,
								"External storage is not available, unable to export data.",

								Toast.LENGTH_SHORT).show();

			}

			return true;
		case EXPORT_DBXML_TO_SD_ID:
			if (HProductAdditivesActivity.this.isExternalStorageAvail()) {
				new ExportDataAsXmlTask().execute("exampledb", "exampledata");
			} else {
				Toast
						.makeText(
								HProductAdditivesActivity.this,
								"External storage is not available, unable to export data.",
								Toast.LENGTH_SHORT).show();
			}
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void showAllENumbers() {
		Intent i = new Intent(this, EMainActivity.class);
		i.putExtra("force", "yes");
		startActivityForResult(i, ACTIVITY_ADDNEW);
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

	private void showECodes(String[] codes) {
		adapter = new ECodeAdapter(this, selectedECodes);
		setListAdapter(adapter);
		allECodes.filterExact(codes, selectedECodes);
	}

	private void searchECodes(String[] codes) {
		adapter = new ECodeAdapter(this, selectedECodes);
		setListAdapter(adapter);
		allECodes.filter(codes, selectedECodes);
	}

	private void searchForECodes() {
		String text = searchText.getText().toString().trim();
		String[] codes = text.split(" ");
		searchECodes(codes);
		searchingCodes = true;

		if (selectedECodes.size() == 1) {
			String myCode = searchText.getText().toString().trim();
			addENumber(myCode);
		}

	}

	private void addENumber(String eNumber) {
		mDbHelper.addENumber(productId, eNumber);
		searchText.setText("");
		fillData(productId);
		getListView().setVisibility(View.VISIBLE);
		// System.out.println("VISIBLE");
		// findViewById(R.id.list2).setVisibility(View.INVISIBLE);
	}

	private void fillData(String productId) {
		String[] codes = mDbHelper.fetchAllAdditivesStrings(productId);

		searchingCodes = false;
		showECodes(codes);
		/*
		 * 
		 * String[] from = new String[] { "enumber" }; int[] to = new int[] {
		 * R.id.text1 };
		 */
		// Now create an array adapter and set it to display using our row
		/*
		 * SimpleCursorAdapter ingridients = new SimpleCursorAdapter(this,
		 * R.layout.eitem, mCursor, from, to);
		 */
		/*
		 * ep = new ECodeProductAdapter(this, allECodes, mCursor);
		 * 
		 * 
		 * ListView tl = (ListView)findViewById(R.id.list2);
		 * this.setListAdapter(ep);
		 */

		if (codes.length > 0) {
			findViewById(R.id.empty).setVisibility(View.INVISIBLE);

		}
	}

	private void viewECodeDetails(ECode ecode) {
		Intent intent = new Intent(this, ECodeViewActivity.class);

		intent.putExtra("ecode", ecode);
		startActivity(intent);
	}

	private boolean isExternalStorageAvail() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	private class ExportDatabaseFileTask extends
			AsyncTask<String, Void, Boolean> {
		private final ProgressDialog dialog = new ProgressDialog(
				HProductAdditivesActivity.this);

		// can use UI thread here
		protected void onPreExecute() {
			this.dialog.setMessage("Exporting database...");
			this.dialog.show();
		}

		// automatically done on worker thread (separate from UI thread)
		protected Boolean doInBackground(final String... args) {
			File dbFile =

			new File(Environment.getDataDirectory()
					+ "/data/org.vrodic.hshopper/databases/hshopper");

			File exportDir = new File(
					Environment.getExternalStorageDirectory(),
					HShopperDbAdapter.TAG);

			if (!exportDir.exists()) {

				exportDir.mkdirs();

			}

			File file = new File(exportDir, dbFile.getName());
			try {
				file.createNewFile();
				this.copyFile(dbFile, file);
				return true;
			} catch (IOException e) {
				Log.e(HShopperDbAdapter.TAG, e.getMessage(), e);
				return false;
			}
		}

		// can use UI thread here
		protected void onPostExecute(final Boolean success) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}

			if (success) {
				Toast.makeText(HProductAdditivesActivity.this,
						"Export successful!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(HProductAdditivesActivity.this, "Export failed",
						Toast.LENGTH_SHORT).show();
			}
		}

		void copyFile(File src, File dst) throws IOException {
			FileChannel inChannel = new FileInputStream(src).getChannel();
			FileChannel outChannel = new FileOutputStream(dst).getChannel();
			try {
				inChannel.transferTo(0, inChannel.size(), outChannel);
			} finally {
				if (inChannel != null)
					inChannel.close();
				if (outChannel != null)
					outChannel.close();
			}
		}
	}

	private class ExportDataAsXmlTask extends AsyncTask<String, Void, String> {

		private final ProgressDialog dialog = new ProgressDialog(
				HProductAdditivesActivity.this);

		// can use UI thread here
		protected void onPreExecute() {
			this.dialog.setMessage("Exporting database as XML...");
			this.dialog.show();
		}

		// automatically done on worker thread (separate from UI thread)
		protected String doInBackground(final String... args) {
			DataXmlExporter dm = new DataXmlExporter(
					HProductAdditivesActivity.this.mDbHelper.getDb());
			try {
				String dbName = args[0];
				String exportFileName = args[1];
				dm.export(dbName, exportFileName);
			} catch (IOException e) {
				Log.e(HShopperDbAdapter.TAG, e.getMessage(), e);
				return e.getMessage();
			}
			return null;
		}

		// can use UI thread here
		protected void onPostExecute(final String errMsg) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
			if (errMsg == null) {
				Toast.makeText(HProductAdditivesActivity.this,
						"Export successful!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(HProductAdditivesActivity.this,
						"Export failed - " + errMsg, Toast.LENGTH_SHORT).show();
			}
		}
	}

}
