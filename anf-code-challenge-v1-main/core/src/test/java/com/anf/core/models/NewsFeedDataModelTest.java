package com.anf.core.models;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;

import javax.jcr.Node;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.anf.core.models.dao.NewsEntityDAO;
import com.day.cq.wcm.api.Page;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
public class NewsFeedDataModelTest {

	private NewsFeedDataModel newsFeedDataModel;

	private Page page;
	private Resource resource;

	@BeforeEach
	public void setup(AemContext context) throws Exception {

		page = context.create().page("/content/mypage");
		resource = context.create().resource(page, "newsfeed", "sling:resourceType",
				"anf-code-challenge/components/newsfeed", "newsFeedPath", "/var/commerce/products/anf-code-challenge");

		context.load().json("/newsfeed-data.json", "/var/commerce/products/anf-code-challenge");

		newsFeedDataModel = context.getService(ModelFactory.class).createModel(resource, NewsFeedDataModel.class);

	}

	@Test
	void testGetNewsFeedList() throws Exception {

		assertTrue(newsFeedDataModel.getNewsFeedPath().equals("/var/commerce/products/anf-code-challenge"));
		List<NewsEntityDAO> feedList = newsFeedDataModel.getNewsList();
		assertNotNull(feedList);
		assertTrue(feedList.size() == 10);
		assertTrue(feedList.get(0).getTitle()
				.equals("UFC 273: Five things we learned as Alexander Volkanovski dominates 'Korean Zombie'"));
		assertTrue(feedList.get(0).getAuthor().equals("Caroline Fox"));
	}

	@Test
	void testGetNewsFeedList_NoDataNodeFound() throws Exception {

		assertTrue(newsFeedDataModel.getNewsFeedPath().equals("/var/commerce/products/anf-code-challenge"));
		List<NewsEntityDAO> newsfeedList = newsFeedDataModel.getNewsList();
		assertNotNull(newsfeedList);
		assertTrue(newsfeedList.get(0).getAuthor().equals("Caroline Fox"));
	}

}
