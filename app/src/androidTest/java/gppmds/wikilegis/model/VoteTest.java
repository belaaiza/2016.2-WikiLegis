package gppmds.wikilegis.model;

import org.junit.Test;

import gppmds.wikilegis.exception.VotesException;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by thiago on 9/20/16.
 */
public class VoteTest {

    @Test
        public void testCreateVotes(){
        boolean isValid = true;

        try {
            Vote vote = new Vote(22, 1, 1, true);
        } catch (VotesException votesException) {
            isValid = false;
        }

        assertTrue(isValid);
    }
    @Test
    public void testUserIdIsNull() {
        boolean isValid = true;

        try {
            Vote vote = new Vote(null, 1, 1, true);
        } catch (VotesException votesException) {
            isValid = false;
        }

        assertFalse(isValid);

    }

    @Test
    public void testContetTypeisNull() {
        boolean isValid = true;

        try {
            Vote vote = new Vote(1, null, 1, true);
        } catch (VotesException votesException) {
            isValid = false;
        }

        assertFalse(isValid);

    }

    @Test
    public void testObjectIdisNull() {
        boolean isValid = true;

        try {
            Vote vote = new Vote(1, 1, null, true);
        } catch (VotesException votesException) {
            isValid = false;
        }

        assertFalse(isValid);

    }

    @Test
    public void testVoteIsFalse() {
        boolean isValid = true;

        try {
            Vote vote = new Vote(1, 1, 1,false);
        } catch (VotesException votesException) {
            isValid = false;
        }

        assertTrue(isValid);

    }

    @Test
    public void testVoteIsTrue() {
        boolean isValid = true;

        try {
            Vote vote = new Vote(1, 1, 1, true);
        } catch (VotesException votesException) {
            isValid = false;
        }

        assertTrue(isValid);

    }

}