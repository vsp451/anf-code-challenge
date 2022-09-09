package com.anf.core.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anf.core.models.dao.NewsEntityDAO;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewsFeedDataModel {

	private static final Logger LOG = LoggerFactory.getLogger(NewsFeedDataModel.class);

	@ValueMapValue
	private String newsFeedPath;

	@SlingObject
	ResourceResolver resourceResolver;

	List<NewsEntityDAO> newsList;

	@PostConstruct
	protected void init() throws RepositoryException {
		newsList = new ArrayList<>();
		Resource resource = resourceResolver.getResource(newsFeedPath + "/newsData");
		Iterator<Resource> itr = resource.listChildren();

		while (itr.hasNext()) {
			NewsEntityDAO dao = new NewsEntityDAO();
			Resource childResource = itr.next();
			ValueMap valueMap = childResource.getValueMap();
			dao.setAuthor(valueMap.get("author", StringUtils.EMPTY));
			dao.setContent(valueMap.get("content", StringUtils.EMPTY));
			dao.setDescription(valueMap.get("description", StringUtils.EMPTY));
			dao.setTitle(valueMap.get("title", StringUtils.EMPTY));
			dao.setUrl(valueMap.get("url", StringUtils.EMPTY));
			dao.setUrlImage(valueMap.get("urlImage", StringUtils.EMPTY));
			dao.setDate(getCurrentDate());
			newsList.add(dao);
		}

	}

	private String getCurrentDate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
		return simpleDateFormat.format(new Date());
	}

	public List<NewsEntityDAO> getNewsList() {
		return newsList;
	}

	public String getNewsFeedPath() {
		return newsFeedPath;
	}

}
