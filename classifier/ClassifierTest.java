package classifier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class ClassifierTest{

	//空的构造函数
	public ClassifierTest(){
		
	}
	
	//测试数据集,0代表isp,1代表wide,默认是isp
	private int testDataSet = 0;

	//每一种应用有标签流数目(50,60,70......140)，默认是100
	private int numLabelFlow = 100;
	
	//无标签流数目(1000,2000,3000......10000)，默认是5000
	private int numUnlabelFlow = 5000;
	
	//未知应用数目(0,1,2,3)，默认是0
	private int numUnknownApp = 0;
	
	//聚类簇数目(100,200,300......1000)，默认是400
	private int numClusters = 400;
	
	//分类方法选择，0代表我们的额方法，代表Erman的方法
	private int functionType = 0;
	
	//训练集中有标签流文件路径
	private String labelFilePath = "";
	
	//训练集中无标签流文件路径
	private String unlabelFilePath = "";
	
	//测试数据集文件路径
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

	//采用原文作者提供的数据集测试，从控制台读取数据参数
	public void readDataArgs(){
		labelFilePath = "data/";	//有标签数据集文件路径
		unlabelFilePath = "data/";	//无标签数据集文件路径
		testFilePath = "data/";	//测试数据集文件路径
		System.out.println("***************测试数据设置*****************");
		System.out.println("测试数据集(0:isp  1:wide)|每一种应用有标签流数目|无标签流数目|未知应用数目|聚类粗数目|分类方法(0:改进的方法  1:Erman的方法)");
		Scanner scanner = new Scanner(System.in);	//从控制台读取参数
		this.setTestDataSet(scanner.nextInt());
		this.setNumLabelFlow(scanner.nextInt());
		this.setNumUnlabelFlow(scanner.nextInt());
		this.setNumUnknownApp(scanner.nextInt());
		this.setNumClusters(scanner.nextInt());
		this.setFunctionType(scanner.nextInt());
		scanner.close();
		if(this.testDataSet == 0){	//如果是isp数据集，设置各文件路径
			labelFilePath = labelFilePath+"isp/training/labelled/";
			unlabelFilePath = unlabelFilePath+"isp/training/unlabelled/";
			testFilePath = testFilePath+"isp/testing/";
		}
		if(this.testDataSet == 1){	//如果是wide数据集，设置各文件路径
			labelFilePath = labelFilePath+"wide/training/labelled/";
			unlabelFilePath = unlabelFilePath+"wide/training/unlabelled/";
			testFilePath = testFilePath+"wide/testing/";
		}
		
		labelFilePath = labelFilePath+this.numLabelFlow+"/";	//每个应用标签流数目对应文件夹
		unlabelFilePath = unlabelFilePath+this.numUnlabelFlow+"/";	//无标签流数目对应文件夹
		if(this.testDataSet == 0){
			switch (this.numUnknownApp) {	//未知应用数目改变时
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
	
	//读取测试集文件路径，以及测试参数
	public void readFileArgs()
	{
		System.out.println("*****************输入测试文件路径********************");
		
		Scanner scanner = new Scanner(System.in);	//从控制台读取参数
		System.out.print("训练集中有标签流文件路径：");
		this.labelFilePath = scanner.nextLine();
		System.out.print("训练集中无标签流文件路径：");
		this.unlabelFilePath = scanner.nextLine();
		System.out.print("测试集文件路径：");
		this.testFilePath = scanner.nextLine();
		System.out.print("聚类簇数目：");
		this.numClusters = Integer.parseInt(scanner.nextLine());
		System.out.print("测试方法(0:改进的方法   1:Erman的方法)：");
		this.functionType = Integer.parseInt(scanner.nextLine());
		scanner.close();
	}
	
	//测试分类器
	public void testClassifier() throws FileNotFoundException, IOException,Exception{
		System.out.print("测试数据集是否采用原文作者提供数据集(Y/N)：");
		Scanner scanner = new Scanner(System.in);	//从控制台读取参数
		String tmpstr = scanner.nextLine();
		if(tmpstr.toLowerCase().equals("y")){
			this.readDataArgs();	//读取控制台输入参数，获取文件路径
		}
		else if(tmpstr.toLowerCase().equals("n")){
			this.readFileArgs();
		}
		else{
			System.out.println("输入有误！");
			return;
		}
		
		FlowsProClassifier flowsProClassifier = new FlowsProClassifier();		
		flowsProClassifier.setLabelInsts(flowsProClassifier.getFileInstances(this.labelFilePath));	//读取有标签流数据集
		flowsProClassifier.setUnlabelInsts(flowsProClassifier.getFileInstances(this.unlabelFilePath));	//读取无标签流数据集
		flowsProClassifier.preserveOriginLable(flowsProClassifier.getLabelInsts(),flowsProClassifier.getUnlabelInsts());	//保存训练集的原始流标签
		if(this.functionType == 0){	//我们的方法有流标签增殖，Erman的方法没有
			flowsProClassifier.flowLabelPropagation();
		}
		flowsProClassifier.setNumPreClusters(this.numClusters);	//设置聚类簇数目
		System.out.println("正在进行分类测试。。。。。。");
		flowsProClassifier.constructNCC();	//构造分类器
		flowsProClassifier.evaluateClassifier(flowsProClassifier.getFileInstances(this.testFilePath));	//用分类器分类测试数据集，等到分类结果
		
	}
	
}
