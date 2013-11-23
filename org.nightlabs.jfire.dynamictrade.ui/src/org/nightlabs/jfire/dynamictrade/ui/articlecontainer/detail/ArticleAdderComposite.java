package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.job.FadeableCompositeJob;
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.accounting.id.TariffID;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.dynamictrade.ui.resource.Messages;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.Unit;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.store.id.UnitID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Segment;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.SegmentID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEdit;
import org.nightlabs.l10n.NumberFormatter;
import org.nightlabs.progress.ProgressMonitor;


public class ArticleAdderComposite
extends ArticleBaseComposite
{
	private ArticleAdder articleAdder;

	private Button addArticle;

	public ArticleAdderComposite(Composite parent, ArticleAdder articleAdder)
	{
		super(parent, articleAdder.getSegmentEdit().getArticleContainer(), (DynamicProductType)articleAdder.getProductType());
		this.articleAdder = articleAdder;
		createUI();
	}

	public ArticleAdderComposite(Composite parent, ArticleAdder articleAdder,boolean isScriptable)
	{
		super(parent, articleAdder.getSegmentEdit().getArticleContainer(), (DynamicProductType)articleAdder.getProductType());
		this.articleAdder = articleAdder;
		setScriptable(isScriptable);
		createUI();
	}


	@Override
	protected void createUI_additionalElements_comp1(Composite parent)
	{
		super.createUI_additionalElements_comp1(parent);

		++comp1.getGridLayout().numColumns;
		addArticle = new Button(comp1, SWT.PUSH);
		addArticle.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		addArticle.setText(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleAdderComposite.addArticleButton.text")); //$NON-NLS-1$
		addArticle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				addArticle();
			}
		});

//		// TODO BEGIN TEST - MOVE TO ArticleBaseComposite!
//		++comp1.getGridLayout().numColumns;
//		Button templateButton = new Button(comp1, SWT.PUSH);
//		templateButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
//		templateButton.setText("T");
//		templateButton.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e)
//			{
//				SelectDynamicProductTemplateDialog dialog = new SelectDynamicProductTemplateDialog(getShell());
//				dialog.open();
//			}
//		});
//		// TODO END TEST
	}

	@Override
	protected void fireCompositeContentChangeEvent()
	{
		articleAdder.getSegmentEdit().fireCompositeContentChangeEvent();
	}

	protected void addArticle()
	{
		try {
			final double qty = NumberFormatter.parseFloat(quantity.getText());

			final TariffID tariffID = (TariffID) JDOHelper.getObjectId(tariffCombo.getSelectedElement());
			if (tariffID == null)
				throw new IllegalStateException("No tariff selected (tariffID is null)!"); //$NON-NLS-1$

			Unit unit = unitCombo.getSelectedElement();
			final UnitID unitID = (UnitID) JDOHelper.getObjectId(unit);
			if (tariffID == null)
				throw new IllegalStateException("No unit selected (unitID is null)!"); //$NON-NLS-1$

			final long quantity = unit.toLong(qty);

			final Price singlePriceOrig = resultPriceConfig.getPriceCell(createPriceCoordinate(), true).getPrice();
			// we must create a new instance (with a new ID), because it would otherwise cause duplicate-key-exceptions when adding multiple articles
//			final Price singlePrice = new Price(resultPriceConfig.getOrganisationID(), resultPriceConfig.getPriceConfigID(), resultPriceConfig.createPriceID(), singlePriceOrig.getCurrency());
			final Price singlePrice = new Price(IDGenerator.getOrganisationID(), IDGenerator.nextID(Price.class), singlePriceOrig.getCurrency());
			singlePrice.sumPrice(singlePriceOrig);


			FadeableCompositeJob addJob = new FadeableCompositeJob(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleAdderComposite.addArticleJob.text"), this, this) { //$NON-NLS-1$
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

					Article article = articleAdder.createArticle(segmentID, offerID, productTypeID, quantity, unitID, tariffID, productName, singlePrice, true, false);
					List<Article> articles = new ArrayList<Article>(1); articles.add(article);
					segmentEdit.getClientArticleSegmentGroupSet().addArticles(articles);
					return Status.OK_STATUS;
				}
			};
			addJob.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
//			addJob.setUser(true);
			addJob.schedule();
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	};
}
