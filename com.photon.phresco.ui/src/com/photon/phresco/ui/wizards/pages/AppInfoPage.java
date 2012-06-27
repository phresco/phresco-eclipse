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
package com.photon.phresco.ui.wizards.pages;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import com.photon.phresco.ui.wizards.PhrescoProjectWizard;

/**
 * App info page
 * 
 * @author arunachalam.lakshmanan@photoninfotech.net
 *
 */
public class AppInfoPage extends WizardPage implements IWizardPage {

	public AppInfoPage(String pageName) {
		super(pageName);
	}

	public AppInfoPage(String pageName, PhrescoProjectWizard phrescoProjectWizard) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {

	}
}