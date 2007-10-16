package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.Iterator;
import java.util.SortedMap;

import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.nebula.widgets.pshelf.RedmondShelfRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;

/**
 * @author Chairat Kongarayawetchakun chairatk [at] NightLabs [dot] de
 *
 */
public class IssueComposite
extends XComposite
{

	public IssueComposite(Composite parent, int style) {
		super(parent, style);
		createComposite(this);
	}

	public IssueComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
		createComposite(this);
	}

	private PShelf shelf;
	protected void createComposite(Composite parent) 
	{
		parent.setLayout(new FillLayout());

		shelf = new PShelf(parent, SWT.NONE);
		shelf.setRenderer(new RedmondShelfRenderer());			
		shelf.setLayoutData(new GridData(GridData.FILL_BOTH));
		SortedMap<Integer, IssueCategoryFactory> index2Category = 
			IssueOverviewRegistry.sharedInstance().getIndex2Catgeory();
		for (Iterator<Integer> iterator = index2Category.keySet().iterator(); iterator.hasNext();) {
			int index = iterator.next();
			IssueCategoryFactory categoryFactory = (IssueCategoryFactory) index2Category.get(index);  
			PShelfItem categoryItem = new PShelfItem(shelf, SWT.NONE);
			categoryItem.setText(categoryFactory.getName());
			categoryItem.setImage(categoryFactory.getImage());
//			categoryItem.getBody().setLayout(new GridLayout());	    
			categoryItem.getBody().setLayout(new FillLayout());
			// TODO: should use scrollable composite
			IssueCategory category = categoryFactory.createIssueCategory();
			categoryItem.setData(category);
			category.createComposite(categoryItem.getBody());	    	
		}
	}

}
