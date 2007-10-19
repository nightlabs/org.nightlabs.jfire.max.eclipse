package org.nightlabs.jfire.issuetracking.ui.overview;

import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.nebula.widgets.pshelf.RedmondShelfRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.base.ui.overview.Category;

/**
 * The {@link OverviewShelf} displays categories and entry registered to 
 * {@link OverviewRegistry} or one of its subclasses (different extension-point) in
 * a {@link PShelf}. 
 * <p>
 * The overridable method {@link #getOverviewRegistry()} defines the registry
 * to use to build up the shelf and it entries. As a default the {@link OverviewRegistry}
 * itself will be used (extension-point: org.nightlabs.jfire.base.ui.overview)
 * </p>
 * 
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public abstract class IssueOverviewShelf 
extends XComposite 
{
	/**
	 * Create a new {@link OverviewShelf}
	 * 
	 * @param parent The shelfs parent {@link Composite}.
	 * @param style The style of the shelfs wrapping {@link XComposite} 
	 * @param layoutMode The {@link LayoutMode} of the shelfs wrapping {@link XComposite}
	 * @param layoutDataMode The {@link LayoutDataMode} of the shelfs wrapping {@link XComposite}
	 */
	public IssueOverviewShelf(Composite parent, int style, LayoutMode layoutMode,
			LayoutDataMode layoutDataMode) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		createComposite(this);
	}

	/**
	 * Create a new {@link OverviewShelf}
	 * 
	 * @param parent The shelfs parent {@link Composite}.
	 * @param style The style of the shelfs wrapping {@link XComposite} 
	 */
	public IssueOverviewShelf(Composite parent, int style) {
		super(parent, style);
		createComposite(this);
	}

	private PShelf shelf;
	
	/**
	 * Creates the contents of this shelf.
	 * Note that it applies a {@link FillLayout} to the 
	 * given Parent.
	 * <p>
	 * Called from the constructor with <code>this</code>.
	 * </p>
	 * @param parent The parent (like <code>this</code>).
	 */
	protected void createComposite(Composite parent) 
	{
		parent.setLayout(new FillLayout());

		shelf = new PShelf(parent, SWT.NONE);
		shelf.setRenderer(new RedmondShelfRenderer());
		shelf.setLayoutData(new GridData(GridData.FILL_BOTH));

		for (Category category: getIssueOverviewRegistry().createCategories()) {
			PShelfItem categoryItem = new PShelfItem(shelf,SWT.NONE);
			categoryItem.setData(category);
			categoryItem.setText(category.getCategoryFactory().getName());
			categoryItem.setImage(category.getCategoryFactory().getImage());
			categoryItem.getBody().setLayout(new FillLayout());
			category.createComposite(categoryItem.getBody());
		}
	}
	
	/**
	 * Callback method to define the {@link OverviewRegistry} that should be used
	 * by this shelf.
	 */
	protected abstract IssueOverviewRegistry getIssueOverviewRegistry();
}
