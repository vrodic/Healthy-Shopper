package org.vrodic.hshopper;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;


public class HProductMainActivity  extends ListActivity implements TextWatcher  {


	private EditText searchText;
	private Button button;
	private EditText nameText;
	private ECodeProductAdapter ep;
	
	private HShopperDbAdapter mDbHelper;
	private Cursor mCursor;
	private String productId = "00000000";
	
	private ECodeList allECodes;
	private ECodeList selectedECodes;
	private ECodeAdapter adapter;
	
	private static final int ACTIVITY_ADDNEW=1;
	public static final int NEW_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	
	
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
        switch(item.getItemId()) {
        case DELETE_ID:
            AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
            // TODO 
            //mDbHelper.deleteNote(info.id);
            
            String code = (String)ep.getItem((int)info.id);
            deleteECode(code);
            //System.out.println("DELETE: " + code);
            
            //fillData();
            return true;
        }
        return super.onContextItemSelected(item);
    }
    
    private void deleteECode(String code) {
    	mDbHelper.deleteECodeFromProduct(productId, code);
    	fillData(productId);
    	
    }
    
	public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			 Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			 try {
			// if (intent.) {
		        intent.putExtra("SCAN_MODE", "BARCODE_MODE");
		        startActivityForResult(intent, 0);
			 //}
			 } catch (Exception e) {
				 // TODO Message box
			 }
			 
			setContentView(R.layout.productmain);
			
			mDbHelper = new HShopperDbAdapter(this);
			mDbHelper.open();
			
			searchText = (EditText) findViewById(R.id.addenumber);

			searchText.addTextChangedListener(this);
			
			nameText = (EditText) findViewById(R.id.productname);
			/*searchText.setOnEditorActionListener(new OnEditorActionListener() {
				
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					// TODO Auto-generated method stub
					System.out.println("actionId" + actionId + " event "+event);
					return false;
				}
			});*/

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
					
				}
			});
			initProduct(productId);
	}
	
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		ECodeProductAdapter a = (ECodeProductAdapter)l.getAdapter();
		ECode ecode = allECodes.getEcode(a.getItem(position).toString());
		
		viewECodeDetails(ecode);
		
		
	}

	private void insertOrUpdateProduct() {
		mDbHelper.insertOrUpdateProduct(productId, nameText.getText().toString());
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    System.out.println("requestCode:" + requestCode + "resultCode:" + resultCode );
		if (requestCode == 0) {
	        if (resultCode == RESULT_OK) {
	            productId = intent.getStringExtra("SCAN_RESULT");
	            System.out.println("requestCode:" + requestCode + "resultCode:" + resultCode );
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
        return result;
    }    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case NEW_ID:
            showAllENumbers();
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
		//System.out.println("changed text " + searchText.getText().toString().trim());
		getListView().setVisibility(View.INVISIBLE);
		
		//findViewById(R.id.list2).setVisibility(View.VISIBLE);		
	}
	
	private void searchForECodes() {
		String text = searchText.getText().toString().trim();
		String[] codes = text.split(" ");

	    ListView tl = (ListView)findViewById(R.id.list2);
		adapter = new ECodeAdapter(this, selectedECodes);
		  if (tl != null) {
	        	tl.setAdapter(adapter);
	        	tl.setOnItemClickListener(new OnItemClickListener() {
	        		@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long id) {

						ECode code = selectedECodes.get(position);
						addENumber(code.eCode);
						
						// TODO Auto-generated method stub
						
					}
				});
	        }
		
		  
				

		

		allECodes.filter(codes, selectedECodes);
		if (selectedECodes.size() == 1) {
			String myCode = searchText.getText().toString().trim();
			addENumber(myCode);
		}
		
	}
	
	private void addENumber (String eNumber) {
		mDbHelper.addENumber(productId, eNumber);
		searchText.setText("");
		fillData(productId);
		getListView().setVisibility(View.VISIBLE);
		//System.out.println("VISIBLE");
		//findViewById(R.id.list2).setVisibility(View.INVISIBLE);
	}

	
	  private void fillData(String productId) {
	     
	    	
	    	mCursor = mDbHelper.fetchAllAdditives(productId);
	    	
	    	
	        startManagingCursor(mCursor);

	        String[] from = new String[] { "enumber" };
	        int[] to = new int[] { R.id.text1 };
	        
	        // Now create an array adapter and set it to display using our row
	        /*SimpleCursorAdapter ingridients =
	            new SimpleCursorAdapter(this, R.layout.eitem, mCursor, from, to);*/
			ep = new ECodeProductAdapter(this, allECodes, mCursor);
		
	        
	        ListView tl = (ListView)findViewById(R.id.list2);
	        this.setListAdapter(ep);
	        	        
				   
	        
	        if (mCursor.getCount() > 0 ) {
	        	findViewById(R.id.empty).setVisibility(View.INVISIBLE);
	        	
	        }
	    }
	  
	  private void 	viewECodeDetails(ECode ecode) {
		  Intent intent = new Intent(this, ECodeViewActivity.class);
	  
		intent.putExtra("ecode", ecode);
		startActivity(intent);
	  }
	  
	

}
