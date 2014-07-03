package com.cyou.marketing.bcloud.web.functional.gui;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.openqa.selenium.By;
import com.cyou.marketing.bcloud.web.functional.BaseSeleniumTestCase;

public class ProfileFT extends BaseSeleniumTestCase {

	/**
	 * 修改用户资料.
	 */
	@Test
	public void editProfile() {
		s.open("/profile");
		s.type(By.id("name"), "Kevin");
		s.click(By.id("submit_btn"));
		assertThat(s.isTextPresent("Kevin")).describedAs("没有成功消息").isTrue();
	}
}
