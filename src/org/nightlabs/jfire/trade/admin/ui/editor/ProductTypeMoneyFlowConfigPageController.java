package org.nightlabs.jfire.trade.admin.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.book.LocalAccountantDelegate;
import org.nightlabs.jfire.base.jdo.IJDOObjectDAO;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.MoneyFlowMappingTree;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.store.ProductTypeDAO;
import org.nightlabs.util.Utils;

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
	
	public void doLoad(IProgressMonitor monitor) {
		// Load nothing as this is done by MoneyFlowConfigComposite.setProductType(...)
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeMoneyFlowConfigPageController.loadMoneyFlowConfigMonitor.task.name"), 2); //$NON-NLS-1$
		monitor.worked(1);
		ProductTypeType original = (ProductTypeType) ProductTypeDAO.sharedInstance().getProductType(
				getProductTypeID(),
				MoneyFlowMappingTree.DEFAULT_PTYPE_FETCH_GROUPS, 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new ProgressMonitorWrapper(monitor));
		ProductTypeType clone = Utils.cloneSerializable(original);
		setProductType(clone);
		monitor.worked(1);
	}

	public void doSave(IProgressMonitor monitor) 
	{
		getProductTypeDAO().storeJDOObject(getProductType(), false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new ProgressMonitorWrapper(monitor));
//		try {
//			AccountingManager am = AccountingManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//			am.storeLocalAccountantDelegate(localAccountantDelegate, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		for (IFormPage page : getPages()) {
//			if (page instanceof ProductTypeMoneyFlowConfigPage) {
//				ProductTypeMoneyFlowConfigPage moneyFlowPage = (ProductTypeMoneyFlowConfigPage) page;
//				LocalAccountantDelegate delegate = moneyFlowPage.getMoneyFlowSection().
//					getMoneyFlowConfigComposite().getProductTypeMappingTree().getDelegate();
//				LocalAccountantDelegateID delegateID = (LocalAccountantDelegateID) JDOHelper.getObjectId(delegate); 
//				LocalAccountantDelegateID originalID = (LocalAccountantDelegateID) JDOHelper.getObjectId(
//						getProductType().getLocalAccountantDelegate());
//				// Same LocalAccountantDelegate, only mappings hav been added or removed	
////				if (originalID.equals(delegateID)) {
//					try {
//						AccountingUtil.getAccountingManager().storeLocalAccountantDelegate(
//								delegate,
//								false,
//								null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT
//							);			
//					} catch (Exception evt) {
//						throw new RuntimeException(evt);
//					}					
////				} 
////				// new LocalAccountantDelegate has been assigned
////				else {
//					try {
//						AccountingUtil.getAccountingManager().assignLocalAccountantDelegateToProductType(
//								getProductTypeID(), 
//								(LocalAccountantDelegateID) JDOHelper.getObjectId(delegate));
//					} catch (Exception evt) {
//						throw new RuntimeException(evt);
//					}										
////				}
//			}
//		}
	}

	public LocalAccountantDelegate getLocalAccountantDelegate() {
		return localAccountantDelegate;
	}
	
	public void setLocalAccountantDelegate(LocalAccountantDelegate localAccountantDelegate) {
		this.localAccountantDelegate = localAccountantDelegate;
	}
}
