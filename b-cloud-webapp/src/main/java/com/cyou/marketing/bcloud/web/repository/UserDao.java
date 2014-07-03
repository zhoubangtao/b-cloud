package com.cyou.marketing.bcloud.web.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import com.cyou.marketing.bcloud.web.entity.User;

public interface UserDao extends PagingAndSortingRepository<User, Long> {
	User findByLoginName(String loginName);
}
