package appeng.core.me.parts.part;

public class PartDummy extends PartBase<PartDummy, DummyState> {

	public PartDummy(){
	}

	public PartDummy(boolean supportsRotation){
		super(supportsRotation);
	}

	@Override
	public DummyState createNewState(){
		return new DummyState(this);
	}

}
