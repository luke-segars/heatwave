package com.lukesegars.heatwave;

import android.content.ContentValues;
import android.content.Context;

public class Wave {
	// The number of seconds per unit entered in the UI.  The UI currently
	// asks users for wavelengths in days, which translates into 86400 seconds.
	public static final int SECONDS_PER_UNIT = 86400;
	
	public class Fields {
		private static final int DEFAULT_ID = -1;
		private static final int DEFAULT_WAVELENGTH = -1;
		
		private int id = DEFAULT_ID;
		private String name = null;
		private int waveLength = DEFAULT_WAVELENGTH;
		
		public int getId() { return id; }
		public void setId(int i) { id = i; }
		
		public String getName() { return name; }
		public void setName(String n) { name = n; }
		
		public int getWavelength() { return waveLength; }
		public void setWavelength(int wl) { waveLength = wl; }
		
		protected void modify(Wave.Fields f) {
			if (f.getId() != DEFAULT_ID) setId(f.getId());
			if (f.getName() != null) setName(f.getName());
			if (f.getWavelength() != DEFAULT_WAVELENGTH) setWavelength(f.getWavelength());
		}
	}

	private static HeatwaveDatabase database;
	private static Context context = null;
	
	private Wave.Fields fields = new Wave.Fields();
	
	private static void initDb() {
		if (database == null) {
			database = HeatwaveDatabase.getInstance(context);
		}
	}

	public static void setContext(Context c) {
		context = c;
	}
	
	//////////////////////////////
	/// Static factory methods ///
	//////////////////////////////
	public static Wave create(String name, int wl) {
		initDb();

		// If the wave already exists, return an instance of that
		// object and do not create a new row in the database.
		Wave exists = database.loadWaveByName(name);
		if (exists != null) return exists;
		
		Wave w = new Wave();
		Wave.Fields wf = w.new Fields();
		
		wf.setName(name);
		wf.setWavelength(wl);

		w.modify(wf, false);
		database.addWave(w);

		return w;
	}
	
	public static Wave load(int id) {
		initDb();
		
		return database.fetchWave(id);
	}
	
	public static Wave skeleton() {
		return new Wave();
	}
	
	////////////////////////////
	/// Private constructors ///
	////////////////////////////
	
	private Wave() {}
	
	private Wave(Wave.Fields f) {
		fields = f;
	}
	
	//////////////////////
	/// Public methods ///
	//////////////////////
	public void modify(Wave.Fields f, boolean updateDb) {
		initDb();
		fields.modify(f);
		
		// Update the database records if requested (default = true).
		if (updateDb) database.updateWave(this);
	}
	
	public void modify(Wave.Fields f) {
		modify(f, true);
	}
	
	public int getId() {
		return fields.getId();
	}
	
	public String getName() {
		return fields.getName();
	}
	
	public int getWaveLength() {
		return fields.getWavelength();
	}
	
	public ContentValues cv() {
		ContentValues cv = new ContentValues();
		
		cv.put("name", fields.getName());
		cv.put("wavelength", fields.getWavelength());
		
		return cv;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + 
			((fields.getName() == null) ? 0 : fields.getName().hashCode());
		result = prime * result + fields.getWavelength();
		
		return result;
	}

	/**
	 * Automatically generated by Eclipse (woohoo!)
	 * 
	 * Checks to ensure that objects are valid and pointers to fields
	 * are valid before comparing values.  If the two waves have the
	 * same WAVE_LENGTH and NAME then they will be declared as "equal."
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		Wave other = (Wave) obj;
		
		if (fields.getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		if (fields.getWavelength() != other.fields.getWavelength())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Wave '" + fields.getName() + 
			"', wavelength: " + String.valueOf(fields.getWavelength());
	}
}
