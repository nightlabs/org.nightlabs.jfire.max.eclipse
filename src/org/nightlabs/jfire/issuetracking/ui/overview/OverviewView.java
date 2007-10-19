package org.nightlabs.jfire.issuetracking.ui.overview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * Abstract base {@link ViewPart} that will display an {@link OverviewShelf}
 * with the {@link OverviewRegistry} returned in {@link #getOverviewRegistry()}.
 * 
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class OverviewView 
extends ViewPart 
{
	public OverviewView() {
		super();
	}

	private OverviewShelf overviewShelf;
	@Override
	public void createPartControl(Composite parent) {
		overviewShelf = new OverviewShelf(parent, SWT.NONE) {
			@Override
			protected OverviewRegistry getOverviewRegistry() {
				return OverviewView.this.getOverviewRegistry();
			}
		};
	}

	@Override
	public void setFocus() {
		overviewShelf.setFocus();
	}

	/**
	 * Returns the {@link OverviewRegistry} this Views {@link OverviewShelf}
	 * should be created with.
	 * 
	 * @return The {@link OverviewRegistry} this Views {@link OverviewShelf}
	 * 		should be created with.
	 */
	protected abstract OverviewRegistry getOverviewRegistry();
}
