package org.nightlabs.jfire.trade.admin.ui.editor;

import javax.jdo.FetchPlan;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.login.ui.part.ICloseOnLogoutEditorPart;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditor;
import org.nightlabs.jfire.store.Unit;
import org.nightlabs.jfire.store.dao.UnitDAO;
import org.nightlabs.jfire.store.id.UnitID;
import org.nightlabs.progress.ProgressMonitor;

public class UnitEditor 
extends ActiveEntityEditor
implements ICloseOnLogoutEditorPart
{
	public static final String ID_EDITOR = UnitEditor.class.getName();
	public static final String[] FETCH_GROUP = new String[] {
		FetchPlan.DEFAULT, 
		Unit.FETCH_GROUP_NAME, 
		Unit.FETCH_GROUP_SYMBOL};
	
	@Override
	protected String getEditorTitleFromEntity(Object entity) {
		Unit unit = (Unit) entity;
		return unit.getUnitID() + ": " + unit.getName().getText();
	}

	@Override
	protected Object retrieveEntityForEditorTitle(ProgressMonitor monitor) {
		UnitEditorInput input=(UnitEditorInput)getEditorInput();
		UnitID unitID = input.getJDOObjectID();
		assert unitID != null;
		Unit unit = UnitDAO.sharedInstance().getUnit(
				unitID, 
				FETCH_GROUP, 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
				monitor);
		return unit;
	}
}
