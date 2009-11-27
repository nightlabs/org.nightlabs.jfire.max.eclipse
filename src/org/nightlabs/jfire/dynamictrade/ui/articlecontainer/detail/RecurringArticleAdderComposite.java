package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.message.MessageType;
import org.nightlabs.script.JSHTMLExecuter;

public class RecurringArticleAdderComposite extends ArticleAdderComposite
{

	private String storedText;

	public String getStoredText() {
		return storedText;
	}


 	public RecurringArticleAdderComposite(Composite parent, ArticleAdder articleAdder) {
		super(parent, articleAdder,true);
	}

	@Override
	protected void addArticle()
	{

		JSHTMLExecuter scripteExec = new JSHTMLExecuter(getProductName());
		String err = scripteExec.validateContent();
		if(err !=null)
		{
			// shows the error message !!!
			showTextNameMessage(err,MessageType.ERROR);
			return;
		}
		else
			super.addArticle();
	}


}
