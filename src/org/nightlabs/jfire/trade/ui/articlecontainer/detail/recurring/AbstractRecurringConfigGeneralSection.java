package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jfire.trade.recurring.RecurringOfferConfiguration;



/**
 * @author Fitas Amine <!-- fitas [AT] nightlabs [DOT] de -->
 *
 */
public abstract class AbstractRecurringConfigGeneralSection extends ToolBarSectionPart{


	private RecurringOfferConfigurationPageController controller;
	private XComposite client;
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
				"Title"
		);
		this.controller = controller;
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());
		
		client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 
		
		getSection().setClient(client);
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

	public XComposite getClient() {
		return client;
	}
	
	
}
