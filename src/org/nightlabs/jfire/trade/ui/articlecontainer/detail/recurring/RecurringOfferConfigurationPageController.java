package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;


import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.recurring.RecurringOffer;
import org.nightlabs.jfire.trade.recurring.RecurringOfferConfiguration;
import org.nightlabs.jfire.trade.recurring.dao.RecurringOfferDAO;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Fitas Amine <!-- fitas [AT] nightlabs [DOT] de -->
 *
 */
public class RecurringOfferConfigurationPageController extends EntityEditorPageController {


	public static final String[] FETCH_GROUPS_RECURRING_OFFER = {
		RecurringOffer.FETCH_GROUP_RECURRING_OFFER_CONFIGURATION,
		RecurringOfferConfiguration.FETCH_GROUP_CREATOR_TASK
		, FetchPlan.DEFAULT };


	private RecurringOfferConfiguration recurringOfferConfiguration;

	public RecurringOfferConfigurationPageController(EntityEditor editor) {
		super(editor);
	}

	protected OfferID getOfferID() {

		ArticleContainerEditorInput input = (ArticleContainerEditorInput) getEntityEditor().getEditorInput();
		return (OfferID) input.getArticleContainerID();
	}

	@Override
	public void doLoad(ProgressMonitor monitor) {

		monitor.beginTask("Loading Configuration", 100); 
		RecurringOffer recurringOffer = RecurringOfferDAO.sharedInstance().getRecurringOffer(getOfferID(), FETCH_GROUPS_RECURRING_OFFER, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		this.recurringOfferConfiguration = recurringOffer.getRecurringOfferConfiguration(); 
		monitor.done();
		setLoaded(true); 

	}

	@Override
	public boolean doSave(ProgressMonitor monitor) {
		return true;
	}

	public RecurringOfferConfiguration getRecurringOfferConfiguration() {
		return recurringOfferConfiguration;
	}

}
