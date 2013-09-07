/**
 * 
 */
package constants;

/**
 * @author Siddhi Chogle, Richil Bhalerao
 *
 */
public class Constants {
	
	public static final String PERCENT = "%";
	
	public static final String QUOTE = "'";
	
	public static final String LIKE = " LIKE ";
	
	public static final String EQUALS = " = ";
	
	public static final String AND = " AND ";
	
	public static final String WHERE = " WHERE ";
	
	public static final long PREMIUM_CUST_ID_START=1000000000L;
	
	public static final long PREMIUM_CUST_ID_END=4999999999L;
	
	public static final long SIMPLE_CUST_ID_END=9999999999L;
	
	public static final class SQLStrings{
		
		public static final String CHECK_MOVIE_RENTED_SQL = "SELECT COUNT(MOVIE_ID) FROM VIDEO_LIBRARY_SYSTEM.RENT_MOVIE WHERE MOVIE_ID = ?";
		
		public static final String DELETE_MOVIE_SQL = "DELETE FROM VIDEO_LIBRARY_SYSTEM.MOVIE WHERE MOVIE_ID = ?";
	
		public static final String SEARCH_MOVIE_SQL = "SELECT MOVIE_ID, NAME, BANNER, RELEASE_DATE, RENT_AMT, CATEGORY, AVAILABLE_COPIES " +
			"FROM VIDEO_LIBRARY_SYSTEM.MOVIE WHERE ";
		
		public static final String SEARCH_SIMPLE_CUSTOMERS_SQL = "SELECT MEMBERSHIP_ID, FNAME, LNAME, ADDRESS, CITY, STATE, ZIPCODE, EMAIL" +
			"FROM VIDEO_LIBRARY_SYSTEM.SIMPLE_CUSTOMER_DETAILS WHERE ";
		
		public static final String SEARCH_PREMIUM_CUSTOMERS_SQL = "SELECT MEMBERSHIP_ID, FNAME, LNAME, ADDRESS, CITY, STATE, ZIPCODE, EMAIL" +
			"FROM VIDEO_LIBRARY_SYSTEM.PREMIUM_CUSTOMER_DETAILS WHERE ";
		
		public static final String SEARCH_ALL_CUSTOMERS_SQL = "SELECT MEMBERSHIP_ID, FNAME, LNAME, ADDRESS, CITY, STATE, ZIPCODE, EMAIL " +
			"FROM VIDEO_LIBRARY_SYSTEM.SIMPLE_CUSTOMER_DETAILS, VIDEO_LIBRARY_SYSTEM.PREMIUM_CUSTOMER_DETAILS WHERE ";
		
		public static final String SELECT_MOVIE_STMT = "Select rent_amt, available_copies from Movie M where M.movie_id=?";
	
		public static final String SELECT_CUSTOMER_STMT = "Select membership_id, fname, lname, address, city, state, zipcode, ssn, email from %s";
		
		public static final String SELECT_CUSTOMER_DUES = "select fee, outstanding_movies from customer_dues where membership_id=?";
		
		public static final String INSERT_RENT_MOVIE = "Insert into RENT_MOVIE value(?,?,?,?,NOW(),(NOW() + INTERVAL 7 DAY))";
		
		public static final String UPDATE_CUST_DUES = "Update Customer_Dues set fee=?, outstanding_movies=? where membership_id=?";
		
		public static final String INSERT_CUST_DUES = "Insert into Customer_Dues values(?,?,?,?)";
		
		public static final String CREATE_MOVIE_STMT="Insert into Movie(name,banner,release_date,rent_amt,category,available_copies) values(?,?,?,?,?,?);";
		
		public static final String SELECT_MOVIE_INFO="select A.name as movieName, B.issued_date as issuedDate, B.due_date as dueDate from video_library_system.movie A inner join video_library_system.rent_movie B where A.id=B.movie_id  and B.membership_id=?;";
		
		public static final String UPDATE_MOVIE_INFO="update Movie set ";
		
		public static final String INSERT_BILL_INFO="INSERT INTO BILL (MEMBERSHIP_ID,BILL_AMT,FINE) VALUES (?,?,?)";
		
		public static final String GET_MOVIE_DETAILS_SQL = "SELECT C.MEMBERSHIP_ID,C.FEE,C.FINE,C.OUTSTANDING_MOVIES,R.MOVIE_ID,M.NAME FROM CUSTOMER_DUES C INNER JOIN RENT_MOVIE R, RENT_MOVIE R1 INNER JOIN Movie M  WHERE C.MEMBERSHIP_id = ? and C.MEMBERSHIP_id = R.MEMBERSHIP_id and R1.MOVIE_ID=M.ID";
		
		public static final String RESET_FINE="UPDATE CUSTOMER_DUES SET FINE=0 WHERE MEMBERSHIP_ID = ? ";
		
		public static final String CREATE_SIMPLE_CUST_SQL = "INSERT INTO SIMPLE_MEMBER_DETAILS (FNAME,LNAME,ADDRESS,CITY,STATE,ZIPCODE,SSN,EMAIL) VALUES (?,?,?,?,?,?,?,?,?)";
		
		public static final String CREATE_PREMIUM_CUST_SQL = "INSERT INTO PREMIUM_MEMBER_DETAILS (FNAME,LNAME,ADDRESS,CITY,STATE,ZIPCODE,SSN,EMAIL) VALUES (?,?,?,?,?,?,?,?,?)";
		
		public static final String SEARCH_RENTED_MOVIE_SQL = "SELECT MEMBERSHIP_ID,FNAME,LNAME,DUE_DATE FROM RENT_MOVIE WHERE MOVIE_ID = ?";

	}
	
	
	public static final class Movie{
		
		public static final String MOVIE_ID = "MOVIE_ID";
	
		public static final String NAME = "NAME";
		
		public static final String BANNER = "BANNER";
		
		public static final String RELEASE_DATE = "RELEASE_DATE";
		
		public static final String RENT_AMT = "RENT_AMT";
		
		public static final String CATEGORY = "CATEGORY";
		
		public static final String AVAILABLE_COPIES = "AVAILABLE_COPIES";
		
	}
	
	public static final class Customer{
		
		public static final String MEMBERSHIP_ID = "MEMBERSHIP_ID";
	
		public static final String FNAME = "FNAME";
		
		public static final String LNAME = "LNAME";
		
		public static final String ADDRESS = "ADDRESS";
		
		public static final String CITY = "CITY";
		
		public static final String STATE = "STATE";
		
		public static final String ZIPCODE = "ZIPCODE";
		
		public static final String SSN = "SSN";
		
		public static final String EMAIL = "EMAIL";
		
	}
}
