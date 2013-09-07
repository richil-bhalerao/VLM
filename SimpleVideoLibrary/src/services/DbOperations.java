/**
 * 
 */
package services;

import static constants.Constants.SQLStrings.CREATE_MOVIE_STMT;
import static constants.Constants.SQLStrings.INSERT_CUST_DUES;
import static constants.Constants.SQLStrings.INSERT_RENT_MOVIE;
import static constants.Constants.SQLStrings.SELECT_CUSTOMER_DUES;
import static constants.Constants.SQLStrings.SELECT_CUSTOMER_STMT;
import static constants.Constants.SQLStrings.SELECT_MOVIE_INFO;
import static constants.Constants.SQLStrings.SELECT_MOVIE_STMT;
import static constants.Constants.SQLStrings.UPDATE_CUST_DUES;
import static constants.Constants.SQLStrings.UPDATE_MOVIE_INFO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import beans.Bill;
import beans.Customer;
import beans.Movie;
import builders.BeanBuilder;
import builders.SqlBuilder;
import constants.Constants;



/**
 * @author Siddhi Chogle, Richil Bhalerao, Anand Shenoy, Ameya Barsode, Nikhil Patil
 *
 */
public class DbOperations {
	private static final DbOperations db = new DbOperations();
	private Connection con;
	private Statement stmt;
	private PreparedStatement prepStmt;
	private ResultSet rs;
	
