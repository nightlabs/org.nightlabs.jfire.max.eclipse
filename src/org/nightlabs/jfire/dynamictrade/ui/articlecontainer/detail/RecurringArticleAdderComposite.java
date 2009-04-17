package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
		JSHTMLExecuter script = new JSHTMLExecuter(getProductName());
		String err = script.validateContent();
		if(err !=null)
		{
			// shows the error message !!!
			storedText = getProductNameTextBox().getText();
			getProductNameTextBox().setText(err);
			getProductNameTextBox().setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
			getProductNameTextBox().addFocusListener(  new FocusListener(){
				@Override
				public void focusGained(FocusEvent arg0) {
					getProductNameTextBox().setText(getStoredText());
					getProductNameTextBox().setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
					getProductNameTextBox().removeFocusListener(this);
					
				}				
				@Override
				public void focusLost(FocusEvent arg0) {
						}

			});
			return; 
		}
		else	
			super.addArticle();
	}
	
	
}
