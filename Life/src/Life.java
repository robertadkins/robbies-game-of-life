import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;


public class Life {

	private int size;

	static final int[] xOffset = {-1, 0, 1, -1, 1, -1, 0, 1};
	static final int[] yOffset = {-1, -1, -1, 0, 0, 1, 1, 1};

	private HashSet<Point> litPoints;
	private HashSet<Point> nextPoints;
	private HashSet<Point> checkedDeadPoints;
	private HashSet<Point> changedPoints;

	public Life() {
		this(10);
	}

	public Life(int size) {
		this.size = size;
		litPoints = new HashSet<Point>();
		nextPoints = new HashSet<Point>();
		checkedDeadPoints = new HashSet<Point>();
		changedPoints = new HashSet<Point>();
	}

	public void changePixel(Point p) {
		if(litPoints.contains(p)) {
			litPoints.remove(p);
		}
		else {
			litPoints.add(p);
		}
	}
	
	public int getSize() {
		return size;
	}
	
	public HashSet<Point> getLitPixels() {
		return litPoints;
	}
	
	public HashSet<Point> nextRound() {

		ArrayList<Thread> threads = new ArrayList<Thread>();

		for(Point p : litPoints) {
			Thread t = new Thread(new PointAnalyzer(p));
			t.start();
			threads.add(t);
		}
		for(Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		litPoints = nextPoints;

		HashSet<Point> toReturn = changedPoints;

		nextPoints = new HashSet<Point>();
		checkedDeadPoints = new HashSet<Point>();
		changedPoints = new HashSet<Point>();

		return toReturn;
	}

	private class PointAnalyzer implements Runnable {

		Point point;

		public PointAnalyzer(Point point) {
			this.point = point;
		}

		public void run() {

			if(litPoints.contains(point)) {
				checkLitPoint();
			}
			else {
				checkDeadPoint();
			}
		}

		private void checkLitPoint() {

			int litNeighbors = 0;
			ArrayList<Thread> deadNeighbors = new ArrayList<Thread>();

			// check neighbors
			for(int i = 0; i < xOffset.length; i++) {

				int x = point.x + xOffset[i];
				int y = point.y + yOffset[i];

				if(x >= 0 && x < size && y >= 0 && y < size) {

					Point neighbor = new Point(x, y);

					if(litPoints.contains(neighbor)) {
						litNeighbors++;
					}
					else {
						synchronized(checkedDeadPoints) {
							if(!checkedDeadPoints.contains(neighbor)) {
								Thread deadNeighbor = new Thread(new PointAnalyzer(neighbor));
								deadNeighbors.add(deadNeighbor);
								deadNeighbor.start();
							}
						}
					}
				}
			}

			if(litNeighbors < 2 || litNeighbors > 3) {
				synchronized(changedPoints) {
					changedPoints.add(point);
				}
			}
			else {
				synchronized(nextPoints) {
					nextPoints.add(point);
				}
			}

			// wait for dead neighbors
			for(Thread t: deadNeighbors) {
				try {
					t.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}


		}
		private void checkDeadPoint() {

			int litNeighbors = 0;

			// check neighbors
			for(int i = 0; i < xOffset.length; i++) {

				int x = point.x + xOffset[i];
				int y = point.y + yOffset[i];

				if(x >= 0 && x < size && y >= 0 && y < size) {

					Point neighbor = new Point(x, y);

					if(litPoints.contains(neighbor)) {
						litNeighbors++;
					}
				}
			}

			if(litNeighbors == 3) {
				synchronized(changedPoints) {
					changedPoints.add(point);
				}
				synchronized(nextPoints) {
					nextPoints.add(point);
				}
			}
		}
	}
}
