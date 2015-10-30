package shadertool.graph;

import java.awt.Shape;

import shadertool.nodes.ConversionNode;
import shadertool.nodes.InputNode;
import shadertool.nodes.OperationNode;
import shadertool.nodes.OutputNode;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;

class ClusterVertexShapeFunction<V> extends EllipseVertexShapeTransformer<V> {

    ClusterVertexShapeFunction() {
        setSizeTransformer(new ClusterVertexSizeFunction<V>(35));
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public Shape transform(V v) {
        if(v instanceof Graph) {
            int size = ((Graph)v).getVertexCount();
            if (size < 8) {
                int sides = Math.max(size, 3);
                return factory.getRegularPolygon(v, sides);
            }
            else {
                return factory.getRegularStar(v, size);
            }
        } else if (v instanceof InputNode){
        	return factory.getRegularPolygon(v, 3);
        } else if (v instanceof OperationNode){
        	return factory.getEllipse(v);
        } else if (v instanceof ConversionNode){
        	return factory.getRoundRectangle(v);
        } else if (v instanceof OutputNode){
        	return factory.getRectangle(v);
        }        
        return factory.getEllipse(v);
    }
}
