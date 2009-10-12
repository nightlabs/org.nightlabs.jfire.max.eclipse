package org.nightlabs.jfire.trade.admin.ui.editor;

import javax.jdo.FetchPlan;

import org.eclipse.ui.IEditorInput;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.dao.CurrencyDAO;
import org.nightlabs.jfire.accounting.id.CurrencyID;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.progress.ProgressMonitor;

/**
 *
 * @author vince
 *
 */
public class CurrencyEditorPageController
extends ActiveEntityEditorPageController<Currency> {

	public CurrencyEditorPageController(EntityEditor editor) {
		super(editor);

	}

	@Override
	protected IEditorInput createNewInstanceEditorInput() {
		return new CurrencyEditorInput(getCurrencyID());
	}

	@Override
	protected String[] getEntityFetchGroups() {

		return new String[]{ FetchPlan.DEFAULT};
	}

	@Override
	protected Currency retrieveEntity(ProgressMonitor monitor) {

		 Currency currency = CurrencyDAO.sharedInstance().getCurrency(getCurrencyID(), monitor);
		return currency;
	}

	@Override
	protected Currency storeEntity(Currency controllerObject,
			ProgressMonitor monitor) {

		return CurrencyDAO.sharedInstance().storeCurrency(controllerObject, true, getEntityFetchGroups(),getEntityMaxFetchDepth(), monitor);

	}

	@Override
	public void fireModifyEvent(Object oldObject, Object newObject, boolean resetDirtyState) {
		super.fireModifyEvent(oldObject, newObject, resetDirtyState);
	}

	protected CurrencyID getCurrencyID() {

	CurrencyEditorInput input = (CurrencyEditorInput) getEntityEditor().getEditorInput();
		return input.getJDOObjectID();
	}

	public Currency getCurrency(){
		return getControllerObject();
	}

}
