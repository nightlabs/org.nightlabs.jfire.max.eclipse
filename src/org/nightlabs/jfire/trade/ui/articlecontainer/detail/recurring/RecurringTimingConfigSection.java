package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.editor.RestorableSectionPart;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;

public class RecurringTimingConfigSection extends ToolBarSectionPart{

	
	private RecurringOfferConfigurationPageController controller;
	
	
	
	
	public RecurringTimingConfigSection(FormPage page, Composite parent, final RecurringOfferConfigurationPageController controller) {
		
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR,
		"Timer");
		
		this.controller = controller;
	
	}


	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);
	}
	

}
