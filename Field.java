import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class Field extends Component implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3472264920354096063L;
	// variables for double buffered display
	private BufferedImage bi;
	private Graphics gi;

	// dimensions of the frame
	private Dimension dim;

	// constants
	private final static Color background = Color.BLACK;
	private final static int DELAY = 75;
	private final static int XSTART = 0;
	private final static int XEND = 800;
	private final static int YSTART = 0;
	private final static int YEND = 560;
	private final int SIZE = 780*2 + 580*2;
	
	// All possible starting locations
	private State[] poss = new State[SIZE];
	
	// ArrayList that contains all the active dots
	private ArrayList<Dot> dots = new ArrayList<Dot>();

	// Thread that updates the position of the dots according to the DELAY
	private Timer t = new Timer();
	private TimerTask move = new TimerTask() {
		@Override
		public void run () {
			for (int i = dots.size() - 1; i >= 0; i--) {
				dots.get(i).move();
				Dot curr = dots.get(i);
				// removing the dots if it is outside the boundary
				if (curr.x > dim.getWidth())
					dots.remove(i);
				else if (curr.x + 20 < 0)
					dots.remove(i);
				else if (curr.y > dim.getHeight())
					dots.remove(i);
				else if (curr.y + 20 < 0)
					dots.remove(i);
			}
			repaint();
		}
	};
	Field () {
		// initializing all the starting positions
		int cnt = 0;
		for (int i = 0; i < 780; i++) {
			poss[cnt++] = new State(i, YSTART, 0);
			poss[cnt++] = new State(i, YEND, 1);
		}
		for (int i = 0; i < 580; i++) {
			poss[cnt++] = new State(XSTART, i, 2);
			poss[cnt++] = new State(XEND, i, 3);
		}
		addMouseListener(this);
		t.scheduleAtFixedRate(move, 0, DELAY);
	}
	// double buffered painting and updating
	public void paint (Graphics g) {
		dim = getSize();
		bi = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
		gi = bi.getGraphics();
		update(g);
	}
	public void update (Graphics g) {
		gi.setColor(background);
		gi.fillRect(0, 0, dim.width, dim.height);
		for (Dot d : dots) {
			// Paint a dot a specific color according to the direction
			if (d.xspeed > 0)
				gi.setColor(Color.RED);
			else if (d.xspeed < 0)
				gi.setColor(Color.BLUE);
			else if (d.yspeed > 0)
				gi.setColor(Color.YELLOW);
			else if (d.yspeed < 0)
				gi.setColor(Color.GREEN);
			gi.fillRect(d.x, d.y, 20, 20);
		}
		g.drawImage(bi, 0, 0, this);
	}
	@Override
	public void mouseClicked (MouseEvent e) {
		// Generate a dot
		permute(0);
		outer : for (State s : poss) {
			for (Dot d : dots) {
				// checking for opposite direction collision
				if (s.dir == 0 && d.yspeed < 0 && isOverlapX(s, d)) {
					continue outer;
				} else if (s.dir == 1 && d.yspeed > 0 && isOverlapX(s, d)) {
					continue outer;
				} else if (s.dir == 2 && d.xspeed < 0 && isOverlapY(s, d)) {
					continue outer;
				} else if (s.dir == 3 && d.xspeed > 0 && isOverlapY(s, d)) {
					continue outer;
				} 
				// checking for same direction collision
				else if (s.dir == 0 && d.yspeed > 0 && isOverlapX(s, d) && (YEND - d.y)*s.speed >= (YEND - s.y)*d.yspeed){
					continue outer;
				} else if (s.dir == 1 && d.yspeed < 0 && isOverlapX(s, d) && (d.y)*s.speed >= (s.y)*-d.yspeed) {
					continue outer;
				} else if (s.dir == 2 && d.xspeed > 0 && isOverlapY(s, d) && (XEND - d.x)*s.speed >= (XEND - s.x)*d.xspeed) {
					continue outer;
				} else if (s.dir == 3 && d.xspeed < 0 && isOverlapY(s, d) && (d.x)*s.speed >= (s.x)*-d.xspeed) {
					continue outer;
				}
				// checking for perpendicular collisions
				else if (s.dir == 0) {
					double time1 = (d.y - 20 - s.y)/(double)(s.speed);
					double time2 = (d.y + 20 - s.y)/(double)(s.speed);
					double time3 = (s.x - 20 - d.x)/(double)(d.xspeed);
					double time4 = (s.x + 20 - d.x)/(double)(d.xspeed);
					if (time3 > time4) {
						double temp = time3;
						time3 = time4;
						time4 = temp;
					}
					if (isOverlap(time1, time2, time3, time4))
						continue outer;
				} else if (s.dir == 1) {
					double time1 = (s.y - d.y - 20)/(double)(s.speed);
					double time2 = (s.y - d.y + 20)/(double)(s.speed);
					double time3 = (s.x - 20 - d.x)/(double)(d.xspeed);
					double time4 = (s.x + 20 - d.x)/(double)(d.xspeed);
					if (time3 > time4) {
						double temp = time3;
						time3 = time4;
						time4 = temp;
					}
					if (isOverlap(time1, time2, time3, time4))
						continue outer;
				} else if (s.dir == 2) {
					double time1 = (d.x - 20 - s.x)/(double)(s.speed);
					double time2 = (d.x + 20 - s.x)/(double)(s.speed);
					double time3 = (s.y - 20 - d.y)/(double)(d.yspeed);
					double time4 = (s.y + 20 - d.y)/(double)(d.yspeed);
					if (time3 > time4) {
						double temp = time3;
						time3 = time4;
						time4 = temp;
					}
					if (isOverlap(time1, time2, time3, time4))
						continue outer;
				} else if (s.dir == 3) {
					double time1 = (s.x - d.x - 20)/(double)(s.speed);
					double time2 = (s.x - d.x + 20)/(double)(s.speed);
					double time3 = (s.y - 20 - d.y)/(double)(d.yspeed);
					double time4 = (s.y + 20 - d.y)/(double)(d.yspeed);
					if (time3 > time4) {
						double temp = time3;
						time3 = time4;
						time4 = temp;
					}
					if (isOverlap(time1, time2, time3, time4))
						continue outer;
				}
			}
			// if the dot is valid, then add the dot into the list of active dots
			if (s.dir == 0) {
				dots.add(new Dot(s.x, s.y, 0, s.speed));
			} else if (s.dir == 1) {
				dots.add(new Dot(s.x, s.y, 0, -s.speed));
			} else if (s.dir == 2) {
				dots.add(new Dot(s.x, s.y, s.speed, 0));
			} else if (s.dir == 3) {
				dots.add(new Dot(s.x, s.y, -s.speed, 0));
			}
			break;
		}
	}
	// auxiliary methods that check for overlaps
	private boolean isOverlapX (State s, Dot d) {
		return isOverlap(s.x, s.x+20, d.x, d.x + 20);
	}
	private boolean isOverlapY (State s, Dot d) {
		return isOverlap(s.y, s.y+20, d.y, d.y + 20);
	}
	private boolean isOverlap (double a, double b, double c, double d) {
		return b - c > 0 && d - a > 0;
	}
	// auxiliary method that randomly permutes states and speeds
	private void permute (int i) {
		if (i == SIZE - 1) {
			poss[i].speed = (int)(Math.random()*20)+10;
			return;
		}
		int j = (int)(Math.random()*(SIZE-i-1) + i);
		State temp = poss[i];
		poss[i] = poss[j];
		poss[j] = temp;
		permute(i+1);
		poss[i].speed = (int)(Math.random()*20)+10;
	}
	// static class that represents the starting states
	static class State {
		int x, y, dir;
		int speed;
		State (int x, int y, int dir) {
			this.x = x;
			this.y = y;
			this.dir = dir;
		}
	}
	@Override
	public void mousePressed (MouseEvent e) {
	}
	@Override
	public void mouseReleased (MouseEvent e) {
	}
	@Override
	public void mouseEntered (MouseEvent e) {
	}
	@Override
	public void mouseExited (MouseEvent e) {
	}
}
