import java.io.IOException;

import org.json.simple.parser.ParseException;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileReader;

public class Bacon {
    private static HashMap<String, ArrayList<Movie>> moviesOfCast;

    public static void populateMoviesOfCast(ArrayList<Movie> movies) {
        moviesOfCast = new HashMap<String, ArrayList<Movie>>(); 
        for (Movie m: movies) {
            String[] cast = m.getCast().split("\\|");
            for (String s: cast) {
                moviesOfCast.computeIfAbsent(s, lambda -> new ArrayList<Movie>());
                moviesOfCast.get(s).add(m);
            }
        }
    }

    public static ArrayList<Movie> loadBaconMovies() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        ArrayList<Movie> movies = new ArrayList<Movie>();
        BufferedReader reader = getFile();
        String nextLine = reader.readLine();
        while (nextLine != null) {
            JSONObject movie = (JSONObject) parser.parse(nextLine);
            Movie movieObj = buildMovie(movie);
            if (movieObj != null) movies.add(movieObj);
            nextLine = reader.readLine();
        } 
        reader.close();
        return movies;
    }

    public static int baconSearch(String searchName) {
        if (searchName.equals("Kevin Bacon")) return 0;
        if (moviesOfCast.get(searchName) == null) return 7;
        int c = 1;
        ArrayList<Movie> baconMovies = moviesOfCast.get("Kevin Bacon");
        while (c < 7) {
            ArrayList<String> castMembers = new ArrayList<String>();
            sortByCastAmount(baconMovies);
            for (Movie m: baconMovies) {
                String[] castList = m.getCast().split("\\|");
                boolean found = false;
                for (String s: castList) {
                    boolean alreadyInList = false;
                    for (String s2: castMembers) if (s2.equals(s)) alreadyInList = true;
                    if (!alreadyInList) castMembers.add(s);
                    if (s.equals(searchName)) found = true;
                }
                if (found) {
                    return c;
                }
            }
            baconMovies = new ArrayList<Movie>();
            sortByMovieAmount(castMembers);
            for (String s: castMembers) {
                ArrayList<Movie> moviesToAdd = moviesOfCast.get(s);
                for (Movie m2 : moviesToAdd) {
                    if (!baconMovies.contains(m2)) baconMovies.add(m2);
                }
            }
            c++;
        }
        return c;
    }

    private static void sortByMovieAmount(ArrayList<String> castMembers) {
        int highest = moviesOfCast.get(castMembers.get(0)).size();
        int highestIndex = 0;
        for (int i = 1; i < castMembers.size(); i++) {
            int a = moviesOfCast.get(castMembers.get(i)).size();
            if (a > highest) {
                highest = a;
                highestIndex = i;
            }
            String temp = castMembers.get(i - 1);
            castMembers.set(i - 1, castMembers.get(highestIndex));
            castMembers.set(highestIndex, temp);
        }
    }

    private static void sortByCastAmount(ArrayList<Movie> movies) {
        int highest = movies.get(0).getCast().split("\\|").length;
        int highestIndex = 0;
        for (int i = 1; i < movies.size(); i++) {
            int a = movies.get(i).getCast().split("\\|").length;
            if (a > highest) {
                highest = a;
                highestIndex = i;
            }
            Movie temp = movies.get(i - 1);
            movies.set(i - 1, movies.get(highestIndex));
            movies.set(highestIndex, temp);
        }
    }

    private static Movie buildMovie(JSONObject movie) {
        String title = getTitle(movie);
        if (title == null) return null;
        String castStr = getCast(movie);

        return new Movie(title, castStr, "", "", "", "", 1, "", 1.0, 1, 1);
    }

    private static String getCast(JSONObject movie) {
        String castStr = "";
        JSONArray actorsArray = (JSONArray) movie.get("cast");
        for (int i = 0; i < actorsArray.size(); i++) {
            String actor = actorsArray.get(i).toString();
            actor = actor.replaceAll("\\[", "").replaceAll("\\]", "");
            if (actor.indexOf("|") != -1) {
                String[] actorChoices = actor.split("\\|");
                actor = actorChoices[actorChoices.length - 1];
            }
            actor = actor.strip();
            castStr += "|" + actor;
        }
        if (castStr.equals("")) return castStr;
        return castStr.substring(1);
    }

    private static String getTitle(JSONObject movie) {
        String title = (String) (movie.get("title"));
        int parenthesesIndex = title.indexOf("(");
        if (parenthesesIndex != -1 && parenthesesIndex != 0) {
            title = title.substring(0, title.indexOf("(") - 1);
        }
        if (title.indexOf("Template:") != -1 || title.indexOf("Wikipedia:") != -1) {
            return null;
        }
        return title;
    }

    private static BufferedReader getFile() {
        try {
            return new BufferedReader(new FileReader("data/movie_data.json"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
