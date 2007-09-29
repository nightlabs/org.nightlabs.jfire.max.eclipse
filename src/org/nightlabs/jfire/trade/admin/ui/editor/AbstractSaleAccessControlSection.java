package org.nightlabs.jfire.trade.admin.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.editor.MessageSectionPart;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite;
import org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlHelper;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 * 
 * @author Daniel.Mazurek [at] NightLabs [dot] de 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class AbstractSaleAccessControlSection 
//extends ToolBarSectionPart
extends MessageSectionPart
implements IProductTypeSectionPart
{
	private IProductTypeDetailPage detailPage;

	public AbstractSaleAccessControlSection(IProductTypeDetailPage page, Composite parent, int style) {
		this(page, parent, style,
				Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractSaleAccessControlSection.title"));		 //$NON-NLS-1$
	}

	public AbstractSaleAccessControlSection(IProductTypeDetailPage page, Composite parent, int style, String title) {
		super(page, parent, style, title);
		this.detailPage = page;
		saleAccessControlComposite = new SaleAccessControlComposite(
				getContainer(), SWT.NONE, createSaleAccessControlHelper(), false, this);		
		saleAccessControlComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));				
	}
	
	public AbstractSaleAccessControlSection(IProductTypeDetailPage page, Composite parent) {
		this(page, parent, ExpandableComposite.TITLE_BAR);
//		super(page, parent, ExpandableComposite.TITLE_BAR, 
//				Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractSaleAccessControlSection.title"));		 //$NON-NLS-1$
//		this.detailPage = page;
//		saleAccessControlComposite = new SaleAccessControlComposite(
//				getContainer(), SWT.NONE, createSaleAccessControlHelper(), false, this);		
//		saleAccessControlComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
	}
	
	private SaleAccessControlComposite saleAccessControlComposite = null;
	public SaleAccessControlComposite getSaleAccessControlComposite() {
		return saleAccessControlComposite;
	}
	
	protected abstract SaleAccessControlHelper createSaleAccessControlHelper();

	public ProductType getProductType() {
		return saleAccessControlComposite.getSaleAccessControlHelper().getProductType();
	}

	public void setProductType(ProductType productType) {
		saleAccessControlComposite.setProductType(productType);
	}

	@Override
	public void commit(boolean save) {
		if (isDirty()) {
			ProductTypeSaleAccessStatus saleAccessStatus = new ProductTypeSaleAccessStatus(
				saleAccessControlComposite.isPublished(),
				saleAccessControlComposite.isConfirmed(),
				saleAccessControlComposite.isSaleable(),
				saleAccessControlComposite.isClosed()
			);
			detailPage.getProductTypeDetailPageController().setProductTypeSaleAccessStatus(saleAccessStatus);
		}
		super.commit(save);		
	}
}
