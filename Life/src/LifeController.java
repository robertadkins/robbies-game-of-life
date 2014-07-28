import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class LifeController implements ActionListener {

	static final int DELAY_MIN = 50;
	static final int DELAY_MAX = 1000;
	static final int DELAY_INIT = 200;
	
	private Life life;
	private LifeGrid grid;
	private Timer timer;
	private int size;
	
	public LifeController() {
		size = 50;
		life = new Life(size);
		grid = new LifeGrid(life.getSize(), this);
		timer = new Timer(100, this);
	}
	
	public void play() {
		timer.start();
	}
	
	public void pause() {
		timer.stop();
	}
	
	public void notifyLife(Point p) {
		life.changePixel(p);
	}

	public void nextStep() {
		for(Point p : life.nextRound()) {
			grid.changePixel(p);
		}
	}
	
	public void setDelay(int delay) {
		timer.setDelay(delay);
	}
	
	public void reset() {
		life = new Life(size);
	}
	
	public void setSize(int s) {
		size = s;
	}
	
	public void actionPerformed(ActionEvent e) {
		nextStep();
	}
	
	public static void main(String[] args) {
		
		LifeController game = new LifeController();
	}
}
