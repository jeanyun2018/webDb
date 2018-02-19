package com.jx;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class WebDbApplicationTests {

	@Test
	public void contextLoads() {
		//mainContol.startWork("/home/jean/upLoad/mysql-test.xml");
	}
	@Test
	public void testEventBus() {
		//eventBusManage.postDbBus(new String("554858"));
	}
}
