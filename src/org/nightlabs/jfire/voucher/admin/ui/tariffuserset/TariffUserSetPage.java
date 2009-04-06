package org.nightlabs.jfire.voucher.admin.ui.tariffuserset;

import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.trade.admin.ui.tariffuserset.AbstractTariffUserSetPage;
import org.nightlabs.jfire.trade.admin.ui.tariffuserset.TariffUserSetPageController;
import org.nightlabs.jfire.voucher.dao.VoucherTypeDAO;
import org.nightlabs.jfire.voucher.store.VoucherType;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class TariffUserSetPage extends AbstractTariffUserSetPage 
{
	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link TariffUserSetPage}.
	 */
	public static class Factory implements IEntityEditorPageFactory {
		public IFormPage createPage(FormEditor formEditor) {
			return new TariffUserSetPage(formEditor);
		}

		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new TariffUserSetPageController<VoucherType>(editor, VoucherTypeDAO.sharedInstance());
		}
	}
	
	/**
	 * @param editor
	 */
	public TariffUserSetPage(FormEditor editor) {
		super(editor);
	}

}
