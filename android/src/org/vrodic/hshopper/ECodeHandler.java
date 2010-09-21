/*
 * This file is distributed under GPL v3 license 
 * http://www.gnu.org/licenses/gpl-3.0.html      
 *                                               
 * raven@acute-angle.net                         
 */
 
package org.vrodic.hshopper;

import java.util.HashMap;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ECodeHandler extends DefaultHandler {
	private final static String TAG_PURPOSE = "purpose";
	private final static String TAG_ENUMBER = "enumber";
	private final static String TAG_DANGER = "danger";
	private final static String TAG_VEGAN = "veg";
	private final static String TAG_CHILD = "child";
	private final static String TAG_NAME = "name";
	private final static String TAG_EXTRA = "extra";
	private final static String TAG_ALLERGIC = "allergic";

	private final static int STATE_PURPOSE = 0;
	private final static int STATE_ENUMBER = 1;
	private final static int STATE_NAME = 2;
	private final static int STATE_EXTRA = 3;

	private int currentState = -1;

	private int currentPurposeId = -1;

	private String language;

	private ECodeList list;
	private ECode currentECode;
	private HashMap<Integer, String> purposes;
	private StringBuilder currentString = new StringBuilder();

	public ECodeHandler(ECodeList list) {
		this.list = list;
		this.currentECode = null;
		language = Locale.getDefault().getLanguage();
		purposes = new HashMap<Integer, String>();
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		if (TAG_ENUMBER.equals(localName)) {
			list.add(currentECode);
			currentState = -1;
		}
		switch (currentState) {
		case STATE_PURPOSE:
			if (localName.equals(language))
				purposes.put(currentPurposeId, currentString.toString());
			break;
		case STATE_NAME:
			if (localName.equals(language))
				currentECode.name = currentString.toString();
			break;
		case STATE_EXTRA:
			if (localName.equals(language))
				currentECode.comment = currentString.toString();
			break;
		}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		list.clear();
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		String value = new String(ch, start, length).trim();
		if ("\n".equals(value) || "".equals(value))
			return;
		currentString.append(value);
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		currentString = new StringBuilder();
		if (TAG_PURPOSE.equals(localName)) {
			if (currentState == STATE_ENUMBER) {
				Integer purposeId = Integer.parseInt(attributes
						.getValue("code"));
				if (purposes.containsKey(purposeId))
					currentECode.purpose = purposes.get(purposeId);
				else
					currentECode.purpose = null;
			} else {
				currentState = STATE_PURPOSE;
				currentPurposeId = Integer.parseInt(attributes.getValue("id"));
			}
		} else if (TAG_ENUMBER.equals(localName)) {
			currentState = STATE_ENUMBER;
			currentECode = new ECode();
			currentECode.eCode = attributes.getValue("id");
			currentECode.allergic = false;
		} else if (TAG_DANGER.equals(localName)) {
			currentECode.setDanger(Integer.parseInt(attributes
					.getValue("value")));
		} else if (TAG_VEGAN.equals(localName)) {
			currentECode.vegan = Integer.parseInt(attributes.getValue("value"));
		} else if (TAG_CHILD.equals(localName)) {
			currentECode.children = Integer.parseInt(attributes
					.getValue("value"));
		} else if (TAG_ALLERGIC.equals(localName)) {
			currentECode.allergic = true;
		} else if (TAG_NAME.equals(localName)) {
			currentState = STATE_NAME;
		} else if (TAG_EXTRA.equals(localName)) {
			currentState = STATE_EXTRA;
		}
	}

}
