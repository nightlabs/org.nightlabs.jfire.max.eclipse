package org.nightlabs.jfire.trade.admin.ui.tariffuserset;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.entityuserset.ui.AbstractEntitySection;
import org.nightlabs.jfire.entityuserset.ui.AbstractEntityUserSetPage;
import org.nightlabs.jfire.entityuserset.ui.EntityUserSetPageControllerHelper;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class TariffUserSetPage 
extends AbstractEntityUserSetPage<Tariff> 
{	
	public static class Factory implements IEntityEditorPageFactory {
		public IFormPage createPage(FormEditor formEditor) {
			return new TariffUserSetPage(formEditor);
		}

		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new TariffUserSetPageController(editor);
		}
	}
	
	private TariffUserSetPageControllerHelper tariffUserSetPageController;
	
	/**
	 * @param editor
	 * @param id
	 * @param name
	 */
	public TariffUserSetPage(FormEditor editor) {
		super(editor, TariffUserSetPage.class.getName(), "Tariff User Set");
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.entityuserset.ui.AbstractEntityUserSetPage#createEntitySection(org.eclipse.ui.forms.editor.IFormPage, org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected AbstractEntitySection<Tariff> createEntitySection(IFormPage formPage, Composite parent) 
	{
		return new TariffSection(formPage, parent, "Tariffs");
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.entityuserset.ui.AbstractEntityUserSetPage#getEntityUserSetPageControllerHelper()
	 */
	@Override
	protected EntityUserSetPageControllerHelper<Tariff> getEntityUserSetPageControllerHelper() 
	{
		return ((TariffUserSetPageController)getPageController()).getTariffUserSetPageControllerHelper();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#getPageFormTitle()
	 */
	@Override
	protected String getPageFormTitle() {
		return "Tariff User Sets";
	}

}
