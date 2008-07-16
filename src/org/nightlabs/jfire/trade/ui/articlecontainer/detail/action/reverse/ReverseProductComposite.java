package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.store.id.ProductID;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ReverseProductComposite 
extends XComposite 
{
	private Button reverseAllButton;
	private Button reverseArticleButton;
	private Text productIDText;
	private String text;
	private boolean reverseAll;
	private boolean reverseArticle;
	private IProductIDParser productIDParser;
	
	/**
	 * @param parent
	 * @param style
	 */
	public ReverseProductComposite(Composite parent, int style) {
		super(parent, style);
		
		Composite wrapper = new XComposite(parent, SWT.NONE);
		
		Composite searchWrapper = new XComposite(wrapper, SWT.NONE);
		searchWrapper.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label label = new Label(searchWrapper, SWT.NONE);
		label.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.ReverseProductComposite.label")); //$NON-NLS-1$
		productIDText = new Text(searchWrapper, SWT.BORDER);
		productIDText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		productIDText.setFocus();
		productIDText.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e) {
				text = productIDText.getText();
			}
		});
	
		Composite chooseComposite = new XComposite(wrapper, SWT.NONE);				
		reverseAllButton = new Button(chooseComposite, SWT.RADIO);
		reverseAllButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.ReverseProductComposite.button.reverseAll.text")); //$NON-NLS-1$
		reverseAllButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				reverseAll = true;
				reverseArticle = false;
			}
		});
		reverseArticleButton = new Button(chooseComposite, SWT.RADIO);
		reverseArticleButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.ReverseProductComposite.button.reverseOnlyArticle.text"));		 //$NON-NLS-1$
		reverseArticleButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				reverseAll = false;
				reverseArticle = true;
			}
		});
		
		reverseAllButton.setSelection(true);
		reverseAll = true;
		reverseArticle = false;
	}

	public IProductIDParser getProductIDParser() 
	{
		if (productIDParser == null) {
			productIDParser = ProductIDParserRegistry.sharedInstance().getProductIDParser().iterator().next();			
		}
		return productIDParser;
	}

	public boolean isReverseAll() {
		return reverseAll;
	}

	public boolean isReverseArticle() {
		return reverseArticle;
	}

	public ProductID getProductID() {
		return getProductIDParser().getProductID(text, new NullProgressMonitor());
	}

	public Text getProductIDText() {
		return productIDText;
	}

}
