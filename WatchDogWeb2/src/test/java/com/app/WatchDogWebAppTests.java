package com.app;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.app.component.DogTestComponent;


@RunWith(SpringRunner.class)
@SpringBootTest
public class WatchDogWebAppTests {
	
	
	@Autowired
	DogTestComponent dogTestComponent;
	
	String INPUT_DIR="testdata/input";
	

	@Test
	public void contextLoads() {
	}
	
/*	@Test
	public void camDataTest() {
		String filename="addEntity@CaM_1.xml";
		String path=INPUT_DIR+"/"+filename;
		dogTestComponent.invokeDog(path);
	}*/

}
