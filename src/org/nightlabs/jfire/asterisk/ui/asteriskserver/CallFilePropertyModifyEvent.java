package org.nightlabs.jfire.asterisk.ui.asteriskserver;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class CallFilePropertyModifyEvent 
{
	private CallFilePropertyTable callFilePropertyTable;

	public CallFilePropertyModifyEvent(CallFilePropertyTable callFilePropertyTable){

		this.callFilePropertyTable=callFilePropertyTable;
	}

	public CallFilePropertyTable getCallFilePropertyTable(){
		return callFilePropertyTable;
	}
}