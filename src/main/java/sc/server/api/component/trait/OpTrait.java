package sc.server.api.component.trait;

import sc.server.api.component.OpProvider;
import sc.server.api.component.OpProvider.OpComponent;
import sc.server.api.component.OpProvider.OpComponent.Operation;
import sc.server.api.component.TraitProvider.TraitComponent;

public abstract class OpTrait<_Target extends OpProvider<_Target>, _Param, _DerivedTrait extends OpTrait<_Target, _Param, _DerivedTrait>> implements TraitComponent<_Target> {
	private OpComponent<_Target, _Param> opComponent;
	private Operation<_Target, _Param> op;
	private Class<_Param> paramClazz;

	public OpTrait(Class<_Param> paramClazz) {
		this.paramClazz = paramClazz;
		this.op = (target, param) -> this.op(target, param);
	}

	protected abstract boolean op(_Target target, _Param param);

	@Override
	public void init(_Target target) {
		this.opComponent = target.getOpComponent(paramClazz);
		opComponent.add(op);
	}

	@Override
	public void uninit(_Target target) {
		opComponent.remove(op);
	}
}