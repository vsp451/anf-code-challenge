package com.anf.core.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;

@Component(immediate = true, service = ResourceChangeListener.class, property = {
		ResourceChangeListener.PATHS + "=/content/anf-code-challenge/us/en",
		ResourceChangeListener.CHANGES + "=ADDED" })
public class PagePropertyChangeListener implements ResourceChangeListener {
	private static final Logger LOG = LoggerFactory.getLogger(PagePropertyChangeListener.class);

	@Reference
	ResourceResolverFactory resourceResolverFactory;

	@Override
	public void onChange(List<ResourceChange> list) {
		ResourceResolver resolver = null;
		try {
			resolver = getSystemUserWriteService(resourceResolverFactory);
			for (ResourceChange resourceChange : list) {
				LOG.debug("Type: {}, Page: {}", resourceChange.getType(), resourceChange.getPath());
				Resource res = resolver.getResource(resourceChange.getPath().substring(0,
						resourceChange.getPath().lastIndexOf("jcr:content") + JcrConstants.JCR_CONTENT.length()));
				if (res.getPath().endsWith(JcrConstants.JCR_CONTENT)) {
					Node node = res.adaptTo(Node.class);
					node.setProperty("pageCreated", true);
					resolver.commit();
					break;
				}

			}
		} catch (LoginException e) {
			LOG.error("Login Exception {0}", e);
		} catch (ValueFormatException e) {
			LOG.error("Value Format Exception {0}", e);
		} catch (VersionException e) {
			LOG.error("Version Exception {0}", e);
		} catch (LockException e) {
			LOG.error("Lock Exception {0}", e);
		} catch (ConstraintViolationException e) {
			LOG.error("Constraint Exception {0}", e);
		} catch (RepositoryException e) {
			LOG.error("Repository Exception {0}", e);
		} catch (PersistenceException e) {
			LOG.error("Persistence Exception {0}", e);
		}

	}

	/**
	 * Get System User
	 * 
	 * @param resourceResolverFactory
	 * @return
	 * @throws LoginException
	 */
	private ResourceResolver getSystemUserWriteService(ResourceResolverFactory resourceResolverFactory)
			throws LoginException {
		Map<String, Object> param = new HashMap<>();
		param.put(ResourceResolverFactory.SUBSERVICE, "writeService");
		return resourceResolverFactory.getServiceResourceResolver(param);
	}
}
