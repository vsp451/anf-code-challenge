package com.anf.core.services.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.anf.core.models.dao.AgeConfig;
import com.anf.core.models.dao.User;
import com.anf.core.services.ContentService;

@Component(immediate = true, service = ContentService.class)
public class ContentServiceImpl implements ContentService {
	private static final String AGE_CONFIG_PATH = "/etc/age";
	private static final String USER_ROOT_PATH = "/var/anf-code-challenge";
	
	@Reference
	private ResourceResolverFactory resolverFactory;
	
	ResourceResolver resolver = null;
	
	

	@Override
	public void commitUserDetails(User user,SlingHttpServletResponse resp) {
		try {
			validateAge(user,resp);
		} catch (RepositoryException | LoginException | IOException e) {
			e.printStackTrace();
		}
	}

	private void validateAge(User user,SlingHttpServletResponse resp) throws RepositoryException, LoginException, IOException {
		resolver = getSystemUserWriteService(resolverFactory);
		ValueMap ageConfigNode = resolver.getResource(AGE_CONFIG_PATH)
				.adaptTo(ValueMap.class);

		int minAge = Integer.parseInt(ageConfigNode.get("minAge", String.class));

		int maxAge = Integer.parseInt(ageConfigNode.get("maxAge", String.class));
		int userAge = user.getAge();
		if (userAge > minAge && userAge < maxAge) {
			commitDetails(user);
			resp.setStatus(HttpStatus.SC_ACCEPTED);
		}else {
			resp.setStatus(HttpStatus.SC_METHOD_FAILURE);
			
			resp.sendError(103,"User Age Invalid");
		}

	}

	private void commitDetails(User user) throws LoginException, RepositoryException {
  
        Session session = resolver.adaptTo(Session.class);

        final String firstName = user.getFirstName()
                .toLowerCase();
        final char firstChar = firstName.charAt(0);
        Node usersRootNode = JcrUtils.getOrCreateByPath(USER_ROOT_PATH + "/" + firstChar, "sling:Folder",
                "sling:OrderedFolder", session, true);
        
        Node userNode = JcrUtils.getOrCreateUniqueByPath(usersRootNode, firstName, "nt:unstructured");

        userNode.setProperty("firstName", user.getFirstName());
        userNode.setProperty("lastName", user.getLastName());
        userNode.setProperty("country", user.getCountry());
        userNode.setProperty("age", user.getAge());

        // save changes
        session.save();
		
	}
	
	private ResourceResolver getSystemUserWriteService(ResourceResolverFactory resourceResolverFactory)
			throws LoginException {
		Map<String, Object> param = new HashMap<>();
		param.put(ResourceResolverFactory.SUBSERVICE, "writeService");
		return resourceResolverFactory.getServiceResourceResolver(param);
	}

}
