package shadertool.graph;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;

class ClusterVertexSizeFunction<V> implements Transformer<V,Integer> {
	int size;
    public ClusterVertexSizeFunction(Integer size) {
        this.size = size;
    }

    public Integer transform(V v) {
        if(v instanceof Graph) {
            return 50;
        }
        return size;
    }
}