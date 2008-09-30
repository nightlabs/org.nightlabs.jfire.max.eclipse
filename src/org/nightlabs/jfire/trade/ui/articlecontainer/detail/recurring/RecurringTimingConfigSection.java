package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.editor.RestorableSectionPart;

public class RecurringTimingConfigSection extends RestorableSectionPart{

	
	private RecurringOfferConfigurationPageController controller;
	
	
	
	
	public RecurringTimingConfigSection(FormPage page, Composite parent, final RecurringOfferConfigurationPageController controller) {
		
		
		super(parent, page.getEditor().getToolkit(), ExpandableComposite.TITLE_BAR);
		
		this.controller = controller;
	
	}


	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);
	}
	

}
