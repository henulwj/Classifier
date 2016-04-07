package classifier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class ClassifierTest{

	//�յĹ��캯��
	public ClassifierTest(){
		
	}
	
	//�������ݼ�,0����isp,1����wide,Ĭ����isp
	private int testDataSet = 0;

	//ÿһ��Ӧ���б�ǩ����Ŀ(50,60,70......140)��Ĭ����100
	private int numLabelFlow = 100;
	
	//�ޱ�ǩ����Ŀ(1000,2000,3000......10000)��Ĭ����5000
	private int numUnlabelFlow = 5000;
	
	//δ֪Ӧ����Ŀ(0,1,2,3)��Ĭ����0
	private int numUnknownApp = 0;
	
	//�������Ŀ(100,200,300......1000)��Ĭ����400
	private int numClusters = 400;
	
	//���෽��ѡ��0�������ǵĶ��������Erman�ķ���
	private int functionType = 0;
	
	//ѵ�������б�ǩ���ļ�·��
	private String labelFilePath = "";
	
	//ѵ�������ޱ�ǩ���ļ�·��
	private String unlabelFilePath = "";
	
	//�������ݼ��ļ�·��
	private String testFilePath = "";
	
	public int getTestDataSet() {
		return testDataSet;
	}


	public void setTestDataSet(int testDataSet) {
		this.testDataSet = testDataSet;
	}


	public int getNumLabelFlow() {
		return numLabelFlow;
	}


	public void setNumLabelFlow(int numLabelFlow) {
		this.numLabelFlow = numLabelFlow;
	}


	public int getNumUnlabelFlow() {
		return numUnlabelFlow;
	}


	public void setNumUnlabelFlow(int numUnlabelFlow) {
		this.numUnlabelFlow = numUnlabelFlow;
	}


	public int getNumUnknownApp() {
		return numUnknownApp;
	}


	public void setNumUnknownApp(int numUnknownApp) {
		this.numUnknownApp = numUnknownApp;
	}


	public int getNumClusters() {
		return numClusters;
	}


	public void setNumClusters(int numClusters) {
		this.numClusters = numClusters;
	}
	
	public int getFunctionType() {
		return functionType;
	}


	public void setFunctionType(int functionType) {
		this.functionType = functionType;
	}

	public String getLabelFilePath() {
		return labelFilePath;
	}


	public void setLabelFilePath(String labelFilePath) {
		this.labelFilePath = labelFilePath;
	}


	public String getUnlabelFilePath() {
		return unlabelFilePath;
	}


	public void setUnlabelFilePath(String unlabelFilePath) {
		this.unlabelFilePath = unlabelFilePath;
	}


	public String getTestFilePath() {
		return testFilePath;
	}


	public void setTestFilePath(String testFilePath) {
		this.testFilePath = testFilePath;
	}

	//����ԭ�������ṩ�����ݼ����ԣ��ӿ���̨��ȡ���ݲ���
	public void readDataArgs(){
		labelFilePath = "data/";	//�б�ǩ���ݼ��ļ�·��
		unlabelFilePath = "data/";	//�ޱ�ǩ���ݼ��ļ�·��
		testFilePath = "data/";	//�������ݼ��ļ�·��
		System.out.println("***************������������*****************");
		System.out.println("�������ݼ�(0:isp  1:wide)|ÿһ��Ӧ���б�ǩ����Ŀ|�ޱ�ǩ����Ŀ|δ֪Ӧ����Ŀ|�������Ŀ|���෽��(0:�Ľ��ķ���  1:Erman�ķ���)");
		Scanner scanner = new Scanner(System.in);	//�ӿ���̨��ȡ����
		this.setTestDataSet(scanner.nextInt());
		this.setNumLabelFlow(scanner.nextInt());
		this.setNumUnlabelFlow(scanner.nextInt());
		this.setNumUnknownApp(scanner.nextInt());
		this.setNumClusters(scanner.nextInt());
		this.setFunctionType(scanner.nextInt());
		scanner.close();
		if(this.testDataSet == 0){	//�����isp���ݼ������ø��ļ�·��
			labelFilePath = labelFilePath+"isp/training/labelled/";
			unlabelFilePath = unlabelFilePath+"isp/training/unlabelled/";
			testFilePath = testFilePath+"isp/testing/";
		}
		if(this.testDataSet == 1){	//�����wide���ݼ������ø��ļ�·��
			labelFilePath = labelFilePath+"wide/training/labelled/";
			unlabelFilePath = unlabelFilePath+"wide/training/unlabelled/";
			testFilePath = testFilePath+"wide/testing/";
		}
		
		labelFilePath = labelFilePath+this.numLabelFlow+"/";	//ÿ��Ӧ�ñ�ǩ����Ŀ��Ӧ�ļ���
		unlabelFilePath = unlabelFilePath+this.numUnlabelFlow+"/";	//�ޱ�ǩ����Ŀ��Ӧ�ļ���
		if(this.testDataSet == 0){
			switch (this.numUnknownApp) {	//δ֪Ӧ����Ŀ�ı�ʱ
				case 0:
					labelFilePath = labelFilePath+"labelled.arff";
					unlabelFilePath = unlabelFilePath+"unlabelled-origin.arff";
					testFilePath = testFilePath+"testing.arff";
					break;
				case 1:
					labelFilePath = labelFilePath+"labelled-nobt.arff";
					unlabelFilePath = unlabelFilePath+"unlabelled-origin-nobt.arff";
					testFilePath = testFilePath+"testing-nobt.arff";
					break;
				case 2:
					labelFilePath = labelFilePath+"labelled-nobtpop3.arff";
					unlabelFilePath = unlabelFilePath+"unlabelled-origin-nobtpop3.arff";
					testFilePath = testFilePath+"testing-nobtpop3.arff";
					break;
				case 3:
					labelFilePath = labelFilePath+"labelled-nobtpop3smtp.arff";
					unlabelFilePath = unlabelFilePath+"unlabelled-origin-nobtpop3smtp.arff";
					testFilePath = testFilePath+"testing-nobtpop3smtp.arff";
					break;
				default:
					break;
			}
		}
		if(this.testDataSet == 1){
			switch (this.numUnknownApp) {
				case 0:
					labelFilePath = labelFilePath+"labelled.arff";
					unlabelFilePath = unlabelFilePath+"unlabelled-origin.arff";
					testFilePath = testFilePath+"testing.arff";
					break;
				case 1:
					labelFilePath = labelFilePath+"labelled-nosmtp.arff";
					unlabelFilePath = unlabelFilePath+"unlabelled-origin-nosmtp.arff";
					testFilePath = testFilePath+"testing-nosmtp.arff";
					break;
				case 2:
					labelFilePath = labelFilePath+"labelled-nosmtpdns.arff";
					unlabelFilePath = unlabelFilePath+"unlabelled-origin-nosmtpdns.arff";
					testFilePath = testFilePath+"testing-nosmtpdns.arff";
					break;
				case 3:
					labelFilePath = labelFilePath+"labelled-nosmtpdnsssh.arff";
					unlabelFilePath = unlabelFilePath+"unlabelled-origin-nosmtpdnsssh.arff";
					testFilePath = testFilePath+"testing-nosmtpdnsssh.arff";
					break;
				default:
					break;
			}
		}
	}
	
	//��ȡ���Լ��ļ�·�����Լ����Բ���
	public void readFileArgs()
	{
		System.out.println("*****************��������ļ�·��********************");
		
		Scanner scanner = new Scanner(System.in);	//�ӿ���̨��ȡ����
		System.out.print("ѵ�������б�ǩ���ļ�·����");
		this.labelFilePath = scanner.nextLine();
		System.out.print("ѵ�������ޱ�ǩ���ļ�·����");
		this.unlabelFilePath = scanner.nextLine();
		System.out.print("���Լ��ļ�·����");
		this.testFilePath = scanner.nextLine();
		System.out.print("�������Ŀ��");
		this.numClusters = Integer.parseInt(scanner.nextLine());
		System.out.print("���Է���(0:�Ľ��ķ���   1:Erman�ķ���)��");
		this.functionType = Integer.parseInt(scanner.nextLine());
		scanner.close();
	}
	
	//���Է�����
	public void testClassifier() throws FileNotFoundException, IOException,Exception{
		System.out.print("�������ݼ��Ƿ����ԭ�������ṩ���ݼ�(Y/N)��");
		Scanner scanner = new Scanner(System.in);	//�ӿ���̨��ȡ����
		String tmpstr = scanner.nextLine();
		if(tmpstr.toLowerCase().equals("y")){
			this.readDataArgs();	//��ȡ����̨�����������ȡ�ļ�·��
		}
		else if(tmpstr.toLowerCase().equals("n")){
			this.readFileArgs();
		}
		else{
			System.out.println("��������");
			return;
		}
		
		FlowsProClassifier flowsProClassifier = new FlowsProClassifier();		
		flowsProClassifier.setLabelInsts(flowsProClassifier.getFileInstances(this.labelFilePath));	//��ȡ�б�ǩ�����ݼ�
		flowsProClassifier.setUnlabelInsts(flowsProClassifier.getFileInstances(this.unlabelFilePath));	//��ȡ�ޱ�ǩ�����ݼ�
		flowsProClassifier.preserveOriginLable(flowsProClassifier.getLabelInsts(),flowsProClassifier.getUnlabelInsts());	//����ѵ������ԭʼ����ǩ
		if(this.functionType == 0){	//���ǵķ���������ǩ��ֳ��Erman�ķ���û��
			flowsProClassifier.flowLabelPropagation();
		}
		flowsProClassifier.setNumPreClusters(this.numClusters);	//���þ������Ŀ
		System.out.println("���ڽ��з�����ԡ�����������");
		flowsProClassifier.constructNCC();	//���������
		flowsProClassifier.evaluateClassifier(flowsProClassifier.getFileInstances(this.testFilePath));	//�÷���������������ݼ����ȵ�������
		
	}
	
}
