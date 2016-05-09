package classifier;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClassifierTest test = new ClassifierTest(); 
		try {
			test.testClassifier();
		} catch (FileNotFoundException e) {
			System.out.println("找不到与输入数据匹配的文件");
		} catch (IOException e) {
			System.out.println("文件格式错误！");
		} catch (Exception e) {
			System.out.println("数据输入有误！");
		}
	}

}
