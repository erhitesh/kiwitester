package applicationComponent;

import java.io.IOException;

import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import com.session.DriverFactory;
import com.session.DriverSession;
import com.utilities.ExcelUtility;
import com.utilities.GlobalParam;

import action.CommonAll;
import testCaseReporting.SuiteReporting;
import testCaseReporting.TestCaseReporting;

public abstract class AppComponent2 {

	ExcelUtility exlUtil = new ExcelUtility();
	static SuiteReporting suitreporting;
	TestCaseReporting testCaseReporting;

	@BeforeSuite
	public void globalInitialize() {
		suitreporting = new SuiteReporting(GlobalParam.APP_NAME);
	}

	@BeforeTest
	@Parameters({ "browser" })
	public void intializer(final ITestContext test, String browser)
			throws Exception {
		DriverSession.setBrowserName(browser);
		DriverSession.setReporting(true);
		String testCaseName = test.getName().replaceAll(".*TC", "TC");
		DriverSession.setTestExcelPath(System.getProperty("user.dir")
				+ GlobalParam.slash + "TestRepository" + GlobalParam.slash
				+ "TestCases" + GlobalParam.slash + GlobalParam.APP_NAME
				+ GlobalParam.slash + testCaseName + ".ods");
		testCaseReporting = new TestCaseReporting(test.getName());
		DriverSession.setLastExecutionReportingInstance(testCaseReporting);
		System.out.println(SuiteReporting.pathToSuiteFolder + GlobalParam.slash
				+ "TestCase" + GlobalParam.slash + testCaseName + " ("
				+ GlobalParam.CURRENT_EXECUTION_MODE + ")_"
				+ DriverSession.getBrowserName() + ".html");
		testCaseReporting.header();
		try {
			DriverFactory df = new DriverFactory(
					GlobalParam.CURRENT_EXECUTION_MODE);
			DriverSession.setLastExecutionDriver(df.getDriver());
			exlUtil.fetchTestCaseData(1);
			GlobalParam.url = df.getUrl(GlobalParam.SERVER_NAME);
			DriverSession.setLastExecutionDriver(df.getDriver());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(test.getName());
	}

	@AfterTest
	public void afterTest() throws IOException {
		CommonAll common = new CommonAll(DriverSession.getLastExecutionDriver());
		common.undoTestChanges();
	}

	@AfterSuite
	public void reportSuite() {
		suitreporting.consolidateResultFooter();
	}
}