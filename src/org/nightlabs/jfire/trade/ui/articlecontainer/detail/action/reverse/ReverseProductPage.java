package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
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
 *
 */
public class ReverseProductPage 
extends DynamicPathWizardPage
{
	private static final Logger logger = Logger.getLogger(ReverseProductPage.class);
	
	private Text productIDText;
	private OrderListComposite orderComp;
	private Button searchButton;
	
	public ReverseProductPage() {
		super(ReverseProductPage.class.getName(), "Reverse Product");
		setDescription("Enter the productID to search for the corresponding order to reverse");
	}

	@Override
	public Control createPageContents(Composite parent) {
		Composite wrapper = new XComposite(parent, SWT.NONE);
		
		Composite searchWrapper = new XComposite(wrapper, SWT.NONE);
		searchWrapper.setLayout(new GridLayout(3, false));
		searchWrapper.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label productIDLabel = new Label(searchWrapper, SWT.NONE);
		productIDLabel.setText("Product ID");
		productIDText = new Text(searchWrapper, SWT.BORDER);
		productIDText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
		productIDText.addSelectionListener(selectionListener);

		searchButton = new Button(searchWrapper, SWT.NONE);
		searchButton.setText("&Search");		
		searchButton.addSelectionListener(selectionListener);
				
		orderComp = new OrderListComposite(wrapper, SWT.NONE);
		orderComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
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

				final Collection articleContainers = ArticleContainerDAO.sharedInstance().getArticleContainersForQueries(
						queryCollection, new String[] {FetchPlan.DEFAULT, Order.FETCH_GROUP_ARTICLES}, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

				getShell().getDisplay().syncExec(new Runnable(){
					@Override
					public void run() {
						orderComp.setInput(articleContainers);
					}
				});
				return Status.OK_STATUS;
			}
		};
		searchJob.schedule();
	}	
	
	private SelectionListener selectionListener = new SelectionAdapter() {		
		@Override
		public void widgetSelected(SelectionEvent e) 
		{
			try {
				Long id = Long.valueOf(productIDText.getText());
				String organisationID = IDGenerator.getOrganisationID();
				ProductID productID = ProductID.create(organisationID, id);
				search(productID);
			} catch (NumberFormatException ex) {
				// do nothing if string is not long
			}
		}
	};
	
	public Collection<Article> getSelectedArticles() {
		Order selectedOrder = orderComp.getFirstSelectedElement();
		if (selectedOrder != null) {
			return selectedOrder.getArticles();
		}
		return null;
	}

	@Override
	public void onShow() {
		super.onShow();
		getShell().setDefaultButton(searchButton);
	}
	
}
