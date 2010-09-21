/*
 * This file is distributed under GPL v3 license 
 * http://www.gnu.org/licenses/gpl-3.0.html      
 *                                               
 * raven@acute-angle.net                         
 */


package org.vrodic.hshopper;

import org.vrodic.hshopper.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class EHelpActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.help);
	}

}
