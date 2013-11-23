package org.nightlabs.jfire.trade.admin.ui.editor;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.store.Unit;
import org.nightlabs.jfire.store.dao.UnitDAO;
import org.nightlabs.jfire.store.id.UnitID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

public class UnitEditorPageController 
extends ActiveEntityEditorPageController<Unit>
{
	public static final String[] FETCH_GROUP = new String[] {
		FetchPlan.DEFAULT, 
		Unit.FETCH_GROUP_NAME, 
		Unit.FETCH_GROUP_SYMBOL};
	
	public UnitEditorPageController(EntityEditor editor) {
		super(editor);
	}
	
	@Override
	protected String[] getEntityFetchGroups() {
		return FETCH_GROUP;
	}

	@Override
	protected Unit retrieveEntity(ProgressMonitor monitor) {
		Unit unit = UnitDAO.sharedInstance().getUnit(getUnitID(), FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		return unit;
	}

	@Override
	protected Unit storeEntity(Unit controllerObject, ProgressMonitor monitor) {
		monitor.beginTask("Storing Unit...", 100);
		try {
			UnitID unitID = (UnitID) JDOHelper.getObjectId(controllerObject);
			if (unitID == null)
				throw new IllegalStateException("JDOHelper.getObjectId(controllerObject) returned null for controllerObject = " + controllerObject); //$NON-NLS-1$

			Unit unit = UnitDAO.sharedInstance().storeUnit(
					controllerObject, false, getEntityFetchGroups(), getEntityMaxFetchDepth(),
					new SubProgressMonitor(monitor, 50)
			);

			return unit;
		} finally {
			monitor.done();
		}
	}
	
	protected UnitID getUnitID() {
		UnitEditorInput input = (UnitEditorInput) getEntityEditor().getEditorInput();
		return input.getJDOObjectID();
	}
}