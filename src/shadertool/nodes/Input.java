package shadertool.nodes;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;


public class Input implements java.io.Serializable, UpdatedFunction {
	private static final long serialVersionUID = 1L;

	public final String name;
	public final IOType type;
	private final UpdatedFunction callback;
	public transient Image img;
	public boolean used;
	
	public Input(String name, IOType type, UpdatedFunction callback) {
		this.name = name;
		this.type = type;
		this.callback = callback;
		this.used = false;
	}
	
	public void updated(Image _img) throws SlickException {
		img = _img;
		callback.updated(_img);
	}
}
