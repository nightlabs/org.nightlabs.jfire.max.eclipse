package org.nightlabs.jfire.reporting.admin.parameter.action;

import org.eclipse.gef.ui.actions.EditorPartAction;
import org.nightlabs.jfire.reporting.admin.parameter.ReportParameterEditor;
import org.nightlabs.jfire.reporting.admin.parameter.command.AutoLayoutCommand;
import org.nightlabs.jfire.reporting.admin.parameter.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class AutoLayoutAction 
extends EditorPartAction 
{
	public static final String ID = AutoLayoutAction.class.getName();
	
	public AutoLayoutAction(ReportParameterEditor part) {
		super(part);
		setId(ID);
		setText(Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.action.AutoLayoutAction.label")); //$NON-NLS-1$
	}

	public ReportParameterEditor getReportParameterEditor() {
		return (ReportParameterEditor) getEditorPart();
	}
	
	@Override
	protected boolean calculateEnabled() {
		return true;
	}
	
	@Override
	public void run() 
	{
		execute(new AutoLayoutCommand(
				getReportParameterEditor().getValueAcquisitionSetup(),
				getReportParameterEditor().getRootEditPart().getViewer()));
	}
}