	private DbOperations(){
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/video_library_system", "root", "root");
			if(!con.isClosed()){
				System.out.println("Successfully connected to DB");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static DbOperations getInstance(){
		return db;
	}
	
	public Connection getConnection(){
		return con;
	}
	
	public boolean deleteMovie(int movieId) throws Exception{
		boolean result = false;
		try {
			prepStmt = con.prepareStatement(Constants.SQLStrings.CHECK_MOVIE_RENTED_SQL);
			prepStmt.setInt(1, movieId);
			rs = prepStmt.executeQuery();
			if(rs.next()){
				throw new Exception("Movie currently rented, cannot be deleted.");
			}
			
			prepStmt = con.prepareStatement(Constants.SQLStrings.DELETE_MOVIE_SQL);
			prepStmt.setInt(1, movieId);
			int numOfRowsdeleted = prepStmt.executeUpdate();
			if(numOfRowsdeleted > 0){
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Movie[] searchMovie(Movie movie){
		List<Movie> moviesList = new ArrayList<Movie>();
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(new SqlBuilder().buildSearchMovieSql(movie));
			while(rs.next()){
				moviesList.add(new BeanBuilder().buildMovieBean(rs.getInt(Constants.Movie.MOVIE_ID), rs.getString(Constants.Movie.NAME), 
						rs.getString(Constants.Movie.BANNER), rs.getString(Constants.Movie.CATEGORY), rs.getDate(Constants.Movie.RELEASE_DATE).toString(), 
						rs.getInt(Constants.Movie.AVAILABLE_COPIES), rs.getFloat(Constants.Movie.RENT_AMT), null, null));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return moviesList.toArray(new Movie[moviesList.size()]);
	}
	
	public Customer[] searchCustomer(Customer customer){
		List<Customer> customersList = new ArrayList<Customer>();
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(new SqlBuilder().buildSearchCustomerSql(customer));
			while(rs.next()){
				customersList.add(new BeanBuilder().buildCustomerBean(rs.getLong(Constants.Customer.MEMBERSHIP_ID), rs.getString(Constants.Customer.FNAME), 
						rs.getString(Constants.Customer.LNAME), rs.getString(Constants.Customer.ADDRESS), rs.getString(Constants.Customer.CITY), 
						rs.getString(Constants.Customer.ZIPCODE), null, null, rs.getString(Constants.Customer.EMAIL), null, null));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return customersList.toArray(new Customer[customersList.size()]);
	}
	
	/* Service descriptions:
	 * 1) Check restriction limit (Premium or Simple). 
	 * 2) Check if copy of the movie is available.
	 * 3) Add a record in RENT_MOVIE table. 
	 * 4) Update fees if there exists a record in the CUSTOMER_DUES table else insert record. 
	 * 5) Update Movie record to subtract no. of available copies by 1.*/
	@SuppressWarnings("resource")
	public String issueMovie(long userId, long movieId, String fname, String lname) throws Exception
	{
		double rentAmt = 0.0, fee = 0.0; 
		int availableCopies = 0, outstandingMovies = 0;
		boolean isPremium, custDueFound = false;
		PreparedStatement pStmt;
		ResultSet rs;
		Savepoint spt1 = null;
		
		try 
		{
			/*----------------Check restriction condition------------------*/
			pStmt = con.prepareStatement(SELECT_CUSTOMER_DUES);
			pStmt.setLong(1, userId);
			rs = pStmt.executeQuery();
			
			//Check restriction condition only if record is present in Customer_Dues table.
			if (rs.next())
			{
				fee = rs.getDouble("fee");
				outstandingMovies = rs.getInt("outstanding_movies");
				custDueFound = true;
		
				//Decide whether user is a premium or simple.
				isPremium = (userId <= Constants.PREMIUM_CUST_ID_END);
				
				//Check restriction condition based on type of customer.
				if ((isPremium) && (outstandingMovies == 10)) 
					return "false: Cannot issue movie - Premium users are allowed to rent only 10 movies at a time.";
				else if (!(isPremium) && (outstandingMovies == 2))
					return "false: Cannot issue movie - Simple users are allowed to rent only 2 movies at a time.";
			}
			
			/*----------------Check if movie is available------------------*/
			pStmt = con.prepareStatement(SELECT_MOVIE_STMT);
			pStmt.setLong(1, movieId);
			rs = pStmt.executeQuery();
			if (rs.next()) //Should return only one unique record.
			{
				rentAmt = rs.getDouble("rent_amt");
				availableCopies = rs.getInt("available_copies");
			}
			else //Movie does not exist in the database.
				return "false: Requested movie does not exist.";
			
			//if no copies are available raise error.
			if (!(availableCopies > 0))
				return "false: Copies of requested movie are not available.";
		
			
			/*----------------Insert a record into RENT_MOVIE----------------*/
			
			//Set AutoCommit to false and store Savepoint. 
			con.setAutoCommit(false);
			spt1 = con.setSavepoint("svpt1");
			
			pStmt = con.prepareStatement(INSERT_RENT_MOVIE);
			pStmt.setLong(1, userId);
			pStmt.setLong(2, movieId);
			pStmt.setString(3, fname);
			pStmt.setString(4, lname);
			//issued_date and due_date are specified in query.
			
			int rowcount = pStmt.executeUpdate();
			if (!(rowcount > 0))
				return "false: Insert of RENT_MOVIE record failed";
			
			/*----------------Insert/update Customer_Dues table---------------*/
			if (custDueFound)
			{
				pStmt = con.prepareStatement(UPDATE_CUST_DUES);
				pStmt.setDouble(1, (fee + rentAmt)); //Existing and current rent will be added.
				pStmt.setDouble(2, (outstandingMovies + 1));
				pStmt.setLong(3, userId);
				rowcount = pStmt.executeUpdate();
				if (!(rowcount > 0))
					return "false: Update of Customer_Dues record failed";
			}
			else
			{
				pStmt = con.prepareStatement(INSERT_CUST_DUES);
				pStmt.setLong(1, userId);
				pStmt.setDouble(2, (fee + rentAmt));
				pStmt.setDouble(3, 0.0);
				pStmt.setDouble(4, (outstandingMovies + 1));
				rowcount = pStmt.executeUpdate();
				if (!(rowcount > 0))
					return "false: Insert of Customer_Dues record failed";
			}
			
			/*----------------Update count of available_copies of Movie record---------------*/
			pStmt = con.prepareStatement(UPDATE_MOVIE_INFO + " available_copies = ? where movie_id = ?");
			pStmt.setInt(1, (availableCopies - 1));   //subtract available copies by 1.
			pStmt.setLong(2, movieId);
			pStmt.execute();
			
			/*---------------Finally commit all insert/update transactions----------*/
		    con.commit();
			return "true";
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			con.rollback(spt1);
			return "false: Error occurred while issuing movie.";
		}
	}
	
	/*Service description:
	 * Return a list of customers (either premium or simple) as specified by the parameter. */
	public Customer[] listCustomers(boolean isPremium)
	{
		try
		{
			PreparedStatement pStmt;
			//Decide what is to be retrieved - premium or simple.
			if (isPremium)
				pStmt = con.prepareStatement(String.format(SELECT_CUSTOMER_STMT, "premium_member_details"));
			else
				pStmt = con.prepareStatement(String.format(SELECT_CUSTOMER_STMT, "simple_customer_details"));	
			
			ResultSet rs = pStmt.executeQuery();
			
			//Go to the last record to get no. of rows retrieved.
			rs.last();
			Customer[] cArray = new Customer[rs.getRow()];
			rs.beforeFirst();
			
			int i = 0;
			while(rs.next())
			{	
				Customer c = new Customer();
				
				c.setMembership_id(rs.getLong("membership_id"));
				c.setFname(rs.getString("fname"));
				c.setLname(rs.getString("lname"));
				c.setAddress(rs.getString("address"));
				c.setCity(rs.getString("city"));
				c.setState(rs.getString("state"));
				c.setZipCode(rs.getString("zipcode"));
				c.setEmail(rs.getString("email"));
				c.setPremium(isPremium);
				
				cArray[i] = c;
				i++;
			}
			
			//Return an array of customers thus retrieved.
			return cArray;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/* Service description:
	 *  Return an array of all the customers in the system. */
	public Customer[] listAllCustomers()
	{
		try
		{
			PreparedStatement pStmt;
			String str = String.format(SELECT_CUSTOMER_STMT, "premium_member_details") + " union " + String.format(SELECT_CUSTOMER_STMT, "simple_customer_details");
			pStmt = con.prepareStatement(str);
			
			ResultSet rs = pStmt.executeQuery();
			
			//Go to the last record to get no. of rows retrieved.
			rs.last();
			Customer[] cArray = new Customer[rs.getRow()];
			rs.beforeFirst();
			
			int i = 0;
			while(rs.next())
			{	
				Customer c = new Customer();
				
				c.setMembership_id(rs.getLong("membership_id"));
				c.setFname(rs.getString("fname"));
				c.setLname(rs.getString("lname"));
				c.setAddress(rs.getString("address"));
				c.setCity(rs.getString("city"));
				c.setState(rs.getString("state"));
				c.setZipCode(rs.getString("zipcode"));
				c.setEmail(rs.getString("email"));
				
				//Set premium or non premium based on MembershipId.
				if (rs.getLong("membership_id") <= Constants.PREMIUM_CUST_ID_END)
					c.setPremium(true);
				else
					c.setPremium(false);
				
				cArray[i] = c;
				i++;
			}
			
			//Return an array of customers thus retrieved.
			return cArray;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean createMovie(Movie m)
	{
		
		try 
		{
			int rowcount=0;
			PreparedStatement insert_stmt= con.prepareStatement(CREATE_MOVIE_STMT);
			insert_stmt.setString(1, m.getMovieName());
			insert_stmt.setString(2, m.getMovieBanner());
			insert_stmt.setString(3, m.getReleaseDate());
			insert_stmt.setFloat(4, m.getRentAmt());
			insert_stmt.setString(5, m.getCategory());
			insert_stmt.setInt(6, m.getAvailableCopies());
			rowcount=insert_stmt.executeUpdate();
			if(rowcount>0)
			return true;
			else
				return false;
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return false;
		}
		

	}

	public Movie[] displayMemberInfo(Long membership_id)
	{
		int rowcount=0;
		PreparedStatement select_stmt;
		Movie[] m;
		try
		{
			select_stmt= con.prepareStatement(SELECT_MOVIE_INFO);
			select_stmt.setLong(1,membership_id);
			ResultSet rs=select_stmt.executeQuery();
			if(rs.last())
				rowcount=rs.getRow();
			m= new Movie[rowcount];
			rowcount=0;
			rs.beforeFirst();
			while(rs.next())
			{
				m[rowcount].setMovieName(rs.getString("movieName"));
				m[rowcount].setIssuedDate(rs.getString("issuedDate"));
				m[rowcount].setDueDate(rs.getString("dueDate"));
				rowcount++;
			}
				return m;
		}
		catch (SQLException e) 
		{
			    System.out.println("Could not retrieve movie info for this member");
			    e.printStackTrace();
			    return null;
		}
		
	}
	
	private String trimCommas(String str)
	{
		if(str.endsWith(","))
			return str.substring(0, (str.length()-1));
		else
			return str;
	}
	
	public boolean updateMovieInfo(Movie m)
	{
		PreparedStatement update_stmt;
		String setStmt="",whereClause="";
		
		if(m.getMovieName()!="")
			setStmt=setStmt+"name='"+m.getMovieName()+"',";
		if(m.getMovieBanner()!="")
			setStmt=setStmt+"banner='"+m.getMovieBanner()+"',";
		if(m.getReleaseDate()!="")
			setStmt=setStmt+"release_date='"+m.getReleaseDate()+"',";
		if(m.getRentAmt()!=0)
			setStmt=setStmt+"rent_amt='"+m.getRentAmt()+"',";
		if(m.getCategory()!="")
			setStmt=setStmt+"category='"+m.getCategory()+"',";
		if(m.getAvailableCopies()!=0)
			setStmt=setStmt+"available_copies='"+m.getAvailableCopies()+"',";
		
		setStmt=trimCommas(setStmt);
		
		if(m.getMovieId()!=null)
			whereClause=" where movie_id='"+m.getMovieId()+"'";
		
		try 
		{
			update_stmt=con.prepareStatement(UPDATE_MOVIE_INFO+setStmt+whereClause);
			System.out.println("update statement "+(UPDATE_MOVIE_INFO+setStmt+whereClause));
			int rowcount= update_stmt.executeUpdate();
			System.out.println(""+rowcount+" updated successfully");
			return true;
		} 
		catch (SQLException e) 
		{
		    System.out.println("Update Failed");	
			e.printStackTrace();
			return false;
		}
		
	}
	
	public boolean deleteCustomer(Customer customer){

		boolean result = false;
		Connection con;

		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection("jdbc:mysql://localhost/video_library_system", "root", "root");
			PreparedStatement pstmt, pstmt1 ;
			int fine=0, outStandingMovies=0;


			pstmt1 = con.prepareStatement("select fine, outstanding_movies from customer_dues where membership_id = ?");
			pstmt1.setLong(1, customer.getMembership_id());
			ResultSet rs = pstmt1.executeQuery();
			while(rs.next()){
				fine= rs.getInt("fine");
				outStandingMovies= rs.getInt("outstanding_movies");
			}

			if(fine>0 || outStandingMovies >0){

				result = false;
			}
			else{

				if(customer.getMembership_id() < 5000000000l)
					pstmt = con.prepareStatement("delete from simple_customer_details where membership_id = ?");
				else
					pstmt = con.prepareStatement("delete from premium_member_details where membership_id = ? ");

				pstmt.setLong(1, customer.getMembership_id());
				result = pstmt.execute();

			}

		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return false;
		}

		return result;
	}
	
	public boolean updateCustomer(Customer customer){

		Customer oldCustomer;
		boolean result=false;
		Connection con;

		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection("jdbc:mysql://localhost/video_library_system", "root", "root");
			PreparedStatement pstmt, pstmt1 ;
/*	
 *  To compare the old and new customer values
			if(customer.getMembership_id() < 5000000000l)
				pstmt = con.prepareStatement("select * from simple_customer_details where membership_id = ?");
			else
				pstmt = con.prepareStatement("select * from premium_member_details where membership_id = ? ");

			pstmt.setLong(1, customer.getMembership_id());
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()){
				
				
			}

*/

			if(customer.getMembership_id() < 5000000000l)
				pstmt1 = con.prepareStatement("update simple_customer_details set fname = ? , lname = ? , address = ? , city =? , state = ? , zipcode = ? , " +
						" password = ? , ssn = ? , zip = ? where membership_id = ?");
			else
				pstmt1 = con.prepareStatement("delete from premium_member_details set fname = ? , lname = ? , address = ? , city =? , state = ? , zipcode = ? , " +
						" password = ? , ssn = ? , email = ? where membership_id = ?");

			int  i =1;
			pstmt1.setString(i, customer.getFname());
			pstmt1.setString(++i, customer.getLname());
			pstmt1.setString(++i, customer.getAddress());
			pstmt1.setString(++i, customer.getCity());
			pstmt1.setString(++i, customer.getState());
			pstmt1.setInt(++i, Integer.parseInt(customer.getZipCode()));
			pstmt1.setString(++i, customer.getPassword());
			pstmt1.setLong(++i, customer.getSsn());
			pstmt1.setString(++i, customer.getEmail());
			pstmt1.setLong(++i, customer.getMembership_id());
			
			result = pstmt1.execute();


		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}



		return result;

	}
	
	public boolean returnMovie(Customer customer, Movie movie){
		Connection con;
		int finePerDay = 2; // 2$ of fine is charged for each late day
		int fine = 0;
		int oldFine = 0, outstandingMovies = 0, availableCopies=0;
		boolean result = false;
		java.sql.Date sqlDate = new java.sql.Date(0);
		java.util.Date due_date = new java.util.Date();
		java.util.Date currentDate= new java.util.Date();

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection("jdbc:mysql://localhost/video_library_system", "root", "root");
			PreparedStatement pstmt = con.prepareStatement("select * from rent_movie where membership_id = ? and movie_id = ?");
			pstmt.setLong(1, customer.getMembership_id() );
			pstmt.setInt(2, movie.getMovieId());
			ResultSet rs= pstmt.executeQuery();

			while(rs.next()){

				sqlDate = rs.getDate("issued_date");
				//	issued_date = new java.util.Date(sqlDate.getTime());
				sqlDate= rs.getDate("due_date");
				due_date = new java.util.Date(sqlDate.getTime());

			}

			int diffInDays = (int) ((currentDate.getTime() - due_date.getTime())/ (1000 * 60 * 60 * 24));

			PreparedStatement pstmt2 = con.prepareStatement("select fine, outstanding_movies from customer_dues where membership_id = ?");
			pstmt2.setLong(1, customer.getMembership_id());
			ResultSet rs2 = pstmt2.executeQuery();			

			while(rs2.next()){
				oldFine = rs2.getInt("fine");
				outstandingMovies = rs2.getInt("outstanding_movies");
			}

			outstandingMovies--;
			if(diffInDays > 0){
				fine =  (diffInDays * finePerDay);
			}
			fine = fine + oldFine;

			PreparedStatement pstmt3 = con.prepareStatement("update customer_dues set fine = ? , outstanding_movies=? where membership_id=?");
			pstmt3.setInt(1, fine);
			pstmt3.setInt(2, outstandingMovies);
			pstmt3.setLong(3, customer.getMembership_id());
			pstmt3.execute();


			PreparedStatement pstmt4 = con.prepareStatement("delete from rent_movie where membership_id=? and movie_id = ?");

			pstmt4.setLong(1, customer.getMembership_id() );
			pstmt4.setInt(2, movie.getMovieId());
			result = pstmt4.execute();

			PreparedStatement pstmt5 = con.prepareStatement("select available_copies from movie where id = ?");
			pstmt5.setLong(1, movie.getMovieId());
			ResultSet rs3 = pstmt2.executeQuery();	

			while(rs3.next()){
				availableCopies = rs3.getInt("available_copies");

			}
			availableCopies++;

			PreparedStatement pstmt6 = con.prepareStatement("update movie set available_copies = ? where id = ?");
			pstmt6.setInt(1, availableCopies);
			pstmt6.setInt(2, movie.getMovieId());
			pstmt6.execute();

			con.close();

		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}



		return result;
	}
	
	public boolean createSimpleCustomer(Customer customer){
		boolean result = false;
		try {
			prepStmt = con.prepareStatement(Constants.SQLStrings.CREATE_SIMPLE_CUST_SQL);
			prepStmt.setString(1, customer.getFname());
			prepStmt.setString(2, customer.getLname());
			prepStmt.setString(3, customer.getAddress());
			prepStmt.setString(4, customer.getCity());
			prepStmt.setString(5, customer.getState());
			prepStmt.setString(6, customer.getZipCode());
			prepStmt.setString(7, customer.getPassword());
			prepStmt.setLong(8, customer.getSsn());
			prepStmt.setString(9, customer.getEmail());
			int numOfRowsdeleted = prepStmt.executeUpdate();
			if(numOfRowsdeleted > 0){
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean createPremiumCustomer(Customer customer){
		boolean result = false;
		try {
			prepStmt = con.prepareStatement(Constants.SQLStrings.CREATE_PREMIUM_CUST_SQL);
			prepStmt.setString(1, customer.getFname());
			prepStmt.setString(2, customer.getLname());
			prepStmt.setString(3, customer.getAddress());
			prepStmt.setString(4, customer.getCity());
			prepStmt.setString(5, customer.getState());
			prepStmt.setString(6, customer.getZipCode());
			prepStmt.setString(7, customer.getPassword());
			prepStmt.setLong(8, customer.getSsn());
			prepStmt.setString(9, customer.getEmail());
			int numOfRowsdeleted = prepStmt.executeUpdate();
			if(numOfRowsdeleted > 0){
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public Customer[] getMovieCustInfo(int movieId){
		List<Customer> custList = new ArrayList<Customer>();
		/*
		Movie[] movieInfo=searchMovie(movie);
		int i=0;
		String membership_Id= "" ,fname= "",lname= "" ,dueDate= "";
		for(;i<movieInfo.length;i++){
			result[i]=movieInfo[i].toString();
		}
		*/
		try {
		prepStmt = con.prepareStatement(Constants.SQLStrings.SEARCH_RENTED_MOVIE_SQL);
		prepStmt.setInt(1,movieId);
		rs=prepStmt.executeQuery();
		while(rs.next()){
			custList.add(new BeanBuilder().buildCustomerBean(rs.getLong(Constants.Customer.MEMBERSHIP_ID), rs.getString(Constants.Customer.FNAME), rs.getString(Constants.Customer.LNAME), null, null, null, null, null, null, null, null));
			
		}
		
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return custList.toArray(new Customer[custList.size()]);
	}
	
	public Bill generateBill(Customer customer, boolean isFinalBill ){
		Bill bill=new Bill();

		//in the middle of the month
		//if(customer.getMembership_id()> Constants.SIMPLE_CUST_ID_START && customer.getMembership_id()<Constants.SIMPLE_CUST_END){
		if(customer.getPremium() && !(isFinalBill)){
			
			String[] movieNames=new String[10];
			int i=0;
	
	try {
		prepStmt = con.prepareStatement(Constants.SQLStrings.GET_MOVIE_DETAILS_SQL);
		prepStmt.setLong(1,customer.getMembership_id());
		rs=prepStmt.executeQuery();
		//SELECT MEMBERSHIP_ID,FEE,FINE,OUTSTANDING_MOVIES FROM CUSTOMER_DUES WHERE MEMBERSHIP_id = ? ";
		
		rs.next();
		bill.setMembership_Id(rs.getLong(Constants.Customer.MEMBERSHIP_ID));
		bill.setTotalFee(rs.getInt("FEE"));
		bill.setFine(rs.getInt("FINE"));
		bill.setOutstanding(rs.getInt("OUTSTANDING_MOVIES"));
		//	custList.add(new BeanBuilder().buildCustomerBean(rs.getLong(Constants.Customer.MEMBERSHIP_ID), rs.getString(Constants.Customer.FNAME), rs.getString(Constants.Customer.LNAME), null, null, null, null, null, null, null, null));
		
		movieNames[i++]=rs.getString("NAME");	
		while(rs.next()){
			movieNames[i]=rs.getString("NAME");
			i++;
		}
			bill.setMovieNames(movieNames);
	}catch (NumberFormatException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
	
			return bill;
			
		} //At the end of the month
		else if(customer.getPremium() && (isFinalBill)) {
			
			

			//in the middle of the month
			//if(customer.getMembership_id()> Constants.SIMPLE_CUST_ID_START && customer.getMembership_id()<Constants.SIMPLE_CUST_END){
			if(customer.getPremium() && !(isFinalBill)){
				
				String[] movieNames=new String[10];
				int i=0;
		
		try {
			prepStmt = con.prepareStatement(Constants.SQLStrings.GET_MOVIE_DETAILS_SQL);
			prepStmt.setLong(1,customer.getMembership_id());
			rs=prepStmt.executeQuery();
			//SELECT MEMBERSHIP_ID,FEE,FINE,OUTSTANDING_MOVIES FROM CUSTOMER_DUES WHERE MEMBERSHIP_id = ? ";
			
			rs.next();
			bill.setMembership_Id(rs.getLong(Constants.Customer.MEMBERSHIP_ID));
			bill.setTotalFee(rs.getInt("FEE"));
			bill.setFine(rs.getInt("FINE"));
			bill.setOutstanding(rs.getInt("OUTSTANDING_MOVIES"));
			//	custList.add(new BeanBuilder().buildCustomerBean(rs.getLong(Constants.Customer.MEMBERSHIP_ID), rs.getString(Constants.Customer.FNAME), rs.getString(Constants.Customer.LNAME), null, null, null, null, null, null, null, null));
			
			movieNames[i++]=rs.getString("NAME");	
			while(rs.next()){
				movieNames[i]=rs.getString("NAME");
				i++;
			}
				bill.setMovieNames(movieNames);
		}catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		int numOfRowsdeleted=0;
		
		try {
			prepStmt=con.prepareStatement(Constants.SQLStrings.RESET_FINE);
			prepStmt.setLong(1,customer.getMembership_id());
			
			numOfRowsdeleted = prepStmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(numOfRowsdeleted > 0){
			
		}
		
		return bill;
		
		
		
			
			
		}}// For simple Customer
		else{
			String[] movieNames=new String[5];
					int i=0;
			
			try {
				prepStmt = con.prepareStatement(Constants.SQLStrings.GET_MOVIE_DETAILS_SQL);
				prepStmt.setLong(1,customer.getMembership_id());
				rs=prepStmt.executeQuery();
				//SELECT MEMBERSHIP_ID,FEE,FINE,OUTSTANDING_MOVIES FROM CUSTOMER_DUES WHERE MEMBERSHIP_id = ? ";
				
				rs.next();
				bill.setMembership_Id(rs.getLong(Constants.Customer.MEMBERSHIP_ID));
				bill.setTotalFee(rs.getInt("FEE"));
				bill.setFine(rs.getInt("FINE"));
				bill.setOutstanding(rs.getInt("OUTSTANDING_MOVIES"));
				//	custList.add(new BeanBuilder().buildCustomerBean(rs.getLong(Constants.Customer.MEMBERSHIP_ID), rs.getString(Constants.Customer.FNAME), rs.getString(Constants.Customer.LNAME), null, null, null, null, null, null, null, null));
				
				movieNames[i++]=rs.getString("NAME");	
				while(rs.next()){
					movieNames[i]=rs.getString("NAME");
					i++;
				}
					bill.setMovieNames(movieNames);
				
				//MEMBERSHIP_ID ,BILL_AMT,FINE
				prepStmt=con.prepareStatement(Constants.SQLStrings.INSERT_BILL_INFO);
				prepStmt.setLong(1,customer.getMembership_id());
				prepStmt.setLong(2,rs.getInt("FEE"));
				prepStmt.setLong(2,rs.getInt("FINE"));
				int numOfRowsdeleted = prepStmt.executeUpdate();
				if(numOfRowsdeleted > 0){
					
				}
				
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			
		}
		
		return bill;
	}



}
