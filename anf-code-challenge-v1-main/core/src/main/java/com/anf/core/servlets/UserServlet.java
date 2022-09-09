/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.anf.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.anf.core.models.dao.User;
import com.anf.core.services.ContentService;

@Component(service = { Servlet.class })
@SlingServletPaths(value = "/bin/saveUserDetails")
public class UserServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;

	@Reference
	private ContentService contentService;

	@Override
	protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws IOException {
		// Make use of ContentService to write the business logic
		User user = new User();
		user.setFirstName(req.getParameter("firstName"));
		user.setLastName(req.getParameter("lastName"));
		user.setCountry(req.getParameter("country"));
		user.setAge(Integer.parseInt(req.getParameter("age")));
		contentService.commitUserDetails(user,resp);	
	}
}
