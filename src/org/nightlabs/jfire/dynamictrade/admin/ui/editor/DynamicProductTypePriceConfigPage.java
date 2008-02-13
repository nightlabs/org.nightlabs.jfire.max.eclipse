package org.nightlabs.jfire.dynamictrade.admin.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.dynamictrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractGridPriceConfigPage;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractGridPriceConfigSection;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public class DynamicProductTypePriceConfigPage
extends AbstractGridPriceConfigPage
{
	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link PriceConfigPage}.
	 */
	public static class Factory implements IEntityEditorPageFactory {
		public IFormPage createPage(FormEditor formEditor) {
			return new DynamicProductTypePriceConfigPage(formEditor);
		}

		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new DynamicProductTypePriceConfigPageController(editor);
		}
	}
	
	/**
	 * @param editor
	 * @param id
	 * @param name
	 */
	public DynamicProductTypePriceConfigPage(FormEditor editor) {
		super(editor, DynamicProductTypePriceConfigPage.class.getName(),
				Messages.getString("org.nightlabs.jfire.dynamictrade.admin.ui.editor.DynamicProductTypePriceConfigPage.title")); //$NON-NLS-1$
	}

	@Override
	protected AbstractGridPriceConfigSection createGridPriceConfigSection(Composite parent) {
		return new DynamicProductTypePriceConfigSection(this, parent, ExpandableComposite.TITLE_BAR);
	}
	
}
