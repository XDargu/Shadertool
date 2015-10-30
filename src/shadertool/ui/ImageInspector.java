package shadertool.ui;

import javax.swing.JFrame;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class ImageInspector extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private boolean started = false;
	//private CanvasGameContainer canvas;
	private InspectorRun runner = new InspectorRun();

	private class InspectorRun extends BasicGame {
		public InspectorRun() {
			super("Inspector");
		}

		private Image img;
	
		@Override
		public void init(GameContainer gc) throws SlickException {
			gc.setAlwaysRender(true);
			gc.setUpdateOnlyWhenVisible(false);
			gc.setVerbose(true);
		}
	
		@Override
		public void update(GameContainer gc, int delta) throws SlickException {
		}
	
		@Override
		public void render(GameContainer gc, Graphics g) throws SlickException {
			if (img != null) {
				g.drawString("Hello World", 100, 100);
				g.drawImage(img, 0, 0);
			}
		}
		
		public void updated(Image img) {
			this.img = img;
		}
	};
	
	public void updated(Image img) throws SlickException {
		runner.updated(img);
		
		if (!started && this.isVisible()) {
			//canvas.start();
			started = true;
		}
	}	

	/**
	 * Create the frame.
	 * @throws SlickException 
	 */
	public ImageInspector() throws SlickException {
		setTitle("Inspector");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		//canvas = new CanvasGameContainer(runner, true);
		//getContentPane().add(canvas);
		/*canvas.setBorder(new EmptyBorder(5, 5, 5, 5));
		canvas.setLayout(new BorderLayout(0, 0));
		setContentPane(canvas);*/
	}
	
	@Override
	public void dispose() {
		//canvas.dispose();
		super.dispose();
	}

}