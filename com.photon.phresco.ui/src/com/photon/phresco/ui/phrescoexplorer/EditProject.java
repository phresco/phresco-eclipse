package com.photon.phresco.ui.phrescoexplorer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.HandlerUtil;

import com.photon.phresco.commons.PhrescoDialog;
import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.ArtifactGroupInfo;
import com.photon.phresco.commons.model.ArtifactInfo;
import com.photon.phresco.commons.model.Category;
import com.photon.phresco.commons.model.DownloadInfo;
import com.photon.phresco.commons.model.ProjectInfo;
import com.photon.phresco.commons.model.WebService;
import com.photon.phresco.commons.util.ApplicationManagerUtil;
import com.photon.phresco.commons.util.BaseAction;
import com.photon.phresco.commons.util.PhrescoUtil;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.service.client.api.ServiceManager;
import com.photon.phresco.ui.resource.Messages;
import com.photon.phresco.ui.wizards.componets.DatabaseComponent;
import com.photon.phresco.ui.wizards.componets.ServerComponent;

/**
 * @author suresh_ma
 *
 */
public class EditProject extends AbstractHandler {
	
	private Text nameText;
	private Text codeText;
	private Text descText;
	private Text appDirText;
	
	private List<ServerComponent> serverComponents = new ArrayList<ServerComponent>();
	private List<DatabaseComponent> dbComponents = new ArrayList<DatabaseComponent>();
	
