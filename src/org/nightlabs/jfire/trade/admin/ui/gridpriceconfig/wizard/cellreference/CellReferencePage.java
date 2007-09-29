package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.PriceConfigComposite;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

public class CellReferencePage extends AbstractCellReferencePage{
	
	private List<Composite> pageCompositeList = new ArrayList<Composite>();
	
	private PriceConfigComposite priceConfigComposite = null;
	
	public CellReferencePage(SourceViewer sourceViewer, PriceConfigComposite priceConfigComposite) {
		super(sourceViewer, priceConfigComposite);
		this.priceConfigComposite = priceConfigComposite;
		
		scriptBufferOutline
				.append(CellReferenceWizard.NEWLINE)
				.append("cell.resolvePriceCellsAmount") //$NON-NLS-1$
				.append(CellReferenceWizard.L_BRACKET)
				.append(CellReferenceWizard.NEWLINE)
				.append(CellReferenceWizard.TAB).append("new Array") //$NON-NLS-1$
				.append(CellReferenceWizard.L_BRACKET)
				.append(CellReferenceWizard.NEWLINE)
				.append(CellReferenceWizard.TAB).append(CellReferenceWizard.TAB)//.append("CurrencyID.create")
				.append("%s") //$NON-NLS-1$
				.append(CellReferenceWizard.NEWLINE).append(CellReferenceWizard.TAB).append(CellReferenceWizard.R_BRACKET).append(CellReferenceWizard.NEWLINE)
				.append(CellReferenceWizard.R_BRACKET);
	}

	@Override
	protected List<Composite> createDimensionTabItems(TabFolder tabFolder) {
		TabItem customerGroupTabItem = new TabItem(tabFolder, SWT.NONE);
		customerGroupTabItem.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference.CellReferencePage.customerGroupTabItem.text")); //$NON-NLS-1$
		CustomerGroupComposite cgc = new CustomerGroupComposite(this, tabFolder);
		customerGroupTabItem.setControl(cgc);
		pageCompositeList.add(cgc);

		TabItem tariffTabItem = new TabItem(tabFolder, SWT.NONE);
		tariffTabItem.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference.CellReferencePage.tariffTabItem.text")); //$NON-NLS-1$
		TariffComposite tc = new TariffComposite(this, tabFolder);
		tariffTabItem.setControl(tc);
		pageCompositeList.add(tc);

		TabItem currencyTabItem = new TabItem(tabFolder, SWT.NONE);
		currencyTabItem.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference.CellReferencePage.currencyTabItem.text")); //$NON-NLS-1$
		CurrencyComposite cc = new CurrencyComposite(this, tabFolder);
		currencyTabItem.setControl(cc);
		pageCompositeList.add(cc);

		ProductTypeComposite pc = new ProductTypeComposite(this, tabFolder, priceConfigComposite);
		if (pc.getCellReferenceProductTypeSelector() == null)
			pc.dispose();
		else {
			TabItem productTypeTabItem = new TabItem(tabFolder, SWT.NONE);
			productTypeTabItem.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference.CellReferencePage.productTypeTabItem.text")); //$NON-NLS-1$

			productTypeTabItem.setControl(pc);
			pageCompositeList.add(pc);
		}

		TabItem priceFragmentTabItem = new TabItem(tabFolder, SWT.NONE);
		priceFragmentTabItem.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference.CellReferencePage.priceFragmentTypeTabItem.text")); //$NON-NLS-1$
		PriceFragmentTypeComposite pfc = new PriceFragmentTypeComposite(this, tabFolder);
		priceFragmentTabItem.setControl(pfc);
		pageCompositeList.add(pfc);
		
		return pageCompositeList;
	}
	
	private StringBuffer scriptBufferOutline = new StringBuffer();
	private Map<String, String> scriptMap = new HashMap<String, String>();

	private String generatedScript = null;
	
	public void addDimensionScript(String dimensionKey, String dimensionScript){
		scriptMap.put(dimensionKey, dimensionScript);
		generateScript();
	}
	
	public void removeDimensionScript(String dimensionKey){
		if(scriptMap.get(dimensionKey) != null){
			scriptMap.remove(dimensionKey);
			generateScript();
		}//if
	}
	
	private void generateScript(){
		StringBuffer scriptBuffer = new StringBuffer();
		int i = 0;
		for(String script : scriptMap.values()){
			scriptBuffer.append(script);
			if(i < scriptMap.size() - 1){
				scriptBuffer.append(",").append(CellReferenceWizard.NEWLINE).append(CellReferenceWizard.TAB).append(CellReferenceWizard.TAB); //$NON-NLS-1$
			}//if
			i++;
		}//for
		
		generatedScript = String.format(scriptBufferOutline.toString(), scriptBuffer.toString());
		getSourcePreviewComposite().getDocument().set(generatedScript);
	}
	
	public String getGeneratedScript(){
		return generatedScript;
	}
}
