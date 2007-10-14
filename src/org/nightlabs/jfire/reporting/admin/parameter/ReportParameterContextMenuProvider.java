package org.nightlabs.jfire.reporting.admin.parameter;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionFactory;
import org.nightlabs.jfire.reporting.admin.parameter.action.AutoLayoutAction;
import org.nightlabs.jfire.reporting.admin.parameter.action.AutoLayoutPagesAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ReportParameterContextMenuProvider 
extends ContextMenuProvider 
{

	/**
	 * @param viewer
	 */
	public ReportParameterContextMenuProvider(EditPartViewer viewer, ActionRegistry registry) {
		super(viewer);
		setActionRegistry(registry);
	}

	private ActionRegistry actionRegistry;
	private ActionRegistry getActionRegistry() {
		return actionRegistry;
	}
	private void setActionRegistry(ActionRegistry registry) {
		actionRegistry = registry;		
	}
	
	@Override
	public void buildContextMenu(IMenuManager manager) 
	{
		GEFActionConstants.addStandardActionGroups(manager);

		IAction action;

		action = getActionRegistry().getAction(ActionFactory.UNDO.getId());
		if (action != null)
			manager.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

		action = getActionRegistry().getAction(ActionFactory.REDO.getId());
		if (action != null)
			manager.appendToGroup(GEFActionConstants.GROUP_UNDO, action);
		
		action = getActionRegistry().getAction(AutoLayoutAction.ID);
		if (action != null)		
			manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

		action = getActionRegistry().getAction(AutoLayoutPagesAction.ID);
		if (action != null)		
			manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		
		action = getActionRegistry().getAction(ActionFactory.DELETE.getId());
		if (action != null)		
			manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		
//		action = getActionRegistry().getAction(DeleteReportItemAction.ID);
//		manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		
//		action = getActionRegistry().getAction(ActionFactory.PASTE.getId());
//		if (action.isEnabled())
//			manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

//		action = getActionRegistry().getAction(GEFActionConstants.DIRECT_EDIT);
//		if (action.isEnabled())
//			manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
//		
//		// Alignment Actions
//		MenuManager submenu = new MenuManager("Align");
//
//		action = getActionRegistry().getAction(GEFActionConstants.ALIGN_LEFT);
//		if (action.isEnabled())
//			submenu.add(action);
//
//		action = getActionRegistry().getAction(GEFActionConstants.ALIGN_CENTER);
//		if (action.isEnabled())
//			submenu.add(action);
//
//		action = getActionRegistry().getAction(GEFActionConstants.ALIGN_RIGHT);
//		if (action.isEnabled())
//			submenu.add(action);
//			
//		submenu.add(new Separator());
//		
//		action = getActionRegistry().getAction(GEFActionConstants.ALIGN_TOP);
//		if (action.isEnabled())
//			submenu.add(action);
//
//		action = getActionRegistry().getAction(GEFActionConstants.ALIGN_MIDDLE);
//		if (action.isEnabled())
//			submenu.add(action);
//
//		action = getActionRegistry().getAction(GEFActionConstants.ALIGN_BOTTOM);
//		if (action.isEnabled())
//			submenu.add(action);
//
//		if (!submenu.isEmpty())
//			manager.appendToGroup(GEFActionConstants.GROUP_REST, submenu);
//
//		action = getActionRegistry().getAction(ActionFactory.SAVE.getId());
//		manager.appendToGroup(GEFActionConstants.GROUP_SAVE, action);
	}

}
