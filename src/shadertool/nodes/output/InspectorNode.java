package shadertool.nodes.output;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.imageout.ImageOut;

import shadertool.nodes.IOType;
import shadertool.nodes.Input;
import shadertool.nodes.OutputNode;
import shadertool.nodes.UpdatedFunction;

public class InspectorNode extends OutputNode implements UpdatedFunction {
	private static final long serialVersionUID = 1L;
	
	private Input imgIn = new Input("Imagen", IOType.Image2f, this);
	
	//private transient ImageInspector frame;

	public InspectorNode() {
		super("Inspector");
		inputs.add(imgIn);
	}

	@Override
	public void initialize() {
		/*try {
			frame = new ImageInspector();
		} catch (SlickException ex) {
			ex.printStackTrace();
		}
		
    	actions.add(new AbstractAction("Visualizar") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (imgIn.img != null)
					frame.setVisible(true);
        	}
        });*/
		
		actions.add(new AbstractAction("Guardar imagen") {
			private static final long serialVersionUID = 1L;
			
			public void actionPerformed(ActionEvent e) {
				try {
					Image target = inputs.get(0).img;
					JFileChooser fc = new JFileChooser();
					 fc.setFileFilter(new FileNameExtensionFilter(
							 "Imágenes (*.jpg, *.gif, *.png)",
							 "jpg",
							 "gif",
							 "png"));
					 int returnVal = fc.showSaveDialog(null);
					 if (returnVal == JFileChooser.APPROVE_OPTION)
					 {
				         if (target != null) {			         
				            Graphics g = target.getGraphics();
				         	g.copyArea(target, 0, 0);
				         	ImageOut.write(target, fc.getSelectedFile().getPath() + ".png");
				         }
					 }
			      } catch (Exception ex) {
			         ex.printStackTrace();
			      }
        	}
		});
	}

	@Override
	public void updated(Image img) throws SlickException {
		//frame.updated(img);
	}

}
