package org.nightlabs.jfire.voucher.articlecontainer.detail;

import java.util.Collection;
import java.util.Locale;

import javax.jdo.FetchPlan;
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
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.FetchGroupsTrade;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Segment;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.SegmentID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEditFactory;
import org.nightlabs.jfire.voucher.VoucherManager;
import org.nightlabs.jfire.voucher.VoucherManagerUtil;
import org.nightlabs.jfire.voucher.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

public class ArticleAdderComposite
		extends FadeableComposite
{
	private ArticleAdder articleAdder;

	private Label productTypeNameLabel;
	private QuantitySelector quantitySelector;

	public ArticleAdderComposite(Composite parent, ArticleAdder articleAdder)
	{
		super(parent, SWT.NONE);
		this.articleAdder = articleAdder;
		
		productTypeNameLabel = new Label(this, SWT.NONE);
		productTypeNameLabel.setLayoutData(new GridData());
		productTypeNameLabel.setText(
				articleAdder.getProductType().getName().getText(Locale.getDefault().getLanguage()));

		quantitySelector = new QuantitySelector(this) {
			protected void quantitySelected(int qty)
			{
				qtySelected(qty);
			}
			protected void relayout()
			{
				ArticleAdderComposite.this.layout(true, true);
			}
		};

		this.getGridLayout().numColumns = this.getChildren().length;
//		((GridData)productTypeNameLabel.getLayoutData()).horizontalSpan = this.getGridLayout().numColumns;
	}

	private void qtySelected(final int qty)
	{
			Job addJob = new FadeableCompositeJob(Messages.getString("org.nightlabs.jfire.voucher.articlecontainer.detail.ArticleAdderComposite.addJob.name"), this, this) { //$NON-NLS-1$

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

					String segmentContext = articleAdder.getSegmentEdit().getSegmentContext();
					String fetchGroupTrade_article;
					if (SegmentEditFactory.SEGMENTCONTEXT_ORDER.equals(segmentContext)) {
						fetchGroupTrade_article = FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_ORDER_EDITOR;
					}
					else if (SegmentEditFactory.SEGMENTCONTEXT_OFFER.equals(segmentContext)) {
						fetchGroupTrade_article = FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_OFFER_EDITOR;
					}
					else
						throw new IllegalStateException("Why is this ArticleAdder in an unknown segment context? segmentContext=" + segmentContext); //$NON-NLS-1$

					VoucherManager vm = VoucherManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();

					Collection<Article> articles = vm.createArticles(
								segmentID, offerID, productTypeID, qty,
								new String[] {
										fetchGroupTrade_article,
										FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

					segmentEdit.getClientArticleSegmentGroups().addArticles(articles);
					return Status.OK_STATUS;
				}
			};
			addJob.setPriority(Job.SHORT);
//			addJob.setUser(true);
			addJob.schedule();
	};
}
