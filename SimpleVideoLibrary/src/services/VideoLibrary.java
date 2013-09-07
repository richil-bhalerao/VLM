/**
 * 
 */
package services;

import javax.jws.WebService;

import beans.Bill;
import beans.Customer;
import beans.Movie;

/**
 * @author Siddhi Chogle, Richil Bhalerao, Anand Shenoy, Ameya Barsode, Nikhil Patil
 *
 */

@WebService
public class VideoLibrary {
	
	DbOperations db = DbOperations.getInstance();
	
	/*
	 * Deletes selected movie
	 */
	public boolean deleteMovie(int movieId) throws Exception{
		return db.deleteMovie(movieId);
	}
	
	/*
	 * Searches movies based on the attributes selected. 
	 */
	public Movie[] searchMovie(Movie movie){
		return db.searchMovie(movie);
	}
	
	/*
	 * Searches movies based on the attributes selected. 
	 */
	public Customer[] searchCustomer(Customer customer){
		return db.searchCustomer(customer);
	}
	
	/* Service descriptions:
	 * 1) Check restriction limit (Premium or Simple). 
	 * 2) Check if copy of the movie is available.
	 * 3) Add a record in RENT_MOVIE table. 
	 * 4) Update fees if there exits a record in the CUSTOMER_DUES table else insert record. */
	public String issueMovie(long userId, long movieId, String fname, String lname) throws Exception {
		return db.issueMovie(userId, movieId, fname, lname);
	}
	
	/*Service description:
	 * Return a list of customers (either premium or simple) as specified by the parameter. */
	public Customer[] listCustomers(boolean isPremium){
		return db.listCustomers(isPremium);
	}
	
	/* Service description:
	 *  Return an array of all the customers in the system. */
	public Customer[] listAllCustomers(){
		return db.listAllCustomers();
	}
	
	/*
	 * Creates new movie. 
	 */
	public boolean createMovie(Movie m){
		return db.createMovie(m);
	}
	
	/*
	 * Get information of movies issued by member. 
	 */
	public Movie[] displayMemberInfo(Long membership_id){
		return db.displayMemberInfo(membership_id);
	}
	
	/*
	 * Updates movie information. 
	 */
	public boolean updateMovieInfo(Movie m){
		return db.updateMovieInfo(m);
	}
	
	/*
	 * Deletes selected customer. 
	 */
	public boolean deleteCustomer(Customer customer){
		return db.deleteCustomer(customer);
	}
	
	/*
	 * Updates customer information.
	 */
	public boolean updateCustomer(Customer customer){
		return db.updateCustomer(customer);
	}
	
	/*
	 * Return issued movie.
	 */
	public boolean returnMovie(Customer customer, Movie movie){
		return db.returnMovie(customer, movie);
	}
	
	/*
	 * Create new simple customer.
	 */
	public boolean createSimpleCustomer(Customer customer){
		return db.createSimpleCustomer(customer);
	}
	
	/*
	 * Create new premium customer.
	 */
	public boolean createPremiumCustomer(Customer customer){
		return db.createPremiumCustomer(customer);
	}
	
	/*
	 * Get list of customers associated to a movie.
	 */
	public Customer[] getMovieCustInfo(int movieId){
		return db.getMovieCustInfo(movieId);
	}
	
	/*
	 * Generate Bill.
	 */
	public Bill generateBill(Customer customer, boolean isFinalBill ){
		return db.generateBill(customer, isFinalBill);
	}


}
