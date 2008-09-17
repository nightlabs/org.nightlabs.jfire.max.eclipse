package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;

import org.eclipse.swt.SWT;
import org.nightlabs.jfire.jbpm.ui.state.CurrentStateComposite;
import org.nightlabs.jfire.jbpm.ui.transition.next.NextTransitionComposite;
import org.nightlabs.jfire.trade.recurring.RecurringOffer;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.HeaderComposite;
import org.eclipse.swt.layout.RowData;

public class RecurringOfferHeaderComposite
extends HeaderComposite{

	private CurrentStateComposite currentStateComposite;
	private NextTransitionComposite nextTransitionComposite;


	private volatile RecurringOffer recurringOffer;

	public RecurringOfferHeaderComposite(ArticleContainerEditComposite articleContainerEditComposite,
			RecurringOffer recurringOffer) {
		super(articleContainerEditComposite, articleContainerEditComposite, recurringOffer);

		this.recurringOffer = recurringOffer;

		currentStateComposite = new CurrentStateComposite(this, SWT.NONE);
		currentStateComposite.setStatable(recurringOffer);
		currentStateComposite.setLayoutData(null);

		nextTransitionComposite = new NextTransitionComposite(this, SWT.NONE);
		nextTransitionComposite.setStatable(recurringOffer);
		nextTransitionComposite.setLayoutData(new RowData(260, SWT.DEFAULT));








	}



}
