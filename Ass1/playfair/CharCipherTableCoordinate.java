package playfair;

public class CharCipherTableCoordinate {
	
	private int coordinateX;
	private int coordinateY;
	
	public CharCipherTableCoordinate() {
		coordinateX = 0;
		coordinateY = 0;
	}
	
	public CharCipherTableCoordinate(int x, int y) {
		coordinateX = x;
		coordinateY = y;
	}
	
	public int getCoordinateX() {
		return coordinateX;
	}
	
	public int getCoordinateY() {
		return coordinateY;
	}
	
	public void setCoordinateX(int x) {
		coordinateX = x;
	}

	public void setCoordinateY(int y) {
		coordinateY = y;
	}
	
}
