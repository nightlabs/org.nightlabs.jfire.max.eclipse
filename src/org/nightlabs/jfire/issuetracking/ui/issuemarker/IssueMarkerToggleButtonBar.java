package org.nightlabs.jfire.issuetracking.ui.issuemarker;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.issuemarker.IssueMarker;
import org.nightlabs.jfire.issue.issuemarker.IssueMarkerDAO;
import org.nightlabs.jfire.issue.issuemarker.id.IssueMarkerID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.NLLocale;

/**
 * @author Marco หงุ่ยตระกูล-Schulze - marco at nightlabs dot de
 */
public class IssueMarkerToggleButtonBar
extends Composite
implements ISelectionProvider
{
	private static final String[] FETCH_GROUPS_ISSUE_MARKER = {
			FetchPlan.DEFAULT,
			IssueMarker.FETCH_GROUP_NAME,
			IssueMarker.FETCH_GROUP_DESCRIPTION,
			IssueMarker.FETCH_GROUP_ICON_16X16_DATA
	};

	private final Display display;

	private int flags;
	private boolean useGroup = true;
	private Group group;
	private ScrolledComposite scrolledComposite;
	private Composite scrolledCompositeContent;
	private Set<IssueMarkerID> selectedIssueMarkerIDs = Collections.emptySet();

	private List<IssueMarker> issueMarkers;
	private Map<IssueMarkerID, IssueMarker> issueMarkerID2issueMarker;
	private Map<IssueMarker, Image> issueMarker2image;
	private Map<Button, IssueMarker> button2issueMarker;
	private Map<IssueMarker, Button> issueMarker2button;

	private void createGroup()
	{
		if (group != null && !group.isDisposed())
			group.dispose();

		group = null;

		if (useGroup) {
			group = new Group(this, SWT.NONE + SWT.BORDER); // With or without border, at least on Linux, it looks the same ;-) Marco.
			group.setLayout(new FillLayout());
			group.setText("Markers");
		}
	}

	/**
	 * Convienience constructor calling {@link #IssueMarkerToggleButtonBar(Composite, int)} with <code>flags = SWT.H_SCROLL</code>.
	 * @param parent the container-UI-element.
	 */
	public IssueMarkerToggleButtonBar(Composite parent) {
		this(parent, SWT.H_SCROLL);
	}

	public void setUseGroup(boolean useGroup) {
		this.useGroup = useGroup;
	}
	public boolean isUseGroup() {
		return useGroup;
	}

	protected boolean isVerticalLayout()
	{
		return (SWT.V_SCROLL & flags) != 0;
	}

	/**
	 * Create a new UI element for selecting (toggling) {@link IssueMarker}s.
	 *
	 * @param parent the container-UI-element.
	 * @param flags Can be {@link SWT#H_SCROLL} for horizontal layout or {@link SWT#V_SCROLL} for vertical layout.
	 */
	public IssueMarkerToggleButtonBar(Composite parent, int flags) {
		super(parent, SWT.NONE);
		this.flags = flags;

		if (isVerticalLayout())
			this.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		else
			this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.setLayout(new FillLayout());

		display = getDisplay();

		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				disposeChildElements();
			}
		});

		Label loadingMessage = new Label(group == null ? this : group, SWT.NONE);
		loadingMessage.setText("Loading...");

		Job job = new Job("Loading issue markers") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				final List<IssueMarker> _issueMarkers = IssueMarkerDAO.sharedInstance().getIssueMarkers(
						FETCH_GROUPS_ISSUE_MARKER, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor
				);

				final Map<IssueMarkerID, IssueMarker> _issueMarkerID2issueMarker = new HashMap<IssueMarkerID, IssueMarker>();
				for (IssueMarker issueMarker : _issueMarkers) {
					IssueMarkerID issueMarkerID = (IssueMarkerID) JDOHelper.getObjectId(issueMarker);
					if (issueMarkerID == null)
						throw new IllegalStateException("JDOHelper.getObjectId(issueMarker) returned null for: " + issueMarker);

					_issueMarkerID2issueMarker.put(issueMarkerID, issueMarker);
				}

				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						issueMarkers = _issueMarkers;
						issueMarkerID2issueMarker = _issueMarkerID2issueMarker;
						createToggleButtons();
					}
				});

				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();
	}

	private void disposeChildElements()
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Thread mismatch! This method must be called on the SWT UI thread!");

		if (issueMarker2button != null) {
			for (Map.Entry<IssueMarker, Button> me : issueMarker2button.entrySet()) {
				me.getValue().dispose();
			}
			issueMarker2button = null;
		}

		if (issueMarker2image != null) {
			for (Map.Entry<IssueMarker, Image> me : issueMarker2image.entrySet()) {
				me.getValue().dispose();
			}
			issueMarker2image = null;
		}

		if (!isDisposed()) {
			for (Control c : getChildren())
				c.dispose();
		}

		scrolledCompositeContent = null; // already disposed by the getChildren iteration before
		scrolledComposite = null; // already disposed by the getChildren iteration before
		button2issueMarker = null;
	}

	private void createToggleButtons()
	{
		disposeChildElements();

		final Locale locale = NLLocale.getDefault();

		createGroup();
		int scrolledCompFlags = flags & (SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite = new ScrolledComposite(group == null ? this : group, scrolledCompFlags); // | SWT.BORDER); // with or without border - not sure what looks better (especially I have no idea how it looks on Windows). Marco.
		scrolledCompositeContent = new Composite(scrolledComposite, SWT.NONE);

		scrolledCompositeContent.setLayout(new GridLayout(1, true));
		scrolledComposite.setContent(scrolledCompositeContent);
		issueMarker2button = new HashMap<IssueMarker, Button>();
		button2issueMarker = new HashMap<Button, IssueMarker>();
		issueMarker2image = new HashMap<IssueMarker, Image>();
		for (IssueMarker issueMarker : issueMarkers) {
			Image image = null;
			if (issueMarker.getIcon16x16Data() != null) {
				ImageData imageData = new ImageData(new ByteArrayInputStream(issueMarker.getIcon16x16Data())); // No need to close this stream, because it is purely in-memory.
				image = new Image(display, imageData);
				issueMarker2image.put(issueMarker, image);
			}

			Button button = new Button(scrolledCompositeContent, SWT.TOGGLE);
			if (image != null)
				button.setImage(image);
			else
				button.setText(issueMarker.getName().getText(locale));

			button.setToolTipText(
					String.format("*** %s ***\n%s", issueMarker.getName().getText(locale), issueMarker.getDescription().getText(locale))
			);
			button.addSelectionListener(toggleButtonSelectionListener);
			issueMarker2button.put(issueMarker, button);
			button2issueMarker.put(button, issueMarker);
		}

		if (isVerticalLayout())
			((GridLayout)scrolledCompositeContent.getLayout()).numColumns = 1;
		else
			((GridLayout)scrolledCompositeContent.getLayout()).numColumns = Math.max(1, issueMarker2button.size());

		scrolledCompositeContent.setSize(scrolledCompositeContent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		getParent().layout(true, true); // the height of this whole composite might change depending on the size of the buttons.

		updateUI();
	}

	private SelectionListener toggleButtonSelectionListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
			IssueMarker issueMarker = button2issueMarker.get(event.getSource());
			if (issueMarker == null)
				throw new IllegalStateException("No IssueMarker registered for this event source: " + event.getSource());

			IssueMarkerID issueMarkerID = (IssueMarkerID) JDOHelper.getObjectId(issueMarker);
			if (issueMarkerID == null)
				throw new IllegalStateException("JDOHelper.getObjectId(issueMarker) returned null for: " + issueMarker);


			Set<IssueMarkerID> tmpIDs = new HashSet<IssueMarkerID>(selectedIssueMarkerIDs);
			Button button = (Button) event.getSource();
			if (button.getSelection())
				tmpIDs.add(issueMarkerID);
			else
				tmpIDs.remove(issueMarkerID);

			selectedIssueMarkerIDs = Collections.unmodifiableSet(tmpIDs);
			fireSelectionChangedListeners();
		}
	};

	private ListenerList selectionChangedListeners = new ListenerList();

	/**
	 * Get the selection, i.e. the OIDs of those {@link IssueMarker}s that were toggled into state "selected" by the user
	 * (or by a call to {@link #setSelection(ISelection)} / {@link #setSelectedIssueMarkerIDs(Collection)}).
	 * <p>
	 * This method never returns <code>null</code>.
	 * </p>
	 *
	 * @return OID-representations of the selected {@link IssueMarker}s - never <code>null</code>.
	 */
	public Set<IssueMarkerID> getSelectedIssueMarkerIDs() {
		return selectedIssueMarkerIDs; // is already unmodifiable.
	}

	/**
	 * Get the selected {@link IssueMarker} instances or <code>null</code> before the data is loaded.
	 * This is a convenience method to convert the IDs to instances of {@link IssueMarker}.
	 *
	 * @return <code>null</code> or the selected {@link IssueMarker}s.
	 * @see #getSelectedIssueMarkerIDs()
	 */
	public Set<IssueMarker> getSelectedIssueMarkers() {
		Set<IssueMarkerID> ids = selectedIssueMarkerIDs;
		Map<IssueMarkerID, IssueMarker> map = issueMarkerID2issueMarker;
		if (map == null)
			return null;

		Set<IssueMarker> result = new HashSet<IssueMarker>(ids.size());
		for (IssueMarkerID issueMarkerID : ids) {
			IssueMarker issueMarker = map.get(issueMarkerID);
			if (issueMarker == null)
				throw new IllegalStateException("issueMarkerID2issueMarker.get(issueMarkerID) returned null for " + issueMarkerID);

			result.add(issueMarker);
		}
		return Collections.unmodifiableSet(result);
	}

	/**
	 * Convenience method which implicitely converts from {@link IssueMarker}s to their OIDs and
	 * then calls {@link #setSelectedIssueMarkerIDs(Collection)}.
	 *
	 * @param selectedIssueMarkers the currently selected {@link IssueMarker}s.
	 * @see #setSelectedIssueMarkerIDs(Collection)
	 */
	public void setSelectedIssueMarkers(Collection<? extends IssueMarker> selectedIssueMarkers) {
		List<IssueMarkerID> ids = null;
		if (selectedIssueMarkers != null)
			ids = NLJDOHelper.getObjectIDList(selectedIssueMarkers);

		setSelectedIssueMarkerIDs(ids);
	}

	/**
	 * Set the currently selected {@link IssueMarker}s via their object-ids (instances of {@link IssueMarkerID}).
	 *
	 * @param selectedIssueMarkerIDs the OIDs of the new selection.
	 */
	public void setSelectedIssueMarkerIDs(Collection<IssueMarkerID> selectedIssueMarkerIDs) {
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Thread mismatch! This method must be called on the SWT UI thread!");

		if (selectedIssueMarkerIDs == null || selectedIssueMarkerIDs.isEmpty())
			this.selectedIssueMarkerIDs = Collections.emptySet();
		else
			this.selectedIssueMarkerIDs = Collections.unmodifiableSet(new HashSet<IssueMarkerID>(selectedIssueMarkerIDs));

		updateUI();
		fireSelectionChangedListeners();
	}

	protected void updateUI()
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Thread mismatch! This method must be called on the SWT UI thread!");

		if (issueMarker2button != null) {
			for (Map.Entry<IssueMarker, Button> me : issueMarker2button.entrySet()) {
				IssueMarkerID issueMarkerID = (IssueMarkerID) JDOHelper.getObjectId(me.getKey());
				me.getValue().setSelection(selectedIssueMarkerIDs.contains(issueMarkerID));
			}
		}
	}

	protected void fireSelectionChangedListeners()
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Thread mismatch! This method must be called on the SWT UI thread!");

		Object[] listeners = selectionChangedListeners.getListeners();
		if (listeners.length == 0)
			return;

		SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());
		for (Object l : listeners) {
			((ISelectionChangedListener)l).selectionChanged(event);
		}
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The implementation in {@link IssueMarkerToggleButtonBar} returns an instance of {@link IStructuredSelection}
	 * containing instances of {@link IssueMarkerID}.
	 * </p>
	 * @see #setSelection(ISelection)
	 * @see #getSelectedIssueMarkerIDs()
	 * @see #getSelectedIssueMarkers()
	 */
	@Override
	public ISelection getSelection() {
		return new StructuredSelection(getSelectedIssueMarkerIDs().toArray());
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The implementation in {@link IssueMarkerToggleButtonBar} accepts only instances of {@link IStructuredSelection}
	 * that contain {@link IssueMarker}s or {@link IssueMarkerID}s (they can be mixed).
	 * </p>
	 * @see #getSelection()
	 * @see #setSelectedIssueMarkerIDs(Collection)
	 * @see #setSelectedIssueMarkers(Collection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		IStructuredSelection sel = (IStructuredSelection) selection;
		Set<IssueMarkerID> issueMarkerIDs = new HashSet<IssueMarkerID>();
		Set<IssueMarker> issueMarkers = new HashSet<IssueMarker>();
		for (Iterator<?> it = sel.iterator(); it.hasNext(); ) {
			Object o = it.next();
			if (o == null)
				; // silently ignore
			else if (o instanceof IssueMarkerID)
				issueMarkerIDs.add((IssueMarkerID) o);
			else if (o instanceof IssueMarker)
				issueMarkers.add((IssueMarker) o);
			else
				throw new IllegalArgumentException("Selection contains element of type \"" + o.getClass().getName() + "\" which is not supported! Only IssueMarkerID and IssueMarker are accepted! Element: " + o);
		}

		if (!issueMarkers.isEmpty()) {
			List<IssueMarkerID> ids = NLJDOHelper.getObjectIDList(issueMarkers);
			issueMarkerIDs.addAll(ids);
		}

		setSelectedIssueMarkerIDs(issueMarkerIDs);
	}

	/**
	 * Apply the current selection to an issue. This is probably the most common use case for this
	 * UI element.
	 *
	 * @param issue the target of the copy operation, which must not be <code>null</code>.
	 */
	public void commitToIssue(Issue issue)
	{
		if (issue == null)
			throw new IllegalArgumentException("issue == null");

		Set<IssueMarker> selectedIssueMarkers = this.getSelectedIssueMarkers();
		// If the selectedIssueMarkers are null, it means the data was not yet loaded.
		// If we commit now, we can be sure that the user did not make any change, because he did not see any UI, yet.
		// Thus, we silently leave.
		if (selectedIssueMarkers == null)
			return;

		// First, we remove all those IssueMarkers from the Issue that are no longer selected.
		for (IssueMarker issueMarker : new ArrayList<IssueMarker>(issue.getIssueMarkers())) {
			if (!selectedIssueMarkers.contains(issueMarker))
				issue.removeIssueMarker(issueMarker);
		}

		// Then we add all currently selected ones (this should only modify the set - hopefully - if it is not yet an element).
		for (IssueMarker issueMarker : selectedIssueMarkers)
			issue.addIssueMarker(issueMarker);
	}

//	/**
//	 * Get the {@link IssueMarker} instances mapped by their OIDs or <code>null</code> before the data was loaded.
//	 *
//	 * @return <code>null</code> or a <code>Map</code> containing all {@link IssueMarker}s mapped by their OIDs.
//	 */
//	public Map<IssueMarkerID, IssueMarker> getIssueMarkerID2issueMarker() {
//		Map<IssueMarkerID, IssueMarker> issueMarkerID2issueMarker = this.issueMarkerID2issueMarker;
//		if (issueMarkerID2issueMarker == null)
//			return null;
//		else
//			return Collections.unmodifiableMap(issueMarkerID2issueMarker);
//	}
}
