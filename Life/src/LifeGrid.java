import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class LifeGrid extends JFrame implements ActionListener, ChangeListener {

	private static final long serialVersionUID = -1774123961637777520L;

	private LifeController controller;

	private Pixel[][] pixels;
	private JPanel gridPanel;

	private JButton playBtn;
	private JButton pauseBtn;
	private JButton nextStepBtn;

	private JButton resetBtn;
	private JButton resizeBtn;

	private JSlider timerSlider;

	public LifeGrid(int size, LifeController controller) {

		setSize(510, 665);
		setTitle("Robbie's Game of Life");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setBackground(Color.white);

		this.controller = controller;
		
		initPixels(size);
		
		playBtn = new JButton("Play");
		playBtn.setBounds(5, 515, 60, 60);
		playBtn.addActionListener(this);
		add(playBtn);

		pauseBtn = new JButton("Pause");
		pauseBtn.setBounds(70, 515, 60, 60);
		pauseBtn.setEnabled(false);
		pauseBtn.addActionListener(this);
		add(pauseBtn);

		nextStepBtn = new JButton("Next Step");
		nextStepBtn.setBounds(135, 515, 120, 60);
		nextStepBtn.addActionListener(this);
		add(nextStepBtn);

		resetBtn = new JButton("Reset");
		resetBtn.setBounds(5, 580, 60, 60);
		resetBtn.addActionListener(this);
		add(resetBtn);

		resizeBtn = new JButton("Resize");
		resizeBtn.setBounds(70, 580, 60, 60);
		resizeBtn.addActionListener(this);
		add(resizeBtn);

		timerSlider = new JSlider(JSlider.HORIZONTAL, LifeController.DELAY_MIN, LifeController.DELAY_MAX, LifeController.DELAY_INIT);
		timerSlider.addChangeListener(this);
		timerSlider.setBounds(255, 515, 250, 60);

		Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
		labels.put(LifeController.DELAY_MIN, new JLabel("Fast"));
		labels.put(LifeController.DELAY_MAX, new JLabel("Slow"));
		labels.put((LifeController.DELAY_MAX + LifeController.DELAY_MIN)/2, new JLabel("Step Speed"));
		timerSlider.setLabelTable(labels);
		timerSlider.setPaintLabels(true);

		add(timerSlider);

		this.setVisible(true);
	}

	public void stateChanged(ChangeEvent e) {
		controller.setDelay(timerSlider.getValue());
	}

	public void actionPerformed(ActionEvent e) {

		if(e.getActionCommand().equals(playBtn.getActionCommand())) {
			playBtn.setEnabled(false);
			nextStepBtn.setEnabled(false);
			resetBtn.setEnabled(false);
			resizeBtn.setEnabled(false);
			pauseBtn.setEnabled(true);
			controller.play();
		}
		else if(e.getActionCommand().equals(pauseBtn.getActionCommand())) {
			playBtn.setEnabled(true);
			nextStepBtn.setEnabled(true);
			pauseBtn.setEnabled(false);
			resetBtn.setEnabled(true);
			resizeBtn.setEnabled(true);
			controller.pause();
		}
		else if(e.getActionCommand().equals(nextStepBtn.getActionCommand())) {
			controller.nextStep();
		}
		else if(e.getActionCommand().equals(resetBtn.getActionCommand())) {

			controller.reset();
			resetPixels();
		}
		else if(e.getActionCommand().equals(resizeBtn.getActionCommand())) {
			int newSize = Integer.parseInt(JOptionPane.showInputDialog("Enter new dimension"));
			remove(gridPanel);
			initPixels(newSize);
			controller.setSize(newSize);
			controller.reset();
			validate();
			repaint();
		}
	}

	private void initPixels(int size) {

		int dim = 500 / (size) * size;

		gridPanel = new JPanel();
		
		gridPanel.setBounds((510 - dim - 1) / 2, 5, dim + 1, dim + 1);
		gridPanel.setLayout(new GridLayout(size, size, 1, 1));
		gridPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		gridPanel.setBackground(Color.black);

		pixels = new Pixel[size][size];

		for(int r = 0; r < size; r++) {
			for(int c = 0; c < size; c++) {

				pixels[r][c] = new Pixel(new Point(r, c));
				pixels[r][c].setBackground(Color.white);
				pixels[r][c].addMouseListener(pixels[r][c]);
				gridPanel.add(pixels[r][c]);
			}
		}

		add(gridPanel);
	}
	
	private void resetPixels() {
		for(int r = 0; r < pixels.length; r++) {
			for(int c = 0; c < pixels.length; c++) {
				pixels[r][c].setBackground(Color.white);
			}
		}
	}

	public void changePixel(Point point) {
		pixels[point.x][point.y].changeColor();
	}

	private class Pixel extends JPanel implements MouseListener {

		Point loc;

		private static final long serialVersionUID = 5928307691075252684L;

		public Pixel(Point p) {
			loc = p;
		}

		public void mouseClicked(MouseEvent arg0) {
			if(playBtn.isEnabled()) {
				changeColor();
				controller.notifyLife(loc);
			}
		}

		public void changeColor() {
			if(getBackground() == Color.white) {
				setBackground(Color.black);
			}
			else {
				setBackground(Color.white);
			}
		}

		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		public void mousePressed(MouseEvent arg0) {}
		public void mouseReleased(MouseEvent arg0) {}

	}
}
