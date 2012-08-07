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
package com.photon.phresco.ui.wizards;

import java.util.Properties;

import org.apache.maven.properties.internal.EnvironmentUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.api.ProjectAdministrator;
import com.photon.phresco.model.ProjectInfo;
import com.photon.phresco.model.Technology;
import com.photon.phresco.model.UserInfo;
import com.photon.phresco.ui.Activator;
import com.photon.phresco.ui.builder.PhrescoNature;
import com.photon.phresco.ui.preferences.PreferenceConstants;
import com.photon.phresco.ui.wizards.pages.AppInfoPage;
import com.photon.phresco.ui.wizards.pages.ConfigurationsPage;
import com.photon.phresco.ui.wizards.pages.FeaturesPage;
import com.photon.phresco.util.Credentials;

/**
 * Phresco project wizard
 * 
 * @author arunachalam.lakshmanan@photoninfotech.net
 */
public class PhrescoProjectWizard extends Wizard implements INewWizard {

	private AppInfoPage appInfoPage;
	private FeaturesPage featuresPage;
	private ConfigurationsPage configurationsPage;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void addPages() {
		// TODO Auto-generated method stub
		super.addPages();
		appInfoPage = new AppInfoPage("AppInfoPage");
		addPage(appInfoPage);
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		ProjectInfo projectInfo = new ProjectInfo();
		projectInfo.setApplication("apptype-webapp");
		String projectName = "PHR_" + appInfoPage.projectTxt.getText();
		projectInfo.setCode(projectName);
		projectInfo.setName(appInfoPage.projectTxt.getText());
		projectInfo.setVersion("1.0.0");
		IProjectDescription description;
		
		Technology technology = new Technology("tech-java-standalone", "Java Standalone");
		projectInfo.setTechnology(technology);
		
		String path = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString() + "/workspace/projects/" + projectName;	
		
		//TODO:set the env variable PHRESCO_HOME value :: " + ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString()
		
		
		try {
			//Create Phresco Project
			ProjectAdministrator admin = PhrescoFrameworkFactory.getProjectAdministrator();
			
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			String username = store.getString(PreferenceConstants.USER_NAME);
			String password = store.getString(PreferenceConstants.PASSWORD);
			Credentials credentials = new Credentials(username, password);
			UserInfo user = admin.doLogin(credentials);
			admin.createProject(projectInfo, null, user);
			
			//Link the created Project to Eclipse
			description = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
			//BuildCommand buildCommand1 = new BuildCommand();
			//buildCommand1.setName("org.eclipse.jdt.core.javabuilder");
			//BuildCommand buildCommand2 = new BuildCommand();
			//buildCommand2.setName("org.maven.ide.eclipse.maven2Builder");
			//[] buildSpec = {buildCommand2};
			//description.setBuildSpec(buildSpec);
			
			
			description.setLocation(new Path(path));
			String[] natures = {PhrescoNature.NATURE_ID, "org.maven.ide.eclipse.maven2Nature"};
			description.setNatureIds(natures);
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
			project.create(description, null);
			project.open(null);
		} catch (PhrescoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
		

}