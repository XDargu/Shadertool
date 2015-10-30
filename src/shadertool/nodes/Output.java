package shadertool.nodes;

import java.util.ArrayList;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import shadertool.graph.Link;

public class Output implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private ArrayList<Link> subscribers = new ArrayList<Link>();

	public final String name;
	public final IOType type;
	public transient Image img;
	
	public Output(String name, IOType type) {
		this.name = name;
		this.type = type;
	}
	
	public void replace(Image _img) throws SlickException {
		if (img != null) {
			img.destroy();
		}
		
		img = _img;
	}
	
	public void updated() throws SlickException {
		for (Link l: subscribers) {
			l.updated(img);
		}
	}
	
	public void create(int width, int height) throws SlickException {
		if (img != null) {
			if (img.getWidth() == width && img.getHeight() == height) {
				img.getGraphics().clear();
				return;
			}
			
			img.destroy();
		}
		
		img = new Image(width, height);
	}
	
	public void flush() throws SlickException {
		if (img != null) {
			img.destroy();
			img = null;
		}
	}
	
	public void subscribe(Link subscriber) {
		subscribers.add(subscriber);
	}
	
	public void unsubscribe(Link subscriber) {
		for (Link l: subscribers) {
			System.out.println(l);
		}
		subscribers.remove(subscriber);
		System.out.println("Despues");
		for (Link l: subscribers) {
			System.out.println(l);
		}
	}
}