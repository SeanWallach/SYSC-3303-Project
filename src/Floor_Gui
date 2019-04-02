import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class Floor_Gui {

	
	private int x;
	private int y;
	
	private Floor F;
	private JButton up[];
	private JButton down[];
	
	public Floor_Gui(int floors,Floor floor) {
		
		JFrame f=new JFrame("Floor");  
		F = floor;

		Font font = new Font("SansSerif", Font.BOLD, 20);
		
		up = new JButton[floors];
		down = new JButton[floors];
		
		ActionListener listener = new ActionListener() { //the floor button action when pressed 
			@Override
			public void actionPerformed(java.awt.event.ActionEvent arg0) {
				if (arg0.getSource() instanceof JButton) {
					JButton BB = ((JButton) arg0.getSource());
	                String text = BB.getActionCommand();
	                BB.setBackground(Color.RED);
	                int request = Integer.parseInt(text);
	                if(BB.getText().equals("UP")) {
	                	//send elevator floor num up
	                	F.sendInstructions(request, 1);
	                	BB.setBackground(Color.RED);
	                }
	                else {
	                	//send elevator floor num down
	                	F.sendInstructions(request, 2);
	                	BB.setBackground(Color.RED);
	                }
	  
	            }
			}
	    };
		
		
		
		
		int count=0;
		x = 0;
		y = 10;
		for(int i=floors;i>=0;i--) {
			
			JButton wall = new JButton(); //wall that separates buttons 
			wall.setBounds(x,y,350,10);
		    wall.setBackground(Color.GRAY);
		    f.add(wall);
			
			JTextField tf=new JTextField("Leve "+i);
			tf.setFont(font);
			tf.setHorizontalAlignment(JTextField.CENTER);
			tf.setBounds(x+10,y+15,150,35);
			f.add(tf);
			
			up[i] = new JButton("UP");
			up[i].setBounds(x+250, y+15, 75,25);
			up[i].setBackground(Color.WHITE);
			up[i].setActionCommand(""+i);
			up[i].addActionListener(listener);
			f.add(up[i]);
			
			down[i] = new JButton("DOWN");
			down[i].setBounds(x+250, y+40, 75,25);
			down[i].setBackground(Color.WHITE);
			down[i].setActionCommand(""+i);
			down[i].addActionListener(listener);
			f.add(down[i]);
			
			count++;
			y+=75;
			if(count==12) {
				y=10;
				JButton swall = new JButton(); //wall that separates buttons 
				swall.setBounds(340,y,10,1000);
			    swall.setBackground(Color.GRAY);
			    f.add(swall);
				x+=350;
				count = 0;
			}
		}

	    
	    
	    f.setSize(700,1000);  
	    f.setLayout(null);  
	    f.setVisible(true);   
	}
	
	public void clearButton(int floor) {
		up[floor-1].setBackground(Color.WHITE);
		down[floor-1].setBackground(Color.WHITE);
	}
	public static void main(String[] args) {
		//Floor_Gui fg = new Floor_Gui(22);
		
	} 
	
}
