package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;


import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
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
public class RecurringOfferConfigurationPageController extends  ActiveEntityEditorPageController<RecurringOfferConfiguration> {


	public static final String[] FETCH_GROUPS_RECURRING_OFFER = {
		RecurringOffer.FETCH_GROUP_RECURRING_OFFER_CONFIGURATION,
		RecurringOfferConfiguration.FETCH_GROUP_CREATOR_TASK
		, FetchPlan.DEFAULT };


	public RecurringOfferConfigurationPageController(EntityEditor editor) {
		super(editor);
	}

	protected OfferID getOfferID() {

		ArticleContainerEditorInput input = (ArticleContainerEditorInput) getEntityEditor().getEditorInput();
		return (OfferID) input.getArticleContainerID();
	}


	@Override
	protected String[] getEntityFetchGroups() {
		return FETCH_GROUPS_RECURRING_OFFER;
	}

	@Override
	protected RecurringOfferConfiguration retrieveEntity(ProgressMonitor monitor) {

		RecurringOffer recurringOffer = RecurringOfferDAO.sharedInstance().getRecurringOffer(getOfferID(), getEntityFetchGroups(), getEntityMaxFetchDepth(), monitor);
		return recurringOffer.getRecurringOfferConfiguration(); 
				
	}

	@Override
	protected RecurringOfferConfiguration storeEntity(
			RecurringOfferConfiguration controllerObject,
			ProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

}
