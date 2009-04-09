package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.util.ScriptParser;

public class RecurringArticleAdderComposite extends ArticleAdderComposite 
{

	public RecurringArticleAdderComposite(Composite parent, ArticleAdder articleAdder) {
		super(parent, articleAdder);
	}

	@Override
	protected void addArticle()
	{
		ScriptParser script = new ScriptParser(getProductName());
		String err = script.validateContent();
		if(err !=null)
		{
			getProductNameTextBox().setText(err);
		}
		else	
			super.addArticle();
	}
	
	
}
