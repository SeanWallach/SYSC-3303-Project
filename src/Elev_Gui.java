
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.*;
import javax.swing.*;

import javax.swing.JButton;
import javax.swing.JFrame;

import javafx.event.ActionEvent;

public class Elev_Gui {

	private int elevNumber;
	private Elev elevator;
	private int topFloor;
	private JButton[] floor;
	private JButton[] fButton;
	private JButton[] door;
	private JTextField tf;

	// temporary test variables
	private int count;

	public Elev_Gui(int elevNum, int floors, Elev elev) {
		elevNumber = elevNum;
		elevator = elev;
		this.topFloor = floors;
		floor = new JButton[floors];
		fButton = new JButton[floors];

		this.createGui(floors, elevNum);

	}

	public static void main(String[] args) {
		Elevator elevator = new Elevator();
		Elev elev1 = new Elev(1, 22, 69, elevator);
		Elev_Gui G = new Elev_Gui(1, 22, elev1);
	}

	public void move(int floor, int direction) {
		// change the colour of specific button to display elevator movement
		if (floor == 1) {
			this.floor[floor].setBackground(Color.WHITE);
			this.floor[floor - 1].setBackground(Color.GREEN);
			if (direction == 1) {
				tf.setText(" Floor: " + floor + "  UP ");
			} else {
				tf.setText(" Floor: " + floor + "  DOWN ");
			}
		} else if (floor == this.topFloor) {
			this.floor[floor - 2].setBackground(Color.WHITE);
			this.floor[floor - 1].setBackground(Color.GREEN);
			if (direction == 1) {
				tf.setText(" Floor: " + floor + "  UP ");
			} else {
				tf.setText(" Floor: " + floor + "  DOWN ");
			}
		} else {
			this.floor[floor].setBackground(Color.WHITE);
			this.floor[floor - 2].setBackground(Color.WHITE);
			this.floor[floor - 1].setBackground(Color.GREEN);
			if (direction == 1) {
				tf.setText(" Floor: " + floor + "  UP ");
			} else {
				tf.setText(" Floor: " + floor + "  DOWN ");
			}
		}
		if (Color.RED.equals(this.fButton[floor - 1].getBackground())) {
			this.fButton[floor - 1].setBackground(Color.WHITE);
			this.fButton[floor - 1].setEnabled(true);

		}

	}
	
	public void resetFButton(int floor) {
		this.fButton[floor - 1].setBackground(Color.WHITE);
		this.fButton[floor - 1].setEnabled(true);		
	}

	private void createGui(int floors, int elevNum) {
		JFrame f = new JFrame("Elevator" + elevNum);
		tf = new JTextField(" Floor: " + elevator.getCurrentFLoor() + " Stop ");
		Font font = new Font("SansSerif", Font.BOLD, 20);
		tf.setFont(font);
		tf.setHorizontalAlignment(JTextField.CENTER);
		tf.setBounds(150, 350, 200, 65);

		// set up floor markers
		String F;
		int x = 10;
		int y = 10;
		for (int i = floors - 1; i >= 0; i--) {
			F = " " + (i + 1) + " ";
			floor[i] = new JButton(F);
			floor[i].setBounds(x, y, 75, 35);
			floor[i].setBackground(Color.WHITE);
			y += 35;
			f.add(floor[i]);

		}

		// set up floor buttons

		ActionListener listener = new ActionListener() { // the floor button action when pressed
			@Override
			public void actionPerformed(java.awt.event.ActionEvent arg0) {
				if (arg0.getSource() instanceof JButton) {
					String text = ((JButton) arg0.getSource()).getText();

					((JButton) arg0.getSource()).setBackground(Color.RED);
					String request = ((JButton) arg0.getSource()).getText();
					int r = Integer.parseInt(request);
					((JButton) arg0.getSource()).setEnabled(false);
					elevator.requestWaiting = true;
					elevator.addRequest(r, elevator.getCurrentFLoor());
				}
			}
		};

		x = 90;
		y = 480; // create and place floor buttons is gui
		int count = 0;
		for (int i = 0; i < floors; i++) {
			F = "" + (i + 1) + "";
			fButton[i] = new JButton(F);

			if (count == 5) {
				count = 0;
				x = 90;
				y += 60;
			}
			fButton[i].setBounds(x, y, 60, 60);
			count++;
			x += 60;
			fButton[i].addActionListener(listener);
			fButton[i].setBackground(Color.WHITE);
			f.add(fButton[i]);

		}

		// Door display
		this.door = new JButton[4];
		door[1] = new JButton(); // left close
		door[1].setBounds(200, 250, 50, 100);
		door[1].setBackground(Color.GRAY);
		door[0] = new JButton(); // left open
		door[0].setBounds(170, 250, 50, 100);
		door[0].setBackground(Color.GRAY);
		door[0].setVisible(false);
		door[2] = new JButton(); // right close
		door[2].setBounds(250, 250, 50, 100);
		door[2].setBackground(Color.GRAY);
		door[3] = new JButton(); // right open
		door[3].setBounds(280, 250, 50, 100);
		door[3].setBackground(Color.GRAY);
		door[3].setVisible(false);

		f.add(door[0]);
		f.add(door[1]);
		f.add(door[2]);
		f.add(door[3]);

		f.add(tf);
		f.setSize(500, 1000);
		f.setLayout(null);
		f.setVisible(true);

	}

	public void door(boolean open) {
		tf.setText(" Floor: " + elevator.getCurrentFLoor() + "  STOP ");
		if (open) {
			door[0].setVisible(true);
			door[1].setVisible(false);
			door[2].setVisible(false);
			door[3].setVisible(true);
		} else {
			door[0].setVisible(false);
			door[1].setVisible(true);
			door[2].setVisible(true);
			door[3].setVisible(false);
		}
	}

}
