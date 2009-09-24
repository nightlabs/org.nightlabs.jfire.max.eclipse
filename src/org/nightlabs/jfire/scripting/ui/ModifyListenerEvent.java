package org.nightlabs.jfire.scripting.ui;


public class ModifyListenerEvent {


	public ModifyListenerEvent(ScriptParameterTable scriptParameterTable){

		this.scriptParameterTable=scriptParameterTable;
	}


	private ScriptParameterTable scriptParameterTable;
	public ScriptParameterTable getScriptParameterTable(){
		return scriptParameterTable;
	}

}
