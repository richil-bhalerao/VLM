/**
 * 
 */
package builders;

import beans.Customer;
import beans.Movie;

/**
 * @author Siddhi Chogle
 *
 */
public class BeanBuilder {
	private Movie movie;
	private Customer customer;
	
	public Movie buildMovieBean(Integer movieId, String movieName, String movieBanner, String category, String releaseDate,
			Integer availableCopies, Float rentAmt, String issuedDate, String dueDate){
		movie = new Movie();
		movie.setMovieId(movieId);
		movie.setMovieName(movieName);
		movie.setMovieBanner(movieBanner);
		movie.setCategory(category);
		movie.setReleaseDate(releaseDate);
		movie.setAvailableCopies(availableCopies);
		movie.setRentAmt(rentAmt);
		movie.setIssuedDate(issuedDate);
		movie.setDueDate(dueDate);
		return movie;
	}
	
	public Customer buildCustomerBean(Long membership_id, String fname, String lname, String address, String city, 
			String zipCode, String password, Long ssn, String email, Boolean premium, Integer outstanding){
		customer = new Customer();
		customer.setMembership_id(membership_id);
		customer.setFname(fname);
		customer.setLname(lname);
		customer.setAddress(address);
		customer.setCity(city);
		customer.setZipCode(zipCode);
		customer.setPassword(password);
		customer.setSsn(ssn);
		customer.setEmail(email);
		customer.setPremium(premium);
		customer.setOutstanding(outstanding);
		return customer;
	}

}
