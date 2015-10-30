package shadertool.graph;

import java.awt.Color;

import org.apache.commons.collections15.Transformer;

import shadertool.nodes.Node;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;

class ClusterVertexFillPaintTransformer<V, Paint> implements Transformer<V, Paint> {
	private final Paint paint;
	@SuppressWarnings("rawtypes")
	VisualizationViewer vv;
	
	@SuppressWarnings("rawtypes")
    public ClusterVertexFillPaintTransformer(Paint paint, VisualizationViewer vv) {
        this.paint = paint;
        this.vv = vv;
    }

	@SuppressWarnings("unchecked")
	@Override
	public Paint transform(V v) {
		if(v instanceof Graph) {
			if (vv.getPickedVertexState().getPicked().contains(v))
				return (Paint) ((Color)paint).darker();
			else
				return paint;
        }
		if (vv.getPickedVertexState().isPicked((Node)v))
			return (Paint) ((Node)v).color.darker();
		else
			return (Paint) ((Node)v).color;
	}
	
}