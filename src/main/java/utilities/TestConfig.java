package utilities;
public class TestConfig{


	public static String server="smtp.gmail.com";
	public static String from = "testautokasuni@gmail.com";
	public static String password = "bucs ioqc hxwa tzle";
	public static String[] to ={"kasunihasitha@gmail.com"};
	public static String subject = "Test Report";
	public static String messageBody ="TestMessage";
	public static String attachmentPath=System.getProperty("user.dir")+"//Reports.zip";
	public static String attachmentName="reports.zip";


	//SQL DATABASE DETAILS
	public static String driver="net.sourceforge.jtds.jdbc.Driver";
	public static String dbConnectionUrl="jdbc:jtds:sqlserver://192.101.44.22;DatabaseName=monitor_eval";
	public static String dbUserName="sa";
	public static String dbPassword="$ql$!!1";


	//MYSQL DATABASE DETAILS
	public static String mysqldriver="com.mysql.cj.jdbc.Driver";
	public static String mysqluserName = "root";
	public static String mysqlpassword = "selenium";
	public static String mysqlurl = "jdbc:mysql://localhost:3306/batchoct2021";
	
	
}
