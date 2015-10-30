package shadertool.nodes;

public enum IOType {
	Image1f,
	Image2f,
	Image1i,
	Image2i,
	Image1u,
	Image2u;
	
	public boolean isCompatible(IOType type) {
		boolean ret = false;
		
		if (this == type)
			ret = true;
		
		return ret;
	}
}
