package com.cyou.marketing.bcloud.web.functional.rest;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springside.examples.quickstart.data.TaskData;
import com.cyou.marketing.bcloud.web.entity.Task;
import com.cyou.marketing.bcloud.web.functional.BaseFunctionalTestCase;
import org.springside.modules.mapper.JsonMapper;
import org.springside.modules.test.category.Smoke;

/**
 * 任务管理的功能测试, 测试页面JavaScript及主要用户故事流程.
 * 
 * @author calvin
 */
public class TaskRestFT extends BaseFunctionalTestCase {

	private final RestTemplate restTemplate = new RestTemplate();

	private final JsonMapper jsonMapper = new JsonMapper();

	private static class TaskList extends ArrayList<Task> {
	}

	private static String resoureUrl;

	@BeforeClass
	public static void initUrl() {
		resoureUrl = baseUrl + "/api/v1/task";
	}

	/**
	 * 查看任务列表.
	 */
	@Test
	@Category(Smoke.class)
	public void listTasks() {
		TaskList tasks = restTemplate.getForObject(resoureUrl, TaskList.class);
		assertThat(tasks).hasSize(5);
		assertThat(tasks.get(0).getTitle()).isEqualTo("Study PlayFramework 2.0");
	}

	/**
	 * 获取任务.
	 */
	@Test
	@Category(Smoke.class)
	public void getTask() {
		Task task = restTemplate.getForObject(resoureUrl + "/{id}", Task.class, 1L);
		assertThat(task.getTitle()).isEqualTo("Study PlayFramework 2.0");
	}

	/**
	 * 创建/更新/删除任务.
	 */
	@Test
	@Category(Smoke.class)
	public void createUpdateAndDeleteTask() {

		// create
		Task task = TaskData.randomTask();

		URI taskUri = restTemplate.postForLocation(resoureUrl, task);
		System.out.println(taskUri.toString());
		Task createdTask = restTemplate.getForObject(taskUri, Task.class);
		assertThat(createdTask.getTitle()).isEqualTo(task.getTitle());

		// update
		String id = StringUtils.substringAfterLast(taskUri.toString(), "/");
		task.setId(new Long(id));
		task.setTitle(TaskData.randomTitle());

		restTemplate.put(taskUri, task);

		Task updatedTask = restTemplate.getForObject(taskUri, Task.class);
		assertThat(updatedTask.getTitle()).isEqualTo(task.getTitle());

		// delete
		restTemplate.delete(taskUri);

		try {
			restTemplate.getForObject(taskUri, Task.class);
			fail("Get should fail while feth a deleted task");
		} catch (HttpStatusCodeException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		}
	}

	@Test
	public void invalidInput() {

		// create
		Task titleBlankTask = new Task();
		try {
			restTemplate.postForLocation(resoureUrl, titleBlankTask);
			fail("Create should fail while title is blank");
		} catch (HttpStatusCodeException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			Map messages = jsonMapper.fromJson(e.getResponseBodyAsString(), Map.class);
			assertThat(messages).hasSize(1);
			Assert.assertTrue(messages.get("title").equals("may not be empty") || messages.get("title").equals("不能为空"));
		}

		// update
		titleBlankTask.setId(1L);
		try {
			restTemplate.put(resoureUrl + "/1", titleBlankTask);
			fail("Update should fail while title is blank");
		} catch (HttpStatusCodeException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			Map messages = jsonMapper.fromJson(e.getResponseBodyAsString(), Map.class);
			assertThat(messages).hasSize(1);
			Assert.assertTrue(messages.get("title").equals("may not be empty") || messages.get("title").equals("不能为空"));
		}
	}
}
