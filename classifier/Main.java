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
			System.out.println("�Ҳ�������������ƥ����ļ�");
		} catch (IOException e) {
			System.out.println("�ļ���ʽ����");
		} catch (Exception e) {
			System.out.println("������������");
		}
	}

}
