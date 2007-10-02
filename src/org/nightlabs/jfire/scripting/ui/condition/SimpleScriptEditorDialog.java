package org.nightlabs.jfire.scripting.ui.condition;

import java.util.Collection;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.jfire.scripting.condition.Script;
import org.nightlabs.jfire.scripting.condition.ScriptConditioner;
import org.nightlabs.jfire.scripting.ui.resource.Messages;

public class SimpleScriptEditorDialog 
extends Dialog 
{
	public SimpleScriptEditorDialog(Shell shell, 
			Collection<ScriptConditioner> scriptConditioners, 
			Script script)
	{
		super(shell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.scriptConditioners = scriptConditioners;
		this.script = script;
	}

	@Override
	public void create() {
		super.create();		
		getShell().setText(Messages.getString("org.nightlabs.jfire.scripting.ui.condition.SimpleScriptEditorDialog.shell.text")); //$NON-NLS-1$
		getShell().setSize(500, 350);
		getShell().setMinimumSize(400, 300);
	}

	private Collection<ScriptConditioner> scriptConditioners;
	private SimpleScriptEditorComposite scriptEditorComp;
	private Script script;
	
	@Override
	protected Control createDialogArea(Composite comp) 
	{		 
		scriptEditorComp = new SimpleScriptEditorComposite(scriptConditioners, comp, SWT.NONE);
		scriptEditorComp.setScript(script);
		return scriptEditorComp;
	}

	public Script getScript() {
		return scriptEditorComp.getScript();
	}

	public static final int ID_DELETE_SCRIPT = 123456;
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		Button deleteScriptButton = createButton(
				parent, ID_DELETE_SCRIPT, 
				Messages.getString("org.nightlabs.jfire.scripting.ui.condition.SimpleScriptEditorDialog.deleteScriptButton.text"),  //$NON-NLS-1$
				false);
		deleteScriptButton.addSelectionListener(new SelectionListener() 
		{		
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(ID_DELETE_SCRIPT);
				close();
			}		
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}		
		});
	}
	
}