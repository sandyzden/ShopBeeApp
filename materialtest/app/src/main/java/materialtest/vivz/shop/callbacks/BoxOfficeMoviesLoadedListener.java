package materialtest.vivz.shop.callbacks;

import java.util.ArrayList;

import materialtest.vivz.shop.pojo.Movie;

/**
 * Created by Windows on 02-03-2015.
 */
public interface BoxOfficeMoviesLoadedListener {
    public void onBoxOfficeMoviesLoaded(ArrayList<Movie> listMovies);
}
