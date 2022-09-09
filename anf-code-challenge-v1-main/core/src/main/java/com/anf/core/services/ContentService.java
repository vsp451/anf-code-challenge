package com.anf.core.services;

import org.apache.sling.api.SlingHttpServletResponse;

import com.anf.core.models.dao.User;

public interface ContentService {
	void commitUserDetails(User user,SlingHttpServletResponse resp);
}
