package flappybird;

import javax.swing.JFrame;

public class flappybird {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame jf = new JFrame();
		jf.setSize(900,700);
		board fboard = new board();
		jf.add(fboard);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);

	}

}
