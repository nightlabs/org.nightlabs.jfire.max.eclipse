package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.store.id.ProductID;
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
		label.setText("Product ID (Organisation ID / Product ID)");
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
		reverseAllButton.setText("Reverse Complete Offer");
		reverseAllButton.setSelection(true);
		reverseArticleButton = new Button(chooseComposite, SWT.RADIO);
		reverseArticleButton.setText("Reverse only Article");		
	}

	public IProductIDParser getProductIDParser() 
	{
		// TODO: should come from extension-point
		return new DefaultProductIDParser();
	}
	
	public boolean isReverseAll() {
		return reverseAllButton.getSelection();
	}

	public boolean isReverseArticle() {
		return reverseArticleButton.getSelection();
	}

	public ProductID getProductID() {
		return getProductIDParser().getProductID(text, new NullProgressMonitor());
	}
		
	public Text getProductIDText() {
		return productIDText;
	}

}
