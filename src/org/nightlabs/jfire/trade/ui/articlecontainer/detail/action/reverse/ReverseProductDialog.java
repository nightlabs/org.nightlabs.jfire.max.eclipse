package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse;

import java.util.Collection;
import java.util.ResourceBundle;

import javax.jdo.FetchPlan;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.store.id.ProductID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.dao.ArticleContainerDAO;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuery;
import org.nightlabs.jfire.trade.query.OrderQuery;
import org.nightlabs.jfire.trade.ui.overview.order.OrderListComposite;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 * @deprecated not used
 */
public class ReverseProductDialog 
extends ResizableTitleAreaDialog 
{
	private static final Logger logger = Logger.getLogger(ReverseProductDialog.class);
	
	private Text productIDText;
	private OrderListComposite orderComp;
	
	/**
	 * @param shell
	 * @param resourceBundle
	 */
	public ReverseProductDialog(Shell shell, ResourceBundle resourceBundle) {
		super(shell, resourceBundle);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) 
	{
		Composite wrapper = new XComposite(parent, SWT.NONE);
		wrapper.setLayout(new GridLayout(2, false));
		
		Label productIDLabel = new Label(wrapper, SWT.NONE);
		productIDLabel.setText("Product ID");
		productIDText = new Text(wrapper, SWT.BORDER);
		productIDText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		productIDText.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				try {
					Long id = Long.valueOf(productIDText.getText());
					String organisationID = IDGenerator.getOrganisationID();
					ProductID productID = ProductID.create(organisationID, id);
					search(productID);
				} catch (NumberFormatException ex) {
					// do nothing is handeld by modifyListener
				}
			}
		});
		
		productIDText.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e) {
				try {
					Long id = Long.valueOf(productIDText.getText());
					setErrorMessage(null);
				} catch (NumberFormatException ex) {
					setErrorMessage("The entered text must be a number");
				}
			}
		});
		
		orderComp = new OrderListComposite(wrapper, SWT.NONE);		
		return wrapper;
	}
	
	protected void search(final ProductID productID) 
	{
		Job searchJob = new Job("Searching Product"){
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception 
			{
				QueryCollection<AbstractArticleContainerQuery> queryCollection = 
					new QueryCollection<AbstractArticleContainerQuery>(Order.class);
				OrderQuery orderQuery = new OrderQuery();				
				orderQuery.setProductID(productID);
				queryCollection.add(orderQuery);

				final Collection articleContainer = ArticleContainerDAO.sharedInstance().getArticleContainersForQueries(
						queryCollection, new String[] {FetchPlan.DEFAULT, Order.FETCH_GROUP_ARTICLES}, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

				getShell().getDisplay().syncExec(new Runnable(){
					@Override
					public void run() {
						orderComp.setInput(articleContainer);
					}
				});
				return Status.OK_STATUS;
			}
		};
		searchJob.schedule();
	}
	
	public Collection<Article> getSelectedArticles() {
		Order selectedOrder = orderComp.getFirstSelectedElement();
		if (selectedOrder != null) {
			return selectedOrder.getArticles();
		}
		return null;
	}
}
;