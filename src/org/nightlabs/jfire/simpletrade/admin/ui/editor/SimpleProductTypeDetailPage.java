package org.nightlabs.jfire.simpletrade.admin.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.simpletrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeDetailPage;
import org.nightlabs.jfire.trade.admin.ui.editor.IProductTypeSectionPart;
import org.nightlabs.jfire.trade.admin.ui.editor.ownervendor.*;
/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SimpleProductTypeDetailPage
extends AbstractProductTypeDetailPage
{
	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link EventDetailPage}.
	 */
	public static class Factory implements IEntityEditorPageFactory {
		public IFormPage createPage(FormEditor formEditor) {
			return new SimpleProductTypeDetailPage(formEditor);
		}

		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new SimpleProductTypeDetailPageController(editor);
		}
	}

	public SimpleProductTypeDetailPage(FormEditor editor) {
		super(editor, SimpleProductTypeDetailPage.class.getName(), Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.editor.SimpleProductTypeDetailPage.title")); //$NON-NLS-1$
	}
	
	@Override
	protected IProductTypeSectionPart createNameSection(Composite parent) {
		return new SimpleProductTypeNameSection(this, parent, getSectionStyle());
	}

	@Override
	protected IProductTypeSectionPart createNestedProductTypesSection(Composite parent) {
		return new SimpleProductTypeNestedProductTypesSection(this, parent, getSectionStyle());
	}

	@Override
	protected IProductTypeSectionPart createSaleAccessControlSection(Composite parent) {
		return new SimpleProductTypeSaleAccessControlSection(this, parent, getSectionStyle());
	}
	

	@Override
	protected IProductTypeSectionPart createOwnerSection(Composite parent) {
		return new OwnerConfigSection(this, parent, getSectionStyle());
	}
	
	@Override
	protected IProductTypeSectionPart createVendorSection(Composite parent) {
		return new VendorConfigSection(this, parent, getSectionStyle());
	}
}
