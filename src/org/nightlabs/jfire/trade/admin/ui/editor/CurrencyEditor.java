package org.nightlabs.jfire.trade.admin.ui.editor;

import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.dao.CurrencyDAO;
import org.nightlabs.jfire.accounting.id.CurrencyID;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditor;
import org.nightlabs.jfire.base.ui.login.part.ICloseOnLogoutEditorPart;
import org.nightlabs.progress.ProgressMonitor;

public class CurrencyEditor extends ActiveEntityEditor implements ICloseOnLogoutEditorPart
{
	public static final String ID_EDITOR = CurrencyEditor.class.getName();

	@Override
	protected String getEditorTitleFromEntity(Object entity) {
		Currency currency = (Currency) entity;
		return currency.getCurrencySymbol();
	}

	@Override
	protected Object retrieveEntityForEditorTitle(ProgressMonitor monitor) {
		CurrencyEditorInput input=(CurrencyEditorInput)getEditorInput();
		CurrencyID currencyID = input.getJDOObjectID();
		Currency currency = CurrencyDAO.sharedInstance().getCurrency(currencyID, monitor);
		return currency;
	}
}
