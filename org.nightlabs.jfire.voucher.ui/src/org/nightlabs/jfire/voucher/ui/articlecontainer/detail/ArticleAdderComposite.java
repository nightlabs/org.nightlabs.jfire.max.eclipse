package org.nightlabs.jfire.voucher.ui.articlecontainer.detail;

import java.util.Collection;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.FadeableComposite;
import org.nightlabs.base.ui.composite.QuantitySelector;
import org.nightlabs.base.ui.job.FadeableCompositeJob;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Segment;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.SegmentID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEdit;
import org.nightlabs.jfire.voucher.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.NLLocale;

public class ArticleAdderComposite
		extends FadeableComposite
{
	private ArticleAdder articleAdder;

	private Label productTypeNameLabel;
	@SuppressWarnings("unused")
	private QuantitySelector quantitySelector;

	public ArticleAdderComposite(Composite parent, ArticleAdder articleAdder)
	{
		super(parent, SWT.NONE);
		this.articleAdder = articleAdder;

		productTypeNameLabel = new Label(this, SWT.NONE);
		productTypeNameLabel.setLayoutData(new GridData());
		productTypeNameLabel.setText(
				articleAdder.getProductType().getName().getText(NLLocale.getDefault().getLanguage()));

		quantitySelector = new QuantitySelector(this) {
			@Override
			protected void quantitySelected(int qty)
			{
				qtySelected(qty);
			}
			@Override
			protected void relayout()
			{
				ArticleAdderComposite.this.layout(true, true);
			}
		};

		this.getGridLayout().numColumns = this.getChildren().length;
		((GridData)productTypeNameLabel.getLayoutData()).horizontalSpan = this.getGridLayout().numColumns;
	}


	private void qtySelected(final int qty)
	{
			Job addJob = new FadeableCompositeJob(Messages.getString("org.nightlabs.jfire.voucher.ui.articlecontainer.detail.ArticleAdderComposite.addJob.name"), this, this) { //$NON-NLS-1$

				@Override
				protected IStatus run(ProgressMonitor monitor, Object source) throws Exception {
					SegmentEdit segmentEdit = articleAdder.getSegmentEdit();

					OfferID offerID = null;
					if (segmentEdit.getArticleContainer() instanceof Offer)
						offerID = (OfferID) JDOHelper.getObjectId(segmentEdit.getArticleContainer());

					Segment segment = segmentEdit.getArticleSegmentGroup().getSegment();
					SegmentID segmentID = (SegmentID) JDOHelper.getObjectId(segment);
					ProductType productType = articleAdder.getProductType();
					ProductTypeID productTypeID = (ProductTypeID) JDOHelper.getObjectId(productType);
					
					Collection<Article> articles = articleAdder.createArticles(segmentID, offerID, productTypeID, qty);

					segmentEdit.getClientArticleSegmentGroupSet().addArticles(articles);
					return Status.OK_STATUS;
				}
			};
			addJob.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
//			addJob.setUser(true);
			addJob.schedule();
	};
}
