package com.anf.core.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.crx.JcrConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

@Component(service = Servlet.class)
@SlingServletResourceTypes(resourceTypes = "/bin/dropDownList", methods = HttpConstants.METHOD_GET)
public class DropdownServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(DropdownServlet.class);


	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

		final ResourceResolver resolver = request.getResourceResolver();

		final String datasourcePath = getPath(request);

		final InputStream inputStream = readFromFile(datasourcePath, resolver);

		if (inputStream != null) {
			final Map<String, String> data = deserialize(inputStream);
			final List<Resource> valueMapResourceList = dataToResources(data, resolver);
			final DataSource dataSource = new SimpleDataSource(valueMapResourceList.iterator());
			request.setAttribute(DataSource.class.getName(), dataSource);
		}

	}

	private InputStream readFromFile(final String path, final ResourceResolver resolver) {

		try {
			Node contentNode = JcrUtils.getNodeIfExists(path + "/jcr:content/renditions/original/jcr:content",
					resolver.adaptTo(Session.class));

			if (null != contentNode) {
				return contentNode.getProperty("jcr:data").getBinary().getStream();
			}

		} catch (RepositoryException e) {
			log.error("Couldn't read json from path {}", path, e);
		}

		return null;
	}

	private String getPath(final SlingHttpServletRequest request) {

		final Resource pathResource = request.getResource();
		final Resource datasourceResource = pathResource.getChild("datasource");
		if (datasourceResource != null && datasourceResource.getValueMap().containsKey("jsonFilePath")) {
			return datasourceResource.getValueMap().get("jsonFilePath", String.class);
		}

		return StringUtils.EMPTY;
	}

	private Map<String, String> deserialize(final InputStream jsonStream) {
		Map<String, String> data = new HashMap<>();

		try {

			final ObjectReader reader = new ObjectMapper().readerFor(Map.class);
			data = reader.readValue(jsonStream);

		} catch (final IOException e) {
			log.error("Unexpected exception while retrieving json values from file", e);
		}
		return data;
	}

	private List<Resource> dataToResources(final Map<String, String> data, final ResourceResolver resolver) {

		List<Resource> resourceList = new ArrayList<>();
		for (Map.Entry<String, String> entry : data.entrySet()) {
			final ValueMap valueMap = new ValueMapDecorator(new HashMap<>());
			valueMap.put("value", entry.getValue());
			valueMap.put("text", entry.getKey());
			resourceList.add(
					new ValueMapResource(resolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, valueMap));

		}

		return resourceList;
	}
}
