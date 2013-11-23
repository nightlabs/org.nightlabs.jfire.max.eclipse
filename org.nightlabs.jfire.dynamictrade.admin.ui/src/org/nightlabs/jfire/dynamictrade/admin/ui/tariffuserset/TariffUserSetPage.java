package org.nightlabs.jfire.dynamictrade.admin.ui.tariffuserset;

import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.dynamictrade.dao.DynamicProductTypeDAO;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.trade.admin.ui.tariffuserset.AbstractTariffUserSetPage;
import org.nightlabs.jfire.trade.admin.ui.tariffuserset.TariffUserSetPageController;

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
			return new TariffUserSetPageController<DynamicProductType>(editor, DynamicProductTypeDAO.sharedInstance());
		}
	}
	
	/**
	 * @param editor
	 */
	public TariffUserSetPage(FormEditor editor) {
		super(editor);
	}

}
