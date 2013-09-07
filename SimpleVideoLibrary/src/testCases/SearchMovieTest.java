/**
 * 
 */
package testCases;

import java.sql.Connection;
import java.sql.Savepoint;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import services.DbOperations;
import beans.Movie;
import builders.BeanBuilder;

/**
 * @author Siddhi Chogle
 *
 */
public class SearchMovieTest {
	
	private Movie movie;
	private BeanBuilder beanBuilder;
	private Connection con;
	private DbOperations db;
	private Savepoint savepoint;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		beanBuilder = new BeanBuilder();
		db = DbOperations.getInstance();
		con = db.getConnection();
		con.setAutoCommit(false);
		savepoint = con.setSavepoint();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		movie = null;
		beanBuilder = null;
		con.rollback(savepoint);
		con.close();
		db = null;
		con = null;
	}

	/**
	 * Test method for {@link services.DbOperations#searchMovie(beans.Movie)}.
	 */
	@Test
	public void testSearchMovieForNoSearchCriteria() {
		//initialize movie bean
		movie = beanBuilder.buildMovieBean(null, null, null, null, null, null, null, null, null);
		
		//call method
		Movie[] movies = db.searchMovie(movie);
		
		//assert
		Assert.assertEquals("Cloud Atlas", movies[0].getMovieName());
		for(Movie movie : movies){
			System.out.println(movie.getMovieName());
		}
	}

}
