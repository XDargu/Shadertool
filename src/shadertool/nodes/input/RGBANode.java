package shadertool.nodes.input;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import shadertool.Settings;
import shadertool.nodes.IOType;
import shadertool.nodes.InputNode;
import shadertool.nodes.Output;

public class RGBANode extends InputNode {
	private static final long serialVersionUID = 1L;
	
	private String type;
	protected Output imageOut = new Output("Imagen", IOType.Image2f) {
		private static final long serialVersionUID = 1L;

		@Override
		public void flush() throws SlickException {}
	};

	protected transient boolean drawn;
	protected transient Image img;

	public RGBANode() {
		this("RGBA");
	}
	
	protected RGBANode(String type) {
		super("Imagen " + type);
		
		this.type = type;
		color = Color.green;
		outputs.add(imageOut);
	}

	@Override
	public void initialize() throws SlickException {
		actions.add(new AbstractAction("Cargar imagen") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
        		try
				{
					 JFileChooser fc = new JFileChooser();
					 fc.setFileFilter(new FileNameExtensionFilter(
							 "Imágenes (*.jpg, *.gif, *.png)",
							 "jpg",
							 "gif",
							 "png"));
					 int returnVal = fc.showOpenDialog(null);
					 if (returnVal == JFileChooser.APPROVE_OPTION)
					 {
						 if (Settings.autoNameNodes)
							 name = fc.getSelectedFile().getName() + " (" + type + ")";
						 img = new Image(fc.getSelectedFile().getPath(), false, Image.FILTER_NEAREST);
						 imageOut.create(img.getWidth(), img.getHeight());
						 drawn = false;
					 }
				} catch (SlickException ex) {
					ex.printStackTrace();
				}
        	}
        });
	}
	
	@Override
	public void run() throws SlickException {
		if (imageOut.img != null && img != null) {
			if (!drawn) {				
				Graphics g = imageOut.img.getGraphics();
				g.drawImage(img, 0, 0);
				g.flush();
				drawn = true;
			}
			imageOut.updated();
		}
	}
}
