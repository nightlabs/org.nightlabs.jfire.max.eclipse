package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jfire.trade.recurring.RecurringOfferConfiguration;
import org.nightlabs.jfire.trade.ui.resource.Messages;



/**
 * @author Fitas Amine <!-- fitas [AT] nightlabs [DOT] de -->
 *
 */
public abstract class AbstractRecurringConfigGeneralSection extends ToolBarSectionPart {

	private RecurringOfferConfigurationPageController controller;
	private RecurringOfferConfiguration recurringOfferConfiguration;
	
	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public AbstractRecurringConfigGeneralSection(FormPage page, Composite parent, RecurringOfferConfigurationPageController controller) {
		super(
				page, parent, 
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR,
				Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.AbstractRecurringConfigGeneralSection.title") //$NON-NLS-1$
		);
		this.controller = controller;
	}
	
	
	public void setRecurringOfferConfiguration(RecurringOfferConfiguration recurringOfferConfiguration) {
		this.recurringOfferConfiguration = recurringOfferConfiguration;
		updateConfigOffer(recurringOfferConfiguration);
	}
	
	public RecurringOfferConfiguration getRecurringOfferConfiguration() {
		return this.recurringOfferConfiguration;
	}
	
	
	protected abstract void updateConfigOffer(RecurringOfferConfiguration recurringOfferConfiguration);
	
	
	public RecurringOfferConfigurationPageController getController() {
		return controller;
	}
}
