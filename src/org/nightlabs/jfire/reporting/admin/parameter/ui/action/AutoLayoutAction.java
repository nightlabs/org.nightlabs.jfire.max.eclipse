package org.nightlabs.jfire.reporting.admin.parameter.ui.action;

import org.eclipse.gef.ui.actions.EditorPartAction;
import org.nightlabs.jfire.reporting.admin.parameter.ui.ReportParameterEditor;
import org.nightlabs.jfire.reporting.admin.parameter.ui.command.AutoLayoutCommand;
import org.nightlabs.jfire.reporting.admin.parameter.ui.resource.Messages;

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
		setText(Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.action.AutoLayoutAction.label")); //$NON-NLS-1$
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
