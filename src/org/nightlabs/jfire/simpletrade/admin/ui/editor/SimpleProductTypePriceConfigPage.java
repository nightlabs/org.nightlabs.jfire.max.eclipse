package org.nightlabs.jfire.simpletrade.admin.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.simpletrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractGridPriceConfigPage;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractGridPriceConfigSection;
import org.nightlabs.jfire.trade.admin.ui.editor.ProductTypePriceConfigPageController;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SimpleProductTypePriceConfigPage
//extends EntityEditorPageWithProgress
extends AbstractGridPriceConfigPage
{
	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link PriceConfigPage}.
	 */
	public static class Factory implements IEntityEditorPageFactory {
		public IFormPage createPage(FormEditor formEditor) {
			return new SimpleProductTypePriceConfigPage(formEditor);
		}

		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new ProductTypePriceConfigPageController(editor);
		}
	}
	
	/**
	 * @param editor
	 * @param id
	 * @param name
	 */
	public SimpleProductTypePriceConfigPage(FormEditor editor) {
		super(editor, SimpleProductTypePriceConfigPage.class.getName(),
				Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.editor.SimpleProductTypePriceConfigPage.title")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.admin.ui.editor.AbstractGridPriceConfigPage#createGridPriceConfigSection(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected AbstractGridPriceConfigSection createGridPriceConfigSection(Composite parent) {
		return new SimpleProductTypePriceConfigSection(this, parent, ExpandableComposite.TITLE_BAR);
	}

}
