package fr.ippon.tatami.repository;

import fr.ippon.tatami.domain.Tweet;

import java.util.Collection;

/**
 * The User Respository.
 *
 * @author Julien Dubois
 */
public interface TweetRepository {

    Tweet createTweet(String login, String content);

    void addTweetToUserline(Tweet tweet);

    void addTweetToTimeline(String login, Tweet tweet);

    Collection<String> getTimeline(String login, int size);

    Tweet findTweetById(String tweetId);
}
