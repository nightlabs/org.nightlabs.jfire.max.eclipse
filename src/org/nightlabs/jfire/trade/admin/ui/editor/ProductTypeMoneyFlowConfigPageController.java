package org.nightlabs.jfire.trade.admin.ui.editor;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.accounting.book.LocalAccountantDelegate;
import org.nightlabs.jfire.base.jdo.IJDOObjectDAO;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.MoneyFlowMappingTree;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.store.ProductTypeDAO;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class ProductTypeMoneyFlowConfigPageController<
	ProductTypeType extends ProductType, JDOObjectDAOType extends IJDOObjectDAO<ProductTypeType>>
extends AbstractProductTypePageController<ProductTypeType>
{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param editor
	 */
	public ProductTypeMoneyFlowConfigPageController(EntityEditor editor) {
		super(editor);
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public ProductTypeMoneyFlowConfigPageController(EntityEditor editor,
			boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}

	private LocalAccountantDelegate localAccountantDelegate;
	
	protected abstract JDOObjectDAOType getProductTypeDAO();
	
	@Override
	protected String[] getEntityFetchGroups() {
		return MoneyFlowMappingTree.DEFAULT_PTYPE_FETCH_GROUPS;
	}

	@Override
	protected ProductTypeType storeEntity(ProductTypeType controllerObject,
			ProgressMonitor monitor) {
		return getProductTypeDAO().storeJDOObject(controllerObject, true, getEntityFetchGroups(), getEntityMaxFetchDepth(), monitor);
	}
	
	@Override
	protected ProductTypeType retrieveEntity(ProgressMonitor monitor) {
		// Load nothing as this is done by MoneyFlowConfigComposite.setProductType(...)
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeMoneyFlowConfigPageController.loadMoneyFlowConfigMonitor.task.name"), 3); //$NON-NLS-1$
		monitor.worked(1);
		ProductTypeType productType = (ProductTypeType) ProductTypeDAO.sharedInstance().getProductType(
				getProductTypeID(),
				getEntityFetchGroups(),
				getEntityMaxFetchDepth(),
				new SubProgressMonitor(monitor, 2));
		monitor.done();
		return productType;
	}

	
	@Override
	
	protected ProductTypeType getExtendedProductType(ProgressMonitor monitor)
	{
		return  (ProductTypeType) ProductTypeDAO.sharedInstance().getProductType(getExtendedProductTypeID(),
				getEntityFetchGroups(),
				getEntityMaxFetchDepth(),
				monitor);	
		
	}
	
	
	public LocalAccountantDelegate getLocalAccountantDelegate() {
		return localAccountantDelegate;
	}
	
	public void setLocalAccountantDelegate(LocalAccountantDelegate localAccountantDelegate) {
		this.localAccountantDelegate = localAccountantDelegate;
	}
}
