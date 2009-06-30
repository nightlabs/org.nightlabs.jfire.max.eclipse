package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.deliverydate;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.deliverydate.ArticleDeliveryDateCarrier;
import org.nightlabs.jfire.trade.deliverydate.DeliveryDateMode;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class EditDeliveryDateDialog
extends ResizableTitleAreaDialog
{
	private EditDeliveryDateComposite deliveryDateComposite;
	private Collection<Article> articles;
	private DeliveryDateMode mode;

	public EditDeliveryDateDialog(Shell shell, Collection<Article> articles, DeliveryDateMode mode) {
		super(shell, null);
		this.articles = articles;
		this.mode = mode;
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		getShell().setText("Edit Delivery Date");
		setTitle("Edit Delivery Date");
		setMessage("Edit the delivery date of the articles. Either set the date for all articles,\nor click into the column to edit the delivery date for each article individually");

		deliveryDateComposite = new EditDeliveryDateComposite(parent, SWT.NONE, mode);
		deliveryDateComposite.setArticles(articles, mode);
		return deliveryDateComposite;
	}

	@Override
	protected void okPressed()
	{
		Job job = new Job("Assign Delivery Dates") {
			/* (non-Javadoc)
			 * @see org.nightlabs.base.ui.job.Job#run(org.nightlabs.progress.ProgressMonitor)
			 */
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				monitor.beginTask("Assign Delivery Dates", 200);
				TradeManagerRemote tm = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class,
						Login.getLogin().getInitialContextProperties());
				Collection<ArticleDeliveryDateCarrier> articleDeliveryDateCarriers =
					deliveryDateComposite.getArticleDeliveryDateCarriers();
				tm.assignDeliveryDate(articleDeliveryDateCarriers,
						false, new String[] {FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.schedule();
		super.okPressed();
	}

}
