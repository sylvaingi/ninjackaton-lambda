package com.ninja_squad.training.lambda;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * Le TP Lambda
 * @author JB
 */
public class TP {

    /**
     * Extrayez une List<String> qui contient les senders des tweets
     */
    public static void step1() {
        System.out.println(Tweet.TWEETS.stream().map(t->t.getDate()).collect(Collectors.toList()));
    }

    /**
     * Faites la même chose, sans appeler getDate() ni System.out.println()
     */
    public static void step2() {
        Tweet.TWEETS.stream().map(Tweet::getDate).forEach(System.out::println);
    }

    /**
     * Extrayez une List<String> qui contient les senders des tweets
     */
    public static List<String> step3() {
        return Tweet.TWEETS.stream().map(Tweet::getSender).collect(Collectors.toList());
    }

    /**
     * Extrayez une List<String> qui contient les senders des tweets, sans duplicata
     */
    public static List<String> step4() {
        return step3().stream().distinct().collect(Collectors.toList());
    }

    /**
     * Extrayez une List<String> qui contient les senders des tweets, sans duplicata, triés par ordre alphabétique
     */
    public static List<String> step5() {
        return step4().stream().sorted().collect(Collectors.toList());
    }

    /**
     * Extrayez une List<Tweet> qui contient les tweets contenant le hashtag #lambda
     */
    public static List<Tweet> step6() {
        return Tweet.TWEETS.stream().filter(t -> t.containsHashTag("#lambda")).collect(Collectors.toList());
    }

    /**
     * Extrayez une List<Tweet> qui contient les tweets contenant le hashtag #lambda, triés par sender puis par date
     */
    public static List<Tweet> step7() {
        return step6().stream()
                .sorted(
                        Comparators.
                        <Tweet, String>comparing(Tweet::getSender).
                        thenComparing(Tweet::getDate)
                   ).collect(Collectors.toList());
    }

    /**
     * Extrayez un Set<String> qui contient l'ensemble des hash tags des tweets
     */
    public static Set<String> step8() {
        return Tweet.TWEETS.stream()
                .map(Tweet::getHashTags)
                .explode((Stream.Downstream<String> downstream, Set<String> tags) -> downstream.send(tags))
                .collect(Collectors.toSet());
    }

    /**
     * Créez une Map<String, List<Tweet>> qui contient, pour chaque sender, les tweets envoyés par ce sender
     */
    public static Map<String, List<Tweet>> step9() {
        return Tweet.TWEETS.stream()
                .collect(Collectors.groupingBy(
                            Tweet::getSender,
                            HashMap::new,
                            ArrayList::new
                        )
                );
    }

    /**
     * Extrayez deux listes: les tweets qui contiennent le hash tag #lambda, et ceux qui ne les contiennent pas.
     */
    public static Map<Boolean, List<Tweet>> step10() {
        return Tweet.TWEETS.stream()
                .collect(Collectors.groupingBy(
                            t->t.containsHashTag("lambda"),
                            HashMap::new,
                            ArrayList::new
                        )
                );
    }

    public static class Stats {

        int tweetCount ;
        int charCount ;

        public Stats(){

        }

        public Stats (Tweet t){
            setTweet(t);
        }

        public void setTweet(Tweet t){
            tweetCount = 1;
            charCount = t.getText().length();
        }

        public Stats addStats(Stats s){
            tweetCount += s.tweetCount;
            charCount += s.charCount;

            return this;
        }

        public Stats addTweet(Tweet t){
            tweetCount ++;
            charCount += t.getText().length();

            return this;
        }

        public int getAverage() {
            return charCount / tweetCount;
        }

        public int getTotal() {
            return charCount;
        }
    }

    /**
     * Calculez le total et la moyenne du nombre de caractères des textes des tweets.
     * Hints:
     *     Créez une class Stats
     *     Utilisez stream.collect(..., ..., ...) ou stream.map(...).reduce(...)
     */
    public static Stats step11() {
        return Tweet.TWEETS.stream()
                .collect(
                        Stats::new,
                        (s, t) -> s.addTweet(t),
                        (s1, s2) -> s1.addStats(s2)
                );

        /*return Tweet.TWEETS.stream()
                .map(t-> new Stats(t))
                .reduce((s1, s2) -> s1.addStats(s2))
                .get();*/
    }

    /**
     * Faites la même chose, mais de manière parrallèle
     */
    public static Stats step12() {
        return Tweet.TWEETS.parallelStream()
                .map(t-> new Stats(t))
                .reduce((s1, s2) -> s1.addStats(s2))
                .get();
    }
}
