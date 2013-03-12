package dk.aau.cs.giraf.pictogram;

public enum PictoFactory {
	INSTANCE;
	
	public Pictogram get(int picId, int soundId, int textId) {
		//DB connection and stuff
		
		
		
		Pictogram picto = new Pictogram(null, null);
		
		return picto;
	}

}
