package fr.ippon.tatami.service;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.CounterRepository;
import fr.ippon.tatami.repository.FollowerRepository;
import fr.ippon.tatami.repository.TweetRepository;

/**
 * Manages the the timeline.
 *
 * @author Julien Dubois
 */
@Service
public class TimelineService {

    private final Log log = LogFactory.getLog(TimelineService.class);

    @Inject
    private UserService userService;

    @Inject
    private TweetRepository tweetRepository;

    @Inject
    private CounterRepository counterRepository;

    @Inject
    private FollowerRepository followerRepository;

    public void postTweet(String content) {
        if (log.isDebugEnabled()) {
            log.debug("Creating new tweet : " + content);
        }
        User currentUser = userService.getCurrentUser();

        Tweet tweet = tweetRepository.createTweet(currentUser.getLogin(), content);
        tweetRepository.addTweetToUserline(tweet);
        tweetRepository.addTweetToTimeline(currentUser.getLogin(), tweet);
        for (String followerLogin : followerRepository.findFollowersForUser(currentUser.getLogin())) {
            tweetRepository.addTweetToTimeline(followerLogin, tweet);
        }
        counterRepository.incrementTweetCounter(currentUser.getLogin());
    }

    public Collection<Tweet> getTimeline(int nbTweets) {
        User currentUser = userService.getCurrentUser();
        Collection<String> tweetIds = tweetRepository.getTimeline(currentUser.getLogin(), nbTweets);

        Collection<Tweet> tweets = new ArrayList<Tweet>(tweetIds.size());
        for (String tweedId : tweetIds) {
            Tweet tweet = tweetRepository.findTweetById(tweedId);
            User tweetUser = userService.getUserByLogin(tweet.getLogin());
            tweet.setFirstName(tweetUser.getFirstName());
            tweet.setLastName(tweetUser.getLastName());
            tweet.setGravatar(tweetUser.getGravatar());
            tweets.add(tweet);
        }
        return tweets;
    }
}
