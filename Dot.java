// Dot object that represents each square image in the grid
public class Dot {
	int x, y, xspeed, yspeed;
	Dot (int x, int y, int xspeed, int yspeed) {
		this.x = x;
		this.y = y;
		this.xspeed = xspeed;
		this.yspeed = yspeed;
	}
	public void move () {
		x += xspeed;
		y += yspeed;
	}
}