	private Button[] webServiceButtons;
	private Button webServiceButton;
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final Shell shell = HandlerUtil.getActiveShell(event);
		final Shell buildDialog = new Shell(shell, SWT.APPLICATION_MODAL |  SWT.DIALOG_TRIM | SWT.MIN | SWT.MAX | SWT.TITLE | SWT.RESIZE);
		GridLayout layout = new GridLayout(1, false);
		buildDialog.setLocation(385, 130);
		buildDialog.setLayout(layout);
		buildDialog.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final ScrolledComposite scrolledComposite = new ScrolledComposite(buildDialog, SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		scrolledComposite.setAlwaysShowScrollBars(true);
		
		final Composite composite = new Composite(scrolledComposite, SWT.NONE);
		GridLayout CompositeLayout = new GridLayout(1, true);
		composite.setLayout(CompositeLayout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		try {
			BaseAction baseAction = new BaseAction();
			String userId = baseAction.getUserId();
			ProjectInfo projectInfo = PhrescoUtil.getProjectInfo();
			final ApplicationInfo appInfo = PhrescoUtil.getApplicationInfo();
			final ServiceManager serviceManager = PhrescoUtil.getServiceManager(userId);
			if(serviceManager == null) {
				PhrescoDialog.errorDialog(buildDialog, Messages.WARNING, Messages.PHRESCO_LOGIN_WARNING);
				return null;
			}
			final String customerId = PhrescoUtil.getCustomerId();
			final String techId = PhrescoUtil.getTechId();
			final String platform = PhrescoUtil.findPlatform();
			createAppInfoPage(composite, projectInfo, appInfo);
			
			final List<DownloadInfo> servers = serviceManager.getDownloads(customerId, techId, Category.SERVER.name(), platform);
			if(CollectionUtils.isNotEmpty(servers)) {
				final Group serverGroup = new Group(composite, SWT.NONE);
				serverGroup.setText(Messages.SERVERS);
				GridLayout serverLayout = new GridLayout(5, false);
				serverGroup.setLayout(serverLayout);
				serverGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
				ServerComponent serverComponent = new ServerComponent();
				serverComponent.getServers(serverGroup, servers, customerId, techId, platform);
				serverComponents.add(serverComponent);
				
				Button serverAddButton = new Button(serverGroup, SWT.PUSH);
				serverAddButton.setText("+");
				serverAddButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						try {
							ServerComponent serverComponent = new ServerComponent();
							serverComponent.getServers(serverGroup, servers, customerId, techId, platform);
							serverComponents.add(serverComponent);
							Button serverDeleteButton = new Button(serverGroup, SWT.PUSH);
							serverDeleteButton.setText("-");
							
						} catch (PhrescoException e1) {
							PhrescoDialog.errorDialog(buildDialog, Messages.ERROR, e1.getLocalizedMessage());
						}
						serverGroup.redraw();
						serverGroup.pack();
						composite.redraw();
						composite.pack();
						reSize(composite, scrolledComposite);
						super.widgetSelected(e);
					}
				});
			}
			final List<DownloadInfo> dataBases = serviceManager.getDownloads(customerId, techId, Category.DATABASE.name(), platform);
			if(CollectionUtils.isNotEmpty(dataBases)) {
				final Group dbGroup = new Group(composite, SWT.NONE);
				dbGroup.setText(Messages.DATABASES);
				GridLayout dbLlayout = new GridLayout(5, false);
				dbGroup.setLayout(dbLlayout);
				dbGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
				DatabaseComponent dbComponent = new DatabaseComponent();
				dbComponent.getDataBases(dbGroup, dataBases, customerId, techId, platform);
				dbComponents.add(dbComponent);
				
				Button dbAddButton = new Button(dbGroup, SWT.PUSH);
				dbAddButton.setText("+");
				
				dbAddButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						try {
							DatabaseComponent dbComponent = new DatabaseComponent();
							dbComponent.getDataBases(dbGroup, dataBases, customerId, techId, platform);
							dbComponents.add(dbComponent);
							
							Button dbDeleteButton = new Button(dbGroup, SWT.PUSH);
							dbDeleteButton.setText("-");
							
						} catch (PhrescoException e1) {
							PhrescoDialog.errorDialog(buildDialog, Messages.ERROR, e1.getLocalizedMessage());
						}
						dbGroup.redraw();
						composite.redraw();
						composite.pack();
						reSize(composite, scrolledComposite);
						super.widgetSelected(e);
					}
				});
			}
			getWebServices(serviceManager, composite);
			
			Composite updateComposite = new Composite(buildDialog, SWT.NONE);
			GridLayout gridLayout = new GridLayout(2, false);
			updateComposite.setLayout(gridLayout);
			updateComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			Button updateButton = new Button(updateComposite, SWT.PUSH);
			updateButton.setText(Messages.UPDATE);
			
			Listener updateListener = new Listener() {
				@Override
				public void handleEvent(Event events) {
					boolean validate = validate(shell);
					if(!validate) {
						return;
					}
					//To set the basic information into applicationInfo
					ApplicationManagerUtil managerUtil = new ApplicationManagerUtil();
					appInfo.setAppDirName(appDirText.getText());
					appInfo.setCode(codeText.getText());
					appInfo.setName(nameText.getText());
					if(StringUtils.isNotEmpty(descText.getText())) {
						appInfo.setDescription(descText.getText());
					}
					String oldAppDirName = PhrescoUtil.getProjectName();
					
					//Top set the selected server
					List<ArtifactGroupInfo> artifactGroupInfos = new ArrayList<ArtifactGroupInfo>();
					setSelectedServers(appInfo, artifactGroupInfos);
					setSelectedDatabase(appInfo, artifactGroupInfos);
					
					List<String> webServiceIds = new ArrayList<String>();
					List<Button> buttons = Arrays.asList(webServiceButtons);
					int i = 0;
					for (Button button : buttons) {
						if(button.getSelection()) {
							String webServiceId = (String) button.getData(button.getText());
							webServiceIds.add(webServiceId);
						}
						i = i+ 1;
					}
					if(CollectionUtils.isNotEmpty(webServiceIds)) {
						appInfo.setSelectedWebservices(webServiceIds);
					}
					
					try {
						managerUtil.updateApplication(oldAppDirName, appInfo);
					} catch (PhrescoException e) {
						PhrescoDialog.exceptionDialog(shell, e);
					}
				}
			};
			
			updateButton.addListener(SWT.Selection, updateListener);
			
			Button cancelButton = new Button(updateComposite, SWT.PUSH);
			cancelButton.setText(Messages.CANCEL);
			
		} catch (PhrescoException e) {
			PhrescoDialog.errorDialog(buildDialog, Messages.ERROR, e.getLocalizedMessage());
		}
		
		scrolledComposite.setContent(composite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		reSize(composite, scrolledComposite);
		scrolledComposite.pack();
		scrolledComposite.redraw();
		Point size = scrolledComposite.getSize();
		int x = size.x;
		int y = size.y;
		buildDialog.setSize(x + 100, y + 100);
		buildDialog.open();
		return null;
	}
	
	/**
	 * @param buildDialog
	 */
	private void createAppInfoPage(Composite comp, ProjectInfo projectInfo, ApplicationInfo appInfo) {
		
		if(projectInfo == null || appInfo == null) {
			return;
		}
		Composite composite = new Composite(comp, SWT.BORDER);
		GridLayout gridlayout = new GridLayout(6, false);
		composite.setLayout(gridlayout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label nameLable = new Label(composite, SWT.NONE);
		nameLable.setText(Messages.NAME);

		nameText = new Text(composite, SWT.BORDER);
		nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		nameText.setText(appInfo.getName());

		Label codeLable = new Label(composite, SWT.NONE);
		codeLable.setText(Messages.CODE);

		codeText = new Text(composite, SWT.BORDER);
		codeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		codeText.setText(appInfo.getCode());

		Label descLable = new Label(composite, SWT.NONE);
		descLable.setText(Messages.DESCRIPTION);

		descText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData descGridData = new GridData();
		descGridData.widthHint = 100;
		descGridData.heightHint = 40;
		descText.setLayoutData(descGridData);
		if(StringUtils.isNotEmpty(appInfo.getDescription())) { 
			descText.setText(appInfo.getDescription());
		}

		Label appDirLabel = new Label(composite, SWT.NONE);
		appDirLabel.setText(Messages.APP_DIRECTORY);

		appDirText = new Text(composite, SWT.BORDER);
		appDirText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		appDirText.setText(appInfo.getAppDirName());

		Label versionLabel = new Label(composite, SWT.NONE);
		versionLabel.setText(Messages.VERSION);

		Text versionText = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		versionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		versionText.setText(projectInfo.getVersion());

		Label techLabel = new Label(composite, SWT.NONE);
		techLabel.setText(Messages.TECHNOLOGY);

		Text techText = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		techText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		techText.setText(appInfo.getTechInfo().getName() + Messages.HYPHEN + appInfo.getTechInfo().getVersion());
	}

	
	private void getWebServices(ServiceManager serviceManager, Composite composite) {
		try {
			List<WebService> webServices = serviceManager.getWebServices();
			if(CollectionUtils.isEmpty(webServices)) {
				return;
			}
			Group webserviceGroup = new Group(composite, SWT.NONE);
			webserviceGroup.setText(Messages.WEBSERVICES);
			GridLayout webServiceLayout = new GridLayout(5, false);
			webserviceGroup.setLayout(webServiceLayout);
			webserviceGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			webServiceButtons = new Button[webServices.size()];
			int i =0;
			for (WebService webService : webServices) {
				webServiceButtons[i] = new Button(webserviceGroup, SWT.CHECK);
				webServiceButtons[i].setText(webService.getName());
				webServiceButtons[i].setData(webService.getName(), webService.getId());
				i = i + 1;
			}
		} catch (PhrescoException e) {
			PhrescoDialog.errorDialog(composite.getShell(), Messages.ERROR, e.getLocalizedMessage());
		}
	}

	private void reSize(final Composite composite, final ScrolledComposite scrolledComposite) {
		composite.addListener(SWT.Resize, new Listener() {
			int width = -1;
			@Override
			public void handleEvent(Event event) {
				int newWidth = composite.getSize().x;
				if (newWidth != width) {
			        scrolledComposite.setMinHeight(composite.computeSize(newWidth, SWT.DEFAULT).y);
			        width = newWidth;
			    }
			}
		});
	}
	
	private boolean validate(Shell shell) {
		if(StringUtils.isEmpty(nameText.getText())) {
			PhrescoDialog.errorDialog(shell, Messages.ERROR, "Name should no be empty");
			return false;
		} if(StringUtils.isEmpty(codeText.getText())) {
			PhrescoDialog.errorDialog(shell, Messages.ERROR, "Code should no be empty");
			return false;
		} if(StringUtils.isEmpty(appDirText.getText())) {
			PhrescoDialog.errorDialog(shell, Messages.ERROR, "App Directory should no be empty");
			return false;
		} 
		return true;
	}
	
	/**
	 * @param appInfo
	 * @param artifactGroupInfos
	 */
	private void setSelectedServers(final ApplicationInfo appInfo,
			List<ArtifactGroupInfo> artifactGroupInfos) {
		if(CollectionUtils.isNotEmpty(serverComponents)) {
			for (ServerComponent serverComponent : serverComponents) {
				String serverName = serverComponent.serverNameCombo.getText();
				String[] selectedVersions = serverComponent.serverVersionListBox.getSelection();
				String serverId = ServerComponent.serverIdMap.get(serverName);
				ArtifactGroupInfo artifactGroupInfo = new ArtifactGroupInfo();
				artifactGroupInfo.setArtifactGroupId(serverId);
				
				List<String> artifactInfoIds = new ArrayList<String>();
				List<ArtifactInfo> artifactInfos = ServerComponent.serverVersionMap.get(serverName);
				for (ArtifactInfo artifactInfo : artifactInfos) {
					List<String> selectedVersionsList = Arrays.asList(selectedVersions);
					if(selectedVersionsList.contains(artifactInfo.getVersion())) {
						artifactInfoIds.add(artifactInfo.getId());
					}
				}
				artifactGroupInfo.setArtifactInfoIds(artifactInfoIds);
				artifactGroupInfos.add(artifactGroupInfo);
				appInfo.setSelectedServers(artifactGroupInfos);
			}
		}
	}
	
	/**
	 * @param appInfo
	 * @param artifactGroupInfos
	 */
	private void setSelectedDatabase(final ApplicationInfo appInfo,
			List<ArtifactGroupInfo> artifactGroupInfos) {
		if(CollectionUtils.isNotEmpty(dbComponents)) {
			for (DatabaseComponent dbComponent : dbComponents) {
				String dataBaseName = dbComponent.dbNameCombo.getText();
				String[] selectedVersions = dbComponent.dbVersionListBox.getSelection();
				String serverId = DatabaseComponent.dbIdMap.get(dataBaseName);
				ArtifactGroupInfo artifactGroupInfo = new ArtifactGroupInfo();
				artifactGroupInfo.setArtifactGroupId(serverId);
				
				List<String> artifactInfoIds = new ArrayList<String>();
				List<ArtifactInfo> artifactInfos = DatabaseComponent.dbVersionMap.get(dataBaseName);
				for (ArtifactInfo artifactInfo : artifactInfos) {
					List<String> selectedVersionsList = Arrays.asList(selectedVersions);
					if(selectedVersionsList.contains(artifactInfo.getVersion())) {
						artifactInfoIds.add(artifactInfo.getId());
					}
				}
				artifactGroupInfo.setArtifactInfoIds(artifactInfoIds);
				artifactGroupInfos.add(artifactGroupInfo);
				appInfo.setSelectedDatabases(artifactGroupInfos);
			}
		}
	}
}
