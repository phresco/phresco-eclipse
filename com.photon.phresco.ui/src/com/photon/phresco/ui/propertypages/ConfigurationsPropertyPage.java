/*
 * ###
 * 
 * Copyright (C) 1999 - 2012 Photon Infotech Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ###
 */
package com.photon.phresco.ui.propertypages;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import com.photon.phresco.commons.model.User;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.api.ProjectAdministrator;
import com.photon.phresco.ui.dialog.ManageEnvironmentsDialog;
import com.photon.phresco.ui.internal.controls.PhrescoConfigControl;
import com.photon.phresco.util.Credentials;

/**
 * Configuration page for project
 * @author arunachalam.lakshmanan@photoninfotech.net
 *
 */
public class ConfigurationsPropertyPage extends PropertyPage implements
		IWorkbenchPropertyPage {
	private IPath configPath;
	
	private String projectCode ;
	
	public ConfigurationsPropertyPage() {
		setTitle("Configurations");
		setDescription("Project local configurations. Select Enviroments to view the configurations");
		try {
			doLogin();
		} catch (PhrescoException e) {
			e.printStackTrace();
		}
	}
	
	public String getProjectCode() {
		return projectCode;
	}
	
	private void doLogin() throws PhrescoException {
		try {
			ProjectAdministrator administrator = PhrescoFrameworkFactory
					.getProjectAdministrator();
			String username = "suresh_ma";//store.getString(PreferenceConstants.USER_NAME);
			String password = "SureshE3510";//store.getString(PreferenceConstants.PASSWORD);
			Credentials credentials = new Credentials(username, password);
			administrator.doLogin(credentials);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PhrescoException();
		}
	}

	@Override
	protected Control createContents(Composite parent) {
		IAdaptable element2 = getElement();
		Object adapter = element2.getAdapter(IProject.class);
		if(adapter instanceof IProject) {
			IProject project = (IProject) adapter;
			configPath = project.getFolder(".phresco").getFile("phresco-env-config.xml").getFullPath();
			projectCode = project.getName();
		}
		PhrescoConfigControl configParent = new PhrescoConfigControl(parent, SWT.NONE, configPath,projectCode);
		return configParent;
	}
}
