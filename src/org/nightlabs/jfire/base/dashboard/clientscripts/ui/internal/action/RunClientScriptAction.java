package org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.action;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.jfire.base.dashboard.clientscripts.ui.resource.Messages;
import org.nightlabs.jfire.dashboard.DashboardGadgetClientScriptsConfig.ClientScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs a given {@link ClientScript} by utilising an appropriate JavaScript script engine.
 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
 */
public class RunClientScriptAction extends Action {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RunClientScriptAction.class);

	private ClientScript clientScript;
	
	private boolean confirmProcessing;
	
	public RunClientScriptAction(ClientScript clientScript, boolean confirmProcessing) {
		setId(RunClientScriptAction.class.getName());
		setText(Messages.getString("org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.action.RunClientScriptAction.action.text")); //$NON-NLS-1$
		this.clientScript = clientScript;
		this.confirmProcessing = confirmProcessing;
	}
	
	@Override
	public void run() {
		if (confirmProcessing && !MessageDialog.openQuestion(
				Display.getCurrent().getActiveShell(), 
				Messages.getString(
					"org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.action.RunClientScriptAction.questionDialog.title"),  //$NON-NLS-1$
				String.format(Messages.getString(
					"org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.action.RunClientScriptAction.questionDialog.message"),  //$NON-NLS-1$
				clientScript.getName())))	
			return;
		
		ScriptEngineManager manager = new ScriptEngineManager();
	    
		// Engine
		// =========================
		// EngineName: Mozilla Rhino
		// EngineVersion: 1.6 release 2
		// LanguageName: ECMAScript
		// LanguageVersion: 1.6
		// Extensions: [js]
		// MimeTypes: [application/javascript, application/ecmascript, text/javascript, text/ecmascript]
		// Names: [js, rhino, JavaScript, javascript, ECMAScript, ecmascript]
//		List<ScriptEngineFactory> factoryList = manager.getEngineFactories();
		
	    ScriptEngine engine = manager.getEngineByName("js"); //$NON-NLS-1$
	    
	    if (engine == null)
	    	throw new IllegalStateException(Messages.getString("org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.action.RunClientScriptAction.error.engineNotFound")); //$NON-NLS-1$
	    
	    try {
	    	LOGGER.debug("Executing script '"+ clientScript.getName() +"'..."); //$NON-NLS-1$ //$NON-NLS-2$
	    	
	    	//engine.eval(clientScript.getContent()) //TODO in
	    	String value = String.valueOf(engine.eval(clientScript.getContent())); //TODO out
	    	System.out.println("Value: " + value); //TODO out
	    	
		} catch (ScriptException e) {
			throw new RuntimeException(e);
		}
	}
}
