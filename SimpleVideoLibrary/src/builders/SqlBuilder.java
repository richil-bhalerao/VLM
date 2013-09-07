/**
 * 
 */
package builders;

import beans.Customer;
import beans.Movie;
import constants.Constants;

/**
 * @author Siddhi Chogle
 *
 */
public class SqlBuilder {
	private StringBuilder sqlBuilder;
	private boolean previousNotNull;
	
	//public String buildSearchMovieSql(String movieName, String movieBanner, String category, Integer availableCopies, 
	//		Float rentAmt, Date releaseDate){
	
	public String buildSearchMovieSql(Movie movie){
		sqlBuilder = new StringBuilder(Constants.SQLStrings.SEARCH_MOVIE_SQL);
		addStringPredicate(Constants.Movie.NAME, movie.getMovieName());
		addStringPredicate(Constants.Movie.BANNER, movie.getMovieBanner());
		addStringPredicate(Constants.Movie.CATEGORY, movie.getCategory());
		addStringPredicate(Constants.Movie.RELEASE_DATE, movie.getReleaseDate());
		addNonStringPredicate(Constants.Movie.RENT_AMT, movie.getRentAmt());
		addNonStringPredicate(Constants.Movie.AVAILABLE_COPIES, movie.getAvailableCopies());
		
		if(!previousNotNull){
			sqlBuilder.delete(sqlBuilder.indexOf(Constants.WHERE), sqlBuilder.length());
		}
		previousNotNull = false;
		return sqlBuilder.toString();
	}
	
	public String buildSearchCustomerSql(Customer customer){
		Boolean isPremium = customer.getPremium();
		if(isPremium == null){
			sqlBuilder = new StringBuilder(Constants.SQLStrings.SEARCH_ALL_CUSTOMERS_SQL);
		} else if(isPremium == Boolean.TRUE){
			sqlBuilder = new StringBuilder(Constants.SQLStrings.SEARCH_PREMIUM_CUSTOMERS_SQL);
		} else {
			sqlBuilder = new StringBuilder(Constants.SQLStrings.SEARCH_SIMPLE_CUSTOMERS_SQL);
		}
		
		addNonStringPredicate(Constants.Customer.MEMBERSHIP_ID, customer.getMembership_id());
		addStringPredicate(Constants.Customer.FNAME, customer.getFname());
		addStringPredicate(Constants.Customer.LNAME, customer.getLname());
		addStringPredicate(Constants.Customer.ADDRESS, customer.getAddress());
		addStringPredicate(Constants.Customer.CITY, customer.getCity());
		addStringPredicate(Constants.Customer.STATE, customer.getState());
		addStringPredicate(Constants.Customer.ZIPCODE, customer.getZipCode());
		addStringPredicate(Constants.Customer.EMAIL, customer.getEmail());
		
		if(!previousNotNull){
			sqlBuilder.delete(sqlBuilder.indexOf(Constants.WHERE), sqlBuilder.length());
		}
		previousNotNull = false;
		return sqlBuilder.toString();
	}

	private void addNonStringPredicate(String predicateName, Object predicateValue) {
		if(null != predicateValue){
			if(previousNotNull){
				sqlBuilder.append(Constants.AND);
			}
			sqlBuilder.append(predicateName).append(Constants.EQUALS).append(predicateValue);
			previousNotNull = true;
		}
	}

	private void addStringPredicate(String predicateName, Object predicateValue) {
		if(null != predicateValue){
			if(previousNotNull){
				sqlBuilder.append(Constants.AND);
			}
			sqlBuilder.append(predicateName).append(Constants.LIKE).append(Constants.QUOTE).append(Constants.PERCENT)
				.append(predicateValue).append(Constants.PERCENT).append(Constants.QUOTE);
			previousNotNull = true;
		}
	}
	
	public static void main(String[] args) {
		SqlBuilder sql = new SqlBuilder();
		Movie movie = new Movie();
		movie.setMovieName("JTHJ");
		movie.setMovieBanner("YRF");
		String sqlStr = sql.buildSearchMovieSql(movie);
		System.out.println(sqlStr);
		
		sqlStr = sql.buildSearchCustomerSql(new BeanBuilder().buildCustomerBean(new Long(1000000000), "Siddhi", 
				null, null, null, null, null, null, null, null, null));
		System.out.println(sqlStr);
	}
}
