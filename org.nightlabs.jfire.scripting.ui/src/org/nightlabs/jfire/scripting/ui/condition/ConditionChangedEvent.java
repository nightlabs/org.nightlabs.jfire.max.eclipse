package org.nightlabs.jfire.scripting.ui.condition;

import org.nightlabs.jfire.scripting.condition.ICondition;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ConditionChangedEvent
{
	public ConditionChangedEvent(ICondition condition, SimpleConditionComposite conditionComp) {
		this.condition = condition;
		this.conditionComp = conditionComp;
	}
	
	private ICondition condition;
	public ICondition getCondition() {
		return condition;
	}
	
	private SimpleConditionComposite conditionComp;
	public SimpleConditionComposite getConditionComposite() {
		return conditionComp;
	}
}
