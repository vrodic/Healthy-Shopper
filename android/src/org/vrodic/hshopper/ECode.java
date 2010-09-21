/*
 * This file is distributed under GPL v3 license 
 * http://www.gnu.org/licenses/gpl-3.0.html      
 *                                               
 * raven@acute-angle.net                         
 */

package org.vrodic.hshopper;

import android.os.Parcel;
import android.os.Parcelable;

public final class ECode implements Comparable<ECode>, Parcelable {
	public static final int UNKNOWN = 4;
	public static final int PERMITTED = 0;
	public static final int UNPERMITTED = 1; // SLIGHT DANGER
	public static final int MOSTLY_SAFE = 2; // Usually safe !!! Value changed!
	public static final int DANGEROUS = 3; // DANGEROUS, AVOID AT ALL COST

	public static final int[] colors = { 0xFF75D175, 0xFFFFFF3D, /*0xFFFF6600,*/
			0xFFBFD175,
			0xFFFF001A, 0xFFCCCCCC };

	public String eCode;
	public String name;
	public String purpose;
	private int danger;
	public String comment;
	public int vegan;
	public int children;
	public boolean allergic;

	public static final Parcelable.Creator<ECode> CREATOR = new Parcelable.Creator<ECode>() {
		public ECode createFromParcel(Parcel in) {
			return new ECode(in);
		}

		public ECode[] newArray(int size) {
			return new ECode[size];
		}
	};
	
	public ECode() {
	}

	private ECode(Parcel in) {
		eCode = in.readString();
		name = in.readString();
		purpose = in.readString();
		danger = in.readInt();
		comment = in.readString();
		vegan = in.readInt();
		children = in.readInt();
		allergic = in.readInt() != 0;
	}

	public boolean safeForVegans() {
		return vegan == 0;
	}

	public boolean safeForChildren() {
		return children == 0;
	}

	public boolean safeForAllergic() {
		return !allergic;
	}

	public boolean hasExtra() {
		return comment != null && !"".equals(comment);
	}

	public String getExtra() {
		return comment;
	}

	public void init(String[] row) {
		eCode = row[0];
		name = row[1];
		if (row.length > 2)
			purpose = row[2];
		else
			purpose = "";
		if (row.length > 3)
			danger = Integer.parseInt(row[3]);
		else
			danger = UNKNOWN;
		if (row.length > 4)
			comment = row[4];
	}

	@Override
	public int compareTo(ECode another) {
		String st1 = eCode;
		String st2 = another.eCode;
		if (st1.charAt(st1.length() - 1) > '9')
			st1 = st1.substring(0, st1.length() - 1);
		if (st2.charAt(st2.length() - 1) > '9')
			st2 = st2.substring(0, st2.length() - 1);
		if (st1.equals(st2))
			return eCode.compareTo(another.eCode);
		else
			return Integer.valueOf(st1).compareTo(Integer.valueOf(st2));
	}

	public int getColor() {
		return colors[danger];
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(eCode);
		dest.writeString(name);
		dest.writeString(purpose);
		dest.writeInt(danger);
		dest.writeString(comment);
		dest.writeInt(vegan);
		dest.writeInt(children);
		dest.writeInt(allergic ? 1 : 0);
	}

	public int getDanger() {
		return danger;
	}

	public void setDanger(int danger) {
		if (danger < 0 || danger > 3)
			danger = 4;
		this.danger = danger;
	}

	
}
